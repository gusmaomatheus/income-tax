import { Outlet } from 'react-router-dom';

export function DefaultLayout(): React.JSX.Element {
    return (
        <main className="bg-emerald-50 min-h-screen flex flex-col items-center justify-center p-4">
            <Outlet />
        </main>
    );
}