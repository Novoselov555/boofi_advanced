import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminPanel.css'; // Подключаем наши основные стили
import UsersTab from '../components/UsersTab';
import AdminBookingsTab from '../components/AdminBookingsTab';

export default function AdminPanel() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('users');

    useEffect(() => {
        const userRole = localStorage.getItem('userRole');
        const authHeader = localStorage.getItem('authHeader');

        if (userRole !== 'ADMIN' || !authHeader) {

            navigate('/auth/login');
        }
    }, [navigate]);

    const renderTabContent = () => {
        switch (activeTab) {
            case 'users':
                return <UsersTab />;
            case 'bookings':
                return <AdminBookingsTab />;
            case 'places':
                return (
                    <div className="admin-tab-content">
                        <h2>Управление помещениями и местами</h2>
                        <p>Этот раздел находится в разработке.</p>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="admin-panel">
            <header className="admin-header">
                <h1>Панель администратора</h1>
                <button onClick={() => navigate('/dashboard')} className="back-btn">
                    На главную (Dashboard)
                </button>
            </header>

            <div className="admin-main-content">
                <div className="admin-tabs">
                    <button
                        className={`tab-btn ${activeTab === 'users' ? 'active' : ''}`}
                        onClick={() => setActiveTab('users')}
                    >
                        Пользователи
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
                        onClick={() => setActiveTab('bookings')}
                    >
                        Бронирования
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'places' ? 'active' : ''}`}
                        onClick={() => setActiveTab('places')}
                    >
                        Помещения
                    </button>
                </div>

                {renderTabContent()}
            </div>
        </div>
    );
}