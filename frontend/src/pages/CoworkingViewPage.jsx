import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './CoworkingViewPage.css';

const API_BASE_URL = 'http://localhost:8080';

export default function CoworkingViewPage() {
    const { coworkingId } = useParams();
    const navigate = useNavigate();
    const [coworkingData, setCoworkingData] = useState(null);
    const [placeToBook, setPlaceToBook] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [bookingStartTime, setBookingStartTime] = useState('');
    const [bookingEndTime, setBookingEndTime] = useState('');
    const [bookingMessage, setBookingMessage] = useState({ type: '', text: '' });

    const [existingBookings, setExistingBookings] = useState([]);
    const [isLoadingExistingBookings, setIsLoadingExistingBookings] = useState(false);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchCoworkingData = async () => {
            setLoading(true);
            setError('');
            const authHeader = localStorage.getItem('authHeader');

            if (!authHeader) {
                navigate('/auth/login');
                setLoading(false);
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/coworkings/${coworkingId}`, {
                    headers: { 'Authorization': authHeader },
                });

                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        localStorage.removeItem('authHeader');
                        localStorage.removeItem('userRole');
                        navigate('/auth/login');
                        throw new Error(`Ошибка авторизации: ${response.status}`);
                    }
                    const errorData = await response.json().catch(() => ({ message: 'Ошибка при получении данных коворкинга' }));
                    throw new Error(errorData.message || `Ошибка: ${response.status}`);
                }
                const data = await response.json();
                setCoworkingData(data);
            } catch (err) {
                console.error("Ошибка при загрузке данных коворкинга:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (coworkingId) {
            fetchCoworkingData();
        }
    }, [coworkingId, navigate]);

    const fetchExistingBookings = useCallback(async (placeId) => {
        setIsLoadingExistingBookings(true);
        setExistingBookings([]);
        const authHeader = localStorage.getItem('authHeader');
        if (!authHeader) {
            setIsLoadingExistingBookings(false);
            return;
        }
        try {
            const response = await fetch(`${API_BASE_URL}/bookings/${placeId}`, {
                headers: { 'Authorization': authHeader },
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ error: `Ошибка загрузки броней: ${response.statusText} (${response.status})` }));
                console.error("Ошибка при загрузке существующих бронирований:", errorData.message || errorData.error);
                setExistingBookings([]);
            } else {
                const data = await response.json();
                setExistingBookings(data || []);
            }
        } catch (err) {
            console.error("Исключение при запросе существующих бронирований:", err);
            setExistingBookings([]);
        } finally {
            setIsLoadingExistingBookings(false);
        }
    }, [navigate]);

    const handlePlaceClick = (place) => {
        setPlaceToBook(place);
        setIsModalOpen(true);
        setBookingStartTime('');
        setBookingEndTime('');
        setBookingMessage({ type: '', text: '' });
        fetchExistingBookings(place.id);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setPlaceToBook(null);
        setBookingStartTime('');
        setBookingEndTime('');
    };

    const handleBookingSubmit = async () => {
        if (!placeToBook || !bookingStartTime || !bookingEndTime) {
            setBookingMessage({ type: 'error', text: 'Пожалуйста, выберите время начала и окончания бронирования.' });
            return;
        }
        if (new Date(bookingEndTime) <= new Date(bookingStartTime)) {
            setBookingMessage({ type: 'error', text: 'Время окончания должно быть позже времени начала.' });
            return;
        }
        const authHeader = localStorage.getItem('authHeader');
        if (!authHeader) {
            navigate('/auth/login');
            return;
        }
        const bookingDto = {
            timeStart: bookingStartTime,
            timeEnd: bookingEndTime,
        };
        setBookingMessage({ type: 'loading', text: 'Идет бронирование...' });
        try {
            const response = await fetch(`${API_BASE_URL}/bookings/${placeToBook.id}`, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bookingDto),
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ error: `Ошибка бронирования: ${response.statusText} (${response.status})` }));
                throw new Error(errorData.message || errorData.error || `Статус: ${response.status}`);
            }
            await response.json();
            setBookingMessage({ type: 'success', text: `Место "${placeToBook.name}" успешно забронировано!` });
            fetchExistingBookings(placeToBook.id);
            setTimeout(() => {
            }, 3000);
        } catch (err) {
            console.error("Ошибка при бронировании:", err);
            setBookingMessage({ type: 'error', text: err.message || 'Не удалось выполнить бронирование.' });
        }
    };

    if (loading) return <div className="cvp-status-message">Загрузка данных коворкинга...</div>;
    if (error) return <div className="cvp-status-message cvp-error-message">Ошибка: {error}</div>;
    if (!coworkingData) return <div className="cvp-status-message">Данные о коворкинге не найдены.</div>;

    return (
        <div className="cvp-page">
            <header className="cvp-header-card">
                <h1>{coworkingData.name}</h1>
                <p className="cvp-description">{coworkingData.description}</p>
                <button onClick={() => navigate('/dashboard')} className="cvp-back-button">
                    Назад к коворкингам
                </button>
            </header>

            <div className="cvp-room-layout-container">
                <div className="cvp-room-label cvp-room-label-window">Окно</div>
                <div className="cvp-room-label cvp-room-label-entrance">Вход</div>
                <h2 className="cvp-room-layout-title">Схема мест</h2>
                {coworkingData.places && coworkingData.places.length > 0 ? (
                    <div className="cvp-places-grid">
                        {coworkingData.places.map(place => (
                            <div
                                key={place.id}
                                className="cvp-place-circle"
                                onClick={() => handlePlaceClick(place)}
                                title={place.description || place.name}
                            >
                                <span className="cvp-place-name">{place.name}</span>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="cvp-no-places-message">В этом коворкинге пока нет доступных мест.</p>
                )}
            </div>

            {isModalOpen && placeToBook && (
                <div className="cvp-modal-overlay">
                    <div className="cvp-modal">
                        <h2>Бронирование места: {placeToBook.name}</h2>
                        <p>Коворкинг: {coworkingData?.name}</p>

                        <div className="cvp-existing-bookings-section">
                            {isLoadingExistingBookings && <p className="cvp-loading-existing">Загрузка информации о бронированиях...</p>}
                            {!isLoadingExistingBookings && existingBookings.length > 0 && (
                                <div className="cvp-existing-bookings">
                                    <h4>Это место уже забронировано:</h4>
                                    <ul>
                                        {existingBookings.map(booking => (
                                            <li key={booking.id}>
                                                C: {new Date(booking.timeStart).toLocaleString()} - По: {new Date(booking.timeEnd).toLocaleString()}
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                            {!isLoadingExistingBookings && existingBookings.length === 0 && (
                                <p className="cvp-no-existing-bookings">На данный момент нет активных бронирований для этого места.</p>
                            )}
                        </div>

                        {bookingMessage.text && (
                            <div className={`cvp-booking-message cvp-booking-${bookingMessage.type}`}>
                                {bookingMessage.text}
                            </div>
                        )}

                        {bookingMessage.type !== 'success' && (
                            <>
                                <div className="cvp-form-group">
                                    <label htmlFor="startTime">Время начала:</label>
                                    <input
                                        type="datetime-local"
                                        id="startTime"
                                        value={bookingStartTime}
                                        onChange={(e) => setBookingStartTime(e.target.value)}
                                        className="cvp-datetime-input"
                                    />
                                </div>
                                <div className="cvp-form-group">
                                    <label htmlFor="endTime">Время окончания:</label>
                                    <input
                                        type="datetime-local"
                                        id="endTime"
                                        value={bookingEndTime}
                                        onChange={(e) => setBookingEndTime(e.target.value)}
                                        className="cvp-datetime-input"
                                    />
                                </div>
                                <div className="cvp-modal-actions">
                                    <button onClick={handleBookingSubmit} className="cvp-button-confirm" disabled={bookingMessage.type === 'loading'}>
                                        {bookingMessage.type === 'loading' ? 'Бронируем...' : 'Забронировать'}
                                    </button>
                                    <button onClick={handleCloseModal} className="cvp-button-cancel" disabled={bookingMessage.type === 'loading'}>
                                        Отмена
                                    </button>
                                </div>
                            </>
                        )}
                        {bookingMessage.type === 'success' && (
                            <div className="cvp-modal-actions">
                                <button onClick={handleCloseModal} className="cvp-button-confirm">
                                    Закрыть
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}