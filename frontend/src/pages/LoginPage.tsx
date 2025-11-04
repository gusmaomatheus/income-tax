import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { useAuth } from '../hooks/useAuth';

export function LoginPage(): React.JSX.Element {
    const [email, setEmail] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const { login } = useAuth();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await login(email, password);
        } catch (error) {
            setIsLoading(false);
        }
    };

    return (
        <>
            <div className="text-center mb-6">
                <h1 className="text-5xl font-bold text-emerald-600">
                    Taxfy
                </h1>
                <p className="text-xl text-slate-500 mt-2">
                    Nunca foi tão fácil escapar do leãozinho
                </p>
            </div>

            <div className="w-full max-w-md bg-white p-8 md:p-10 rounded-xl shadow-xl">

                <h2 className="text-3xl font-bold text-slate-800 mb-6 text-center">
                    Fazer Login
                </h2>

                <form onSubmit={handleSubmit} className="space-y-5">

                    <Input
                        label="E-mail"
                        type="email"
                        id="email"
                        name="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="voce@exemplo.com"
                        disabled={isLoading}
                        required
                    />

                    <Input
                        label="Senha"
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        disabled={isLoading}
                        required
                    />

                    <Button
                        type="submit"
                        variant="primary"
                        className="w-full !mt-6"
                        disabled={isLoading}
                    >
                        {isLoading ? 'Entrando...' : 'Entrar'}
                    </Button>

                    <p className="text-sm text-center text-slate-600 !mt-5">
                        Não possui uma conta?{' '}
                        <Link
                            to="/register"
                            className="font-medium text-emerald-600 hover:text-emerald-700"
                        >
                            Cadastre-se
                        </Link>
                    </p>
                </form>
            </div>
        </>
    );
}