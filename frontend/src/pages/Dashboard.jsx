import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

export default function Dashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [coworkings, setCoworkings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

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
                setUser(userData);

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
                setCoworkings(coworkingsData);

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

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>Коворкинг</h1>
                <div className="header-actions">
                    <span className="user-info">Привет, {user?.firstName || user?.email || 'Пользователь'}!</span>
                    <button onClick={() => navigate('/my-bookings')}>Мои брони</button>
                    {user?.role === 'ADMIN' && (
                        <button onClick={() => navigate('/admin')}>Админ панель</button>
                    )}
                    <button onClick={handleLogout} className="logout-btn">Выйти</button>
                </div>
            </header>
            <main className="dashboard-main dashboard-main--single">
                <div className="places-section places-section--single">
                    <h2>Выберите коворкинг</h2>
                    {coworkings.length > 0 ? (
                        <div className="places-grid places-grid--single">
                            {coworkings.map(coworking => (
                                <div
                                    key={coworking.id}
                                    className="place-card place-card--big"
                                    onClick={() => navigate(`/coworking/${coworking.id}`)}
                                >
                                    <h3>{coworking.name}</h3>
                                    <p>{coworking.description}</p>

                                    <button className="choose-btn">Выбрать</button>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <p>Нет доступных коворкингов.</p>
                    )}
                </div>
            </main>
        </div>
    );
}
