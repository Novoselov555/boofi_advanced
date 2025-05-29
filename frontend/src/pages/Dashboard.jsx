import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

export default function Dashboard() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/auth/login', { replace: true });
    };

    return (
        <div className="dashboard-container">
            <h2>Добро пожаловать в Dashboard!</h2>
            <p>Здесь будет твоя основная логика и данные.</p>
            <button className="dashboard-logout" onClick={handleLogout}>
                Выйти
            </button>
        </div>
    );
}
