import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
// import BookingPage from './pages/BookingPage';
// import MyBookings from './pages/MyBookings';
import AdminPanel from './pages/AdminPanel';

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

                {/*<Route path="/booking/:placeId" element={*/}
                {/*    <PrivateRoute>*/}
                {/*        <BookingPage />*/}
                {/*    </PrivateRoute>*/}
                {/*} />*/}

                {/*<Route path="/my-bookings" element={*/}
                {/*    <PrivateRoute>*/}
                {/*        <MyBookings />*/}
                {/*    </PrivateRoute>*/}
                {/*} />*/}

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
