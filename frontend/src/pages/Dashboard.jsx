import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

// Удаляем захардкоженный массив ROOMS
// const ROOMS = [
//     { id: 1, name: 'Переговорка 4 этаж', description: 'Уютная переговорная на 4 этаже.' },
//     { id: 2, name: 'Переговорка 5 этаж', description: 'Современная переговорная на 5 этаже.' },
//     { id: 3, name: 'Коворкинг VK', description: 'Открытое пространство для работы и встреч.' },
//     { id: 4, name: 'Коворкинг Cosmos', description: 'Стильный коворкинг с панорамными окнами.' },
// ];

export default function Dashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [coworkings, setCoworkings] = useState([]); // Для хранения данных о коворкингах и их местах
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // URL API (аналогично Login.jsx, лучше вынести в конфигурацию)
    const API_BASE_URL = 'http://localhost:8080';

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            setError('');
            const authHeader = localStorage.getItem('authHeader');

            if (!authHeader) {
                navigate('/auth/login');
                setLoading(false);
                return;
            }

            try {
                // Запрос данных пользователя
                const userRes = await fetch(`${API_BASE_URL}/user/profile/me`, {
                    headers: {
                        'Authorization': authHeader,
                    },
                });
                if (!userRes.ok) {
                    if (userRes.status === 401 || userRes.status === 403) {
                        throw new Error(`AuthError: ${userRes.status}`);
                    }
                    const userData = await userRes.json();
                    throw new Error(userData.message || `Ошибка загрузки профиля: ${userRes.status}`);
                }
                const userData = await userRes.json();
                setUser(userData); // Ожидается, что userData содержит firstName и role (как строку)

                // Запрос списка коворкингов
                const coworkingsRes = await fetch(`${API_BASE_URL}/coworkings`, {
                    headers: {
                        'Authorization': authHeader,
                    },
                });
                if (!coworkingsRes.ok) {
                    if (coworkingsRes.status === 401 || coworkingsRes.status === 403) {
                        throw new Error(`AuthError: ${coworkingsRes.status}`);
                    }
                    const coworkingsData = await coworkingsRes.json();
                    throw new Error(coworkingsData.message || `Ошибка загрузки коворкингов: ${coworkingsRes.status}`);
                }
                const coworkingsData = await coworkingsRes.json();
                setCoworkings(coworkingsData); // Ожидается, что это массив объектов коворкингов

            } catch (err) {
                console.error("Ошибка при загрузке данных дэшборда:", err);
                if (err.message.startsWith('AuthError')) {
                    localStorage.removeItem('authHeader');
                    localStorage.removeItem('userRole');
                    navigate('/auth/login');
                } else {
                    setError(err.message);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('authHeader');
        localStorage.removeItem('userRole');
        navigate('/auth/login');
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div className="error-message" style={{ margin: '20px' }}>Ошибка: {error}</div>;
    }

    // Получаем все места из всех коворкингов
    // Ожидаем, что каждый объект coworking в массиве coworkings имеет свойство 'places', которое является массивом мест
    // Каждое место (place) должно иметь 'id', 'name', 'description'
    const allPlaces = coworkings.flatMap(coworking =>
        (coworking.places || []).map(place => ({
            ...place,
            coworkingName: coworking.name // Добавляем имя коворкинга для возможного использования
        }))
    );

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>Коворкинг</h1>
                <div className="header-actions">
                    {/* Используем user.firstName или user.email как запасной вариант */}
                    <span className="user-info">Привет, {user?.firstName || user?.email || 'Пользователь'}!</span>
                    <button onClick={() => navigate('/my-bookings')}>Мои брони</button>
                    {/* Проверяем user.role (ожидаем строку 'ADMIN') */}
                    {user?.role === 'ADMIN' && (
                        <button onClick={() => navigate('/admin')}>Админ панель</button>
                    )}
                    <button onClick={handleLogout} className="logout-btn">Выйти</button>
                </div>
            </header>
            <main className="dashboard-main dashboard-main--single">
                <div className="places-section places-section--single">
                    <h2>Выберите помещение</h2>
                    {allPlaces.length > 0 ? (
                        <div className="places-grid places-grid--single">
                            {allPlaces.map(place => (
                                <div
                                    key={place.id}
                                    className="place-card place-card--big"
                                    onClick={() => navigate(`/booking/${place.id}`)} // Передаем ID места
                                >
                                    <h3>{place.name}</h3>
                                    <p>{place.description}</p>
                                    {/* Можно добавить имя коворкинга, если нужно: <p>Из: {place.coworkingName}</p> */}
                                    <button className="choose-btn">Забронировать</button>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p>Нет доступных помещений для бронирования.</p>
                    )}
                </div>
            </main>
        </div>
    );
}
