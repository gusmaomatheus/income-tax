import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

export function ProtectedLayout(): React.JSX.Element {
    const { isAuthenticated } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return (
        <div className="min-h-screen bg-slate-100">
            <nav className="bg-white shadow-md">
                <div className="max-w-4xl mx-auto px-4 py-3 flex justify-between items-center">
                    <h1 className="text-xl font-bold text-emerald-600">Taxfy Dashboard</h1>
                    <LogoutButton />
                </div>
            </nav>

            <main className="max-w-4xl mx-auto p-4 mt-4">
                <Outlet />
            </main>
        </div>
    );
}

function LogoutButton(): React.JSX.Element {
    const { logout } = useAuth();

    return (
        <button
            onClick={logout}
            className="bg-red-500 text-white px-3 py-1 rounded-md text-sm font-medium hover:bg-red-600 transition-colors"
        >
            Sair
        </button>
    );
}