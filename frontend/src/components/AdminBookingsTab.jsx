import React, { useEffect, useState, useCallback } from 'react';
import '../pages/AdminPanel.css'; // Используем те же стили для общего вида

const API_BASE_URL = 'http://localhost:8080';

export default function AdminBookingsTab() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editingBooking, setEditingBooking] = useState(null);
    const [editForm, setEditForm] = useState({ timeStart: '', timeEnd: '' });
    const [editMessage, setEditMessage] = useState({ type: '', text: '' });

    const authHeader = localStorage.getItem('authHeader');

    const fetchBookings = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/bookings`, {
                headers: { 'Authorization': authHeader },
            });
            if (!response.ok) {
                const errData = await response.json().catch(() => ({ message: `Ошибка загрузки бронирований: ${response.status}` }))
                throw new Error(errData.message);
            }
            const data = await response.json();
            setBookings(data || []);
        } catch (err) {
            console.error("Ошибка при загрузке бронирований:", err);
            setError(err.message || 'Не удалось загрузить бронирования.');
        } finally {
            setLoading(false);
        }
    }, [authHeader]);

    useEffect(() => {
        fetchBookings();
    }, [fetchBookings]);

    const formatDateTimeLocal = (dateTimeString) => {
        if (!dateTimeString) return '';
        // LocalDateTime от Spring обычно приходит как 'YYYY-MM-DDTHH:MM:SS'.
        // Для <input type="datetime-local"> нужен формат 'YYYY-MM-DDTHH:MM'.
        if (typeof dateTimeString === 'string' && dateTimeString.includes('T') && dateTimeString.length >= 16) {
            return dateTimeString.slice(0, 16);
        }
        // Если формат другой, можно добавить более сложную логику или вернуть ошибку/исходную строку
        console.warn("Не удалось отформатировать дату для datetime-local из:", dateTimeString, " Возвращается исходное значение.");
        return dateTimeString;
    };

    const handleEditClick = (booking) => {
        setEditingBooking(booking);
        setEditForm({
            timeStart: formatDateTimeLocal(booking.timeStart),
            timeEnd: formatDateTimeLocal(booking.timeEnd),
        });
        setEditMessage({ type: '', text: '' });
    };

    const handleEditFormChange = (e) => {
        setEditForm({ ...editForm, [e.target.name]: e.target.value });
    };

    const handleEditFormSubmit = async (e) => {
        e.preventDefault();
        if (!editingBooking) return;

        if (!editForm.timeStart || !editForm.timeEnd) {
            setEditMessage({ type: 'error', text: 'Пожалуйста, выберите время начала и окончания.' });
            return;
        }
        if (new Date(editForm.timeEnd) <= new Date(editForm.timeStart)) {
            setEditMessage({ type: 'error', text: 'Время окончания должно быть позже времени начала.' });
            return;
        }

        setEditMessage({ type: 'loading', text: 'Обновление бронирования...' });

        const bookingDto = {
            timeStart: editForm.timeStart, // Отправляем в формате YYYY-MM-DDTHH:MM
            timeEnd: editForm.timeEnd,
        };

        try {
            const response = await fetch(`${API_BASE_URL}/admin/booking/${editingBooking.id}`, {
                method: 'PATCH',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bookingDto),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Ошибка обновления: ${response.status}` }));
                throw new Error(errorData.message || 'Не удалось обновить бронирование.');
            }
            const updatedBooking = await response.json();
            setBookings(prevBookings =>
                prevBookings.map(b => b.id === updatedBooking.id ? updatedBooking : b)
            );
            setEditMessage({ type: 'success', text: 'Бронирование успешно обновлено!' });
            setTimeout(() => {
                setEditingBooking(null);
            }, 2000);
        } catch (err) {
            console.error("Ошибка при обновлении бронирования:", err);
            setEditMessage({ type: 'error', text: err.message });
        }
    };

    if (loading) return <div className="admin-tab-content">Загрузка бронирований...</div>;
    if (error) return <div className="admin-tab-content"><div className="admin-message admin-message-error">{error}</div></div>;

    return (
        <div className="admin-tab-content">
            <h2>Управление бронированиями</h2>
            {bookings.length === 0 && !loading && <p>Нет доступных бронирований.</p>}
            {bookings.length > 0 && (
                <table className="admin-table">
                    <thead>
                    <tr>
                        <th>ID Брони</th>
                        <th>ID Пользователя</th>
                        <th>ID Места</th>
                        <th>Начало</th>
                        <th>Окончание</th>
                        <th>Статус</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {bookings.map(booking => (
                        <tr key={booking.id}>
                            <td>{booking.id}</td>
                            <td>{booking.user ? booking.user.id : 'N/A'}</td>
                            <td>{booking.place ? booking.place.id : 'N/A'}</td>
                            <td>{new Date(booking.timeStart).toLocaleString()}</td>
                            <td>{new Date(booking.timeEnd).toLocaleString()}</td>
                            <td>{booking.status}</td>
                            <td>
                                <button onClick={() => handleEditClick(booking)} className="edit-btn">
                                    Изменить время
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            {editingBooking && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal">
                        <h3>Изменить время бронирования #{editingBooking.id}</h3>
                        {editMessage.text && (
                            <div className={`admin-message admin-message-${editMessage.type}`}>
                                {editMessage.text}
                            </div>
                        )}
                        <form onSubmit={handleEditFormSubmit}>
                            <div className="admin-form-group">
                                <label htmlFor="timeStart">Новое время начала:</label>
                                <input
                                    type="datetime-local"
                                    id="timeStart"
                                    name="timeStart"
                                    value={editForm.timeStart}
                                    onChange={handleEditFormChange}
                                    required
                                    className="admin-datetime-input"
                                />
                            </div>
                            <div className="admin-form-group">
                                <label htmlFor="timeEnd">Новое время окончания:</label>
                                <input
                                    type="datetime-local"
                                    id="timeEnd"
                                    name="timeEnd"
                                    value={editForm.timeEnd}
                                    onChange={handleEditFormChange}
                                    required
                                    className="admin-datetime-input"
                                />
                            </div>
                            <div className="admin-modal-actions">
                                <button type="submit" className="admin-button-confirm" disabled={editMessage.type === 'loading'}>
                                    {editMessage.type === 'loading' ? 'Сохранение...' : 'Сохранить'}
                                </button>
                                <button type="button" onClick={() => setEditingBooking(null)} className="admin-button-cancel" disabled={editMessage.type === 'loading'}>
                                    Отмена
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}