import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/auth.css';

export default function Register() {
    const [form, setForm] = useState({ name: '', email: '', password: '' });
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(form),
            });
            if (!res.ok) throw new Error('Ошибка регистрации');
            // после успешной регистрации — на страницу логина
            navigate('/auth/login', { replace: true });
        } catch (err) {
            alert(err.message);
        }
    };

    return (
        <div className="auth-container">
            <h2>Регистрация</h2>
            <form onSubmit={handleSubmit}>
                <input
                    name="name"
                    type="text"
                    placeholder="Имя пользователя"
                    value={form.username}
                    onChange={handleChange}
                    required
                />
                <input
                    name="email"
                    type="email"
                    placeholder="Email"
                    value={form.email}
                    onChange={handleChange}
                    required
                />
                <input
                    name="password"
                    type="password"
                    placeholder="Пароль"
                    value={form.password}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Зарегистрироваться</button>
            </form>
            <p className="auth-switch">
                Уже есть аккаунт? <Link to="/login">Войти</Link>
            </p>
        </div>
    );
}
