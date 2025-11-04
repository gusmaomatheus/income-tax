import { useAuth } from '../hooks/useAuth';

export function DashboardPage(): React.JSX.Element {
    const { token } = useAuth();

    return (
        <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-2xl font-bold text-slate-800 mb-4">
                Dashboard
            </h2>
            <p className="text-slate-600">
                Você está autenticado!
            </p>

            <div className="mt-4 bg-slate-50 p-3 rounded-md overflow-x-auto">
                <h3 className="text-sm font-semibold text-slate-700">Seu Token JWT:</h3>
                <code className="text-xs text-emerald-700 break-all">
                    {token}
                </code>
            </div>
        </div>
    );
}