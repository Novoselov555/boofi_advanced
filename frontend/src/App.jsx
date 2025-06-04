import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import CoworkingViewPage from './pages/CoworkingViewPage';
import AdminPanel from './pages/AdminPanel';
import MyBookingsPage from './pages/MyBookingsPage';

const PrivateRoute = ({ children }) => {
    const authHeader = localStorage.getItem('authHeader');
    return authHeader ? children : <Navigate to="/auth/login" replace />;
};

const AdminRoute = ({ children }) => {
    const authHeader = localStorage.getItem('authHeader');
    const userRole = localStorage.getItem('userRole');

    if (!authHeader) {
        return <Navigate to="/auth/login" replace />;
    }

    if (userRole !== 'ADMIN') {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
};

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/auth/login" element={<Login />} />
                <Route path="/auth/register" element={<Register />} />

                <Route path="/dashboard" element={
                    <PrivateRoute>
                        <Dashboard />
                    </PrivateRoute>
                } />

                <Route path="/coworking/:coworkingId" element={
                    <PrivateRoute>
                        <CoworkingViewPage />
                    </PrivateRoute>
                } />

                {/*<Route path="/booking/:placeId" element={*/}
                {/*    <PrivateRoute>*/}
                {/*        <BookingPage />*/}
                {/*    </PrivateRoute>*/}
                {/*} />*/}

                <Route path="/my-bookings" element={
                    <PrivateRoute>
                        <MyBookingsPage />
                    </PrivateRoute>
                } />

                <Route path="/admin" element={
                    <AdminRoute>
                        <AdminPanel />
                    </AdminRoute>
                } />

                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
