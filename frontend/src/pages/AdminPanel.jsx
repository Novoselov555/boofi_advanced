import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminPanel.css'; // Подключаем наши основные стили
import UsersTab from '../components/UsersTab';
import AdminBookingsTab from '../components/AdminBookingsTab';
// import AdminPlacesTab from '../components/AdminPlacesTab'; // Закомментировано, т.к. еще не реализовано

export default function AdminPanel() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('users'); // Начнем с вкладки Пользователи по умолчанию

    useEffect(() => {
        const userRole = localStorage.getItem('userRole');
        const authHeader = localStorage.getItem('authHeader'); // Проверим и токен на всякий случай

        if (userRole !== 'ADMIN' || !authHeader) {
            // Если не админ или нет токена, перенаправляем
            navigate('/auth/login'); // Лучше на логин, если нет прав
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
                    <div className="admin-tab-content"> {/* Обертка для консистентности */}
                        <h2>Управление помещениями и местами</h2>
                        <p>Этот раздел находится в разработке.</p>
                        {/* Здесь будет компонент для управления помещениями/местами, например <AdminPlacesTab /> */}
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

            <div className="admin-main-content"> {/* Общая обертка для табов и контента */}
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

                {/* <div className="tab-content-wrapper">  Убрали эту обертку, .admin-tab-content будет у каждого таба */}
                {renderTabContent()}
                {/* </div> */}
            </div>
        </div>
    );
}