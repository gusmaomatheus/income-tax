import React, {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState
} from 'react';
import { useNavigate } from 'react-router-dom';

interface IAuthContext {
    token: string | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
}

export const AuthContext = createContext<IAuthContext | undefined>(undefined);

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
            console.log('Tentando login com:', email, password);

            // TODO: Substituir por chamada real à API

            const mockApiCall = (): Promise<{ token: string }> => {
                return new Promise((resolve, reject) => {
                    setTimeout(() => {
                        if (email === 'user@taxfy.com' && password === '123456') {
                            const mockToken: string =
                                'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik1hdGhldXMgU2FudG9zIiwiZW1haWwiOiJ1c2VyQHRheGZ5LmNvbSIsImlhdCI6MTcxOTkzNDIwNn0.8_N5-0-r-sO9h4-m-s-p-s-e-c-u-r-e';
                            resolve({ token: mockToken });
                        } else {
                            reject(new Error('Credenciais inválidas! (Use user@taxfy.com e 123456)'));
                        }
                    }, 1000);
                });
            };

            try {
                const data = await mockApiCall();

                localStorage.setItem('jwt_token', data.token);

                setToken(data.token);

                navigate('/dashboard');

            } catch (error) {
                console.error('Erro no login:', error);
                alert((error as Error).message || 'Falha ao tentar fazer login.');
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