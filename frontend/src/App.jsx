import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/auth/login"    element={<Login />} />
                <Route path="/auth/register" element={<Register />} />
                <Route
                    path="/dashboard" element={<Dashboard />}
                    // element={
                    //     localStorage.getItem('token')
                    //         ? <Dashboard />
                    //         : <Navigate to="/login" replace />
                    // }
                />
                <Route path="*" element={<Navigate to="/auth/login" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
