import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

const ROOMS = [
    { id: 1, name: 'Переговорка 4 этаж', description: 'Уютная переговорная на 4 этаже.' },
    { id: 2, name: 'Переговорка 5 этаж', description: 'Современная переговорная на 5 этаже.' },
    { id: 3, name: 'Коворкинг VK', description: 'Открытое пространство для работы и встреч.' },
    { id: 4, name: 'Коворкинг Cosmos', description: 'Стильный коворкинг с панорамными окнами.' },
];

export default function Dashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);

    useEffect(() => {
        const authHeader = localStorage.getItem('authHeader');
        const userRole = localStorage.getItem('userRole');

        console.log('User role from localStorage:', userRole);

        if (!authHeader) {
            navigate('/auth/login');
            return;
        }

        setUser({
            name: 'Пользователь',
            role: userRole
        });
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('authHeader');
        localStorage.removeItem('userRole');
        navigate('/auth/login');
    };

    if (!user) {
        return <div>Loading...</div>;
    }

    console.log('Current user role:', user.role);

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>Коворкинг</h1>
                <div className="header-actions">
                    <span className="user-info">Привет, {user.name}!</span>
                    <button onClick={() => navigate('/my-bookings')}>Мои брони</button>
                    {user.role === 'ADMIN' && (
                        <button onClick={() => navigate('/admin')}>Админ панель</button>
                    )}
                    <button onClick={handleLogout} className="logout-btn">Выйти</button>
                </div>
            </header>
            <main className="dashboard-main dashboard-main--single">
                <div className="places-section places-section--single">
                    <h2>Выберите помещение</h2>
                    <div className="places-grid places-grid--single">
                        {ROOMS.map(room => (
                            <div
                                key={room.id}
                                className="place-card place-card--big"
                                onClick={() => navigate(`/booking/${room.id}`)}
                            >
                                <h3>{room.name}</h3>
                                <p>{room.description}</p>
                                <button className="choose-btn">Забронировать</button>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
        </div>
    );
}
