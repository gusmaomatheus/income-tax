import { isAxiosError } from 'axios';
import React, {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState
} from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';

interface IAuthContext {
    token: string | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
}

export const AuthContext = createContext<IAuthContext | undefined>(undefined);

type LoginResponse = {
    token: string;
};

interface AuthProviderProps {
    children: React.ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps): React.JSX.Element {
    const [token, setToken] = useState<string | null>(
        localStorage.getItem('jwt_token')
    );
    const navigate = useNavigate();

    useEffect(() => {
        const storedToken: string | null = localStorage.getItem('jwt_token');
        if (storedToken) {
            setToken(storedToken);
        }
    }, []);

    const login = useCallback(
        async (email: string, password: string): Promise<void> => {
            console.log('Tentando login com:', email);

            try {
                const response = await api.post<LoginResponse>(
                    '/auth',
                    {
                        username: email,
                        password: password,
                    }
                );

                const { token } = response.data;

                localStorage.setItem('jwt_token', token);

                setToken(token);

                navigate('/dashboard');

            } catch (error) {
                console.error('Erro no login:', error);

                let errorMessage = 'Falha ao tentar fazer login.';
                if (isAxiosError(error) && error.response) {
                    errorMessage = error.response.data.message || 'Credenciais inválidas.';
                }

                alert(errorMessage);

                throw new Error(errorMessage);
            }
        },
        [navigate],
    );

    const logout = useCallback(() => {
        setToken(null);
        localStorage.removeItem('jwt_token');
        navigate('/login');
    }, [navigate]);

    const value = useMemo<IAuthContext>(
        () => ({
            token,
            login,
            logout,
            isAuthenticated: !!token,
        }),
        [token, login, logout],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}