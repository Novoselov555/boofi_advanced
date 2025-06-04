import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import './MyBookingsPage.css'; // Создадим этот CSS файл позже

const API_BASE_URL = 'http://localhost:8080';

export default function MyBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [message, setMessage] = useState({ type: '', text: '' }); // Для сообщений об успехе/ошибке отмены
    const navigate = useNavigate();
    const authHeader = localStorage.getItem('authHeader');

    const fetchUserBookings = useCallback(async () => {
        setLoading(true);
        setError('');
        setMessage({ type: '', text: '' });

        if (!authHeader) {
            navigate('/auth/login');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/user/bookings`, {
                headers: { 'Authorization': authHeader },
            });
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    localStorage.removeItem('authHeader');
                    localStorage.removeItem('userRole');
                    navigate('/auth/login');
                    throw new Error('Сессия истекла или нет прав. Пожалуйста, войдите снова.');
                }
                const errData = await response.json().catch(() => ({ message: `Ошибка загрузки бронирований: ${response.status}` }));
                throw new Error(errData.message);
            }
            const data = await response.json();
            setBookings(data || []);
        } catch (err) {
            console.error("Ошибка при загрузке бронирований пользователя:", err);
            setError(err.message || 'Не удалось загрузить ваши бронирования.');
        } finally {
            setLoading(false);
        }
    }, [authHeader, navigate]);

    useEffect(() => {
        fetchUserBookings();
    }, [fetchUserBookings]);

    const handleCancelBooking = async (bookingId) => {
        if (!window.confirm('Вы уверены, что хотите отменить это бронирование?')) return;

        setMessage({ type: 'loading', text: 'Отмена бронирования...' });
        setError('');

        if (!authHeader) {
            navigate('/auth/login');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/user/bookings/cancel/${bookingId}`, {
                method: 'PATCH',
                headers: { 'Authorization': authHeader },
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Ошибка отмены: ${response.status}` }));
                throw new Error(errorData.message || 'Не удалось отменить бронирование.');
            }
            // const updatedBooking = await response.json(); // Бэкенд возвращает обновленное бронирование
            setMessage({ type: 'success', text: 'Бронирование успешно отменено!' });
            // Обновляем список бронирований, чтобы отразить изменение статуса
            fetchUserBookings(); // или можно обновить статус локально:
            // setBookings(prev => prev.map(b => b.id === bookingId ? {...b, status: updatedBooking.status} : b));
        } catch (err) {
            console.error("Ошибка при отмене бронирования:", err);
            setMessage({ type: 'error', text: err.message });
        }
    };

    if (loading) return <div className="my-bookings-page status-message">Загрузка ваших бронирований...</div>;
    if (error) return <div className="my-bookings-page status-message error-message">{error}</div>;

    return (
        <div className="my-bookings-page">
            <h1>Мои бронирования</h1>

            {message.text && (
                <div className={`status-message message-${message.type}`}>
                    {message.text}
                </div>
            )}

            {bookings.length === 0 && !loading && (
                <p>У вас пока нет бронирований.</p>
            )}

            {bookings.length > 0 && (
                <table className="bookings-table">
                    <thead>
                    <tr>
                        <th>ID Брони</th>
                        {/* <th>Место</th> Пока не можем отобразить название/ID места */}
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
                            {/* <td>{booking.place ? booking.place.name : 'N/A'}</td> */}
                            <td>{new Date(booking.timeStart).toLocaleString()}</td>
                            <td>{new Date(booking.timeEnd).toLocaleString()}</td>
                            <td>{booking.status}</td>
                            <td>
                                {booking.status === 'BOOKED' && ( // Показываем кнопку отмены только для активных броней
                                    <button
                                        onClick={() => handleCancelBooking(booking.id)}
                                        className="cancel-btn"
                                    >
                                        Отменить
                                    </button>
                                )}
                                {/* Здесь можно будет добавить кнопку "Изменить время" */}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
            <button onClick={() => navigate('/dashboard')} className="back-to-dashboard-btn">
                К выбору коворкингов
            </button>
        </div>
    );
}
