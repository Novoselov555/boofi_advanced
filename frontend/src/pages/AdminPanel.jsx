import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminPanel.css';
import UsersTab from '../components/UsersTab';

export default function AdminPanel() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('bookings');

    useEffect(() => {
        const userRole = localStorage.getItem('userRole');
        if (userRole !== 'ADMIN') {
            navigate('/dashboard');
        }
    }, [navigate]);

    return (
        <div className="admin-panel">
            <header className="admin-header">
                <h1>Панель администратора</h1>
                <button onClick={() => navigate('/dashboard')} className="back-btn">
                    Вернуться на главную
                </button>
            </header>

            <div className="admin-content">
                <div className="admin-tabs">
                    <button
                        className={`tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
                        onClick={() => setActiveTab('bookings')}
                    >
                        Бронирования
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'users' ? 'active' : ''}`}
                        onClick={() => setActiveTab('users')}
                    >
                        Пользователи
                    </button>
                    <button
                        className={`tab-btn ${activeTab === 'places' ? 'active' : ''}`}
                        onClick={() => setActiveTab('places')}
                    >
                        Помещения
                    </button>
                </div>

                <div className="tab-content">
                    {activeTab === 'bookings' && (
                        <div className="bookings-tab">
                            <h2>Управление бронированиями</h2>
                            {/* Здесь будет список бронирований */}
                        </div>
                    )}

                    {activeTab === 'users' && (
                        <UsersTab />
                    )}

                    {activeTab === 'places' && (
                        <div className="places-tab">
                            <h2>Управление помещениями</h2>
                            {/* Здесь будет список помещений */}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}