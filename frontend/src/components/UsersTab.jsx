import React, { useEffect, useState, useCallback } from 'react';
import '../pages/AdminPanel.css'; // Используем те же стили для общего вида

const API_BASE_URL = 'http://localhost:8080';

export default function UsersTab() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editUser, setEditUser] = useState(null);
    const [editForm, setEditForm] = useState({ name: '', email: '', role: '' });
    const [editMessage, setEditMessage] = useState({ type: '', text: '' });

    const authHeader = localStorage.getItem('authHeader');

    const fetchUsers = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/users`, {
                headers: { 'Authorization': authHeader },
            });
            if (!response.ok) {
                const errData = await response.json().catch(() => ({ message: `Ошибка загрузки пользователей: ${response.status}` }))
                throw new Error(errData.message);
            }
            const data = await response.json();
            setUsers(data || []);
        } catch (err) {
            console.error("Ошибка при загрузке пользователей:", err);
            setError(err.message || 'Не удалось загрузить пользователей.');
        } finally {
            setLoading(false);
        }
    }, [authHeader]);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    const handleDelete = async (id) => {
        if (!window.confirm('Вы уверены, что хотите удалить этого пользователя?')) return;
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/users/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) {
                const errData = await response.json().catch(() => ({ message: `Ошибка удаления: ${response.status}` }))
                throw new Error(errData.message);
            }
            setUsers(prevUsers => prevUsers.filter(u => u.id !== id));
        } catch (err) {
            console.error("Ошибка при удалении пользователя:", err);
            setError(err.message || 'Не удалось удалить пользователя.');
        }
    };

    const handleDeleteAll = async () => {
        if (!window.confirm('Вы уверены, что хотите удалить ВСЕХ пользователей? Это действие необратимо!')) return;
        setError('');
        try {
            const response = await fetch(`${API_BASE_URL}/admin/users`, {
                method: 'DELETE',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) {
                const errData = await response.json().catch(() => ({ message: `Ошибка удаления: ${response.status}` }))
                throw new Error(errData.message);
            }
            setUsers([]);
        } catch (err) {
            console.error("Ошибка при удалении всех пользователей:", err);
            setError(err.message || 'Не удалось удалить всех пользователей.');
        }
    };

    const handleEdit = (user) => {
        setEditUser(user);
        setEditForm({ name: user.name, email: user.email, role: user.role });
        setEditMessage({ type: '', text: '' });
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditForm(prev => ({ ...prev, [name]: value }));
    };

    const handleEditSubmit = async (e) => {
        e.preventDefault();
        if (!editUser) return;
        setEditMessage({ type: 'loading', text: 'Сохранение данных...' });
        const targetUrl = `${API_BASE_URL}/admin/users/${editUser.id}`;
        try {
            const response = await fetch(targetUrl, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(editForm)
            });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: `Ошибка обновления: ${response.status}` }));
                throw new Error(errorData.message || 'Не удалось обновить данные пользователя.');
            }
            const updatedUser = await response.json();
            setUsers(prevUsers => prevUsers.map(u => u.id === updatedUser.id ? updatedUser : u));
            setEditMessage({ type: 'success', text: 'Данные пользователя успешно обновлены!' });
            setTimeout(() => {
                setEditUser(null);
            }, 2000);
        } catch (err) {
            console.error("Ошибка при обновлении пользователя:", err);
            setEditMessage({ type: 'error', text: err.message });
        }
    };

    if (loading) return <div className="admin-tab-content">Загрузка пользователей...</div>;
    if (error) return <div className="admin-tab-content"><div className="admin-message admin-message-error">{error}</div></div>;

    return (
        <div className="admin-tab-content">
            <h2>Управление пользователями</h2>
            {users.length > 0 && (
                <button
                    className="admin-delete-all-button" // Используем этот класс
                    onClick={handleDeleteAll}
                >
                    Удалить всех пользователей
                </button>
            )}
            {users.length === 0 && !loading && (
                <p>Пользователи не найдены.</p>
            )}
            {users.length > 0 && (
                <table className="admin-table"> {/* Ключевой класс для таблицы */}
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Email</th>
                        <th>Роль</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.name}</td>
                            <td>{user.email}</td>
                            <td>{user.role}</td>
                            <td>
                                <button onClick={() => handleEdit(user)} className="edit-btn" style={{ marginRight: 8 }}>
                                    Редактировать
                                </button>
                                <button className="delete-button" onClick={() => handleDelete(user.id)}>
                                    Удалить
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
            {editUser && (
                <div className="admin-modal-overlay">
                    <div className="admin-modal">
                        <form onSubmit={handleEditSubmit}>
                            <h3>Редактировать пользователя #{editUser.id}</h3>
                            {editMessage.text && (
                                <div className={`admin-message admin-message-${editMessage.type}`}>
                                    {editMessage.text}
                                </div>
                            )}
                            <div className="admin-form-group">
                                <label htmlFor="name">Имя:</label>
                                <input id="name" name="name" value={editForm.name} onChange={handleEditChange} required />
                            </div>
                            <div className="admin-form-group">
                                <label htmlFor="email">Email:</label>
                                <input id="email" name="email" type="email" value={editForm.email} onChange={handleEditChange} required />
                            </div>
                            <div className="admin-form-group">
                                <label htmlFor="role">Роль:</label>
                                <select id="role" name="role" value={editForm.role} onChange={handleEditChange} required>
                                    <option value="" disabled>Выберите роль</option>
                                    <option value="USER">USER</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            </div>
                            <div className="admin-modal-actions">
                                <button type="submit" className="admin-button-confirm" disabled={editMessage.type === 'loading'}>
                                    {editMessage.type === 'loading' ? 'Сохранение...' : 'Сохранить'}
                                </button>
                                <button type="button" onClick={() => setEditUser(null)} className="admin-button-cancel" disabled={editMessage.type === 'loading'}>
                                    Отмена
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}