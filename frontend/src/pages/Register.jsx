import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/auth.css';

export default function Register() {
    const [form, setForm] = useState({ name: '', email: '', password: '' });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const res = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(form),
            });

            const data = await res.json();

            if (!res.ok) {
                throw new Error(data.message || 'Ошибка при регистрации');
            }

            navigate('/auth/login', { replace: true });
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <h2>Регистрация</h2>
            {error && <div className="error-message">{error}</div>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="name">Имя пользователя</label>
                    <input
                        id="name"
                        name="name"
                        type="text"
                        placeholder="Введите имя пользователя"
                        value={form.name}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        placeholder="Введите email"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Пароль</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        placeholder="Введите пароль"
                        value={form.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
                </button>
            </form>
            <p className="auth-switch">
                Уже есть аккаунт? <Link to="/auth/login">Войти</Link>
            </p>
        </div>
    );
}
