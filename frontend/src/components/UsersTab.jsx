import React, { useEffect, useState } from 'react';
import '../pages/AdminPanel.css';

export default function UsersTab() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editUser, setEditUser] = useState(null);
    const [editForm, setEditForm] = useState({ name: '', email: '', role: '' });
    const authHeader = localStorage.getItem('authHeader');

    const fetchUsers = () => {
        setLoading(true);
        fetch('http://localhost:8080/admin/users', {
            method: 'GET',
            headers: { 'Authorization': authHeader },
        })
            .then(res => {
                if (!res.ok) throw new Error('Ошибка загрузки пользователей');
                return res.json();
            })
            .then(data => {
                setUsers(data);
                setLoading(false);
            })
            .catch(() => {
                setError('Ошибка загрузки пользователей');
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchUsers();
        // eslint-disable-next-line
    }, []);

    const handleDelete = (id) => {
        if (!window.confirm('Удалить пользователя?')) return;
        fetch(`http://localhost:8080/admin/users/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': authHeader }
        })
            .then(res => {
                if (!res.ok) throw new Error();
                setUsers(users.filter(u => u.id !== id));
            })
            .catch(() => setError('Ошибка удаления пользователя'));
    };

    const handleDeleteAll = () => {
        if (!window.confirm('Удалить всех пользователей?')) return;
        fetch('http://localhost:8080/admin/users', {
            method: 'DELETE',
            headers: { 'Authorization': authHeader }
        })
            .then(res => {
                if (!res.ok) throw new Error();
                setUsers([]);
            })
            .catch(() => setError('Ошибка удаления всех пользователей'));
    };

    const handleEdit = (user) => {
        setEditUser(user);
        setEditForm({ name: user.name, email: user.email, role: user.role });
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditForm(prev => ({ ...prev, [name]: value }));
    };

    const handleEditSubmit = (e) => {
        e.preventDefault();
        fetch(`http://localhost:8080/admin/users${editUser.id}`, {
            method: 'POST',
            headers: {
                'Authorization': authHeader,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(editForm)
        })
            .then(res => {
                if (!res.ok) throw new Error();
                return res.json();
            })
            .then(updatedUser => {
                setUsers(users.map(u => u.id === updatedUser.id ? updatedUser : u));
                setEditUser(null);
            })
            .catch(() => setError('Ошибка обновления пользователя'));
    };

    if (loading) return <div>Загрузка пользователей...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div>
            <h2>Управление пользователями</h2>
            <button className="delete-button" onClick={handleDeleteAll} style={{ marginBottom: 24 }}>Удалить всех</button>
            <table className="users-table">
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
                            <button onClick={() => handleEdit(user)} style={{ marginRight: 8 }}>Редактировать</button>
                            <button className="delete-button" onClick={() => handleDelete(user.id)}>Удалить</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {editUser && (
                <div className="edit-modal">
                    <form onSubmit={handleEditSubmit} className="edit-form">
                        <h3>Редактировать пользователя</h3>
                        <label>
                            Имя:
                            <input name="name" value={editForm.name} onChange={handleEditChange} required />
                        </label>
                        <label>
                            Email:
                            <input name="email" value={editForm.email} onChange={handleEditChange} required />
                        </label>
                        <label>
                            Роль:
                            <select name="role" value={editForm.role} onChange={handleEditChange} required>
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </label>
                        <div style={{ marginTop: 16 }}>
                            <button type="submit">Сохранить</button>
                            <button type="button" onClick={() => setEditUser(null)} style={{ marginLeft: 8 }}>Отмена</button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}