import { isAxiosError } from 'axios';
import React, {
    createContext,
    useCallback,
    useEffect,
    useMemo,
    useState
} from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from '../components/ui/Modal';
import { api } from '../services/api';

type ModalVariant = 'info' | 'warning' | 'success';

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

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [modalMessage, setModalMessage] = useState<string>('');
    const [modalVariant, setModalVariant] = useState<ModalVariant>('info');
    const [modalTitle, setModalTitle] = useState<string | undefined>(undefined);

    useEffect(() => {
        const storedToken: string | null = localStorage.getItem('jwt_token');
        if (storedToken) {
            setToken(storedToken);
        }
    }, []);

    const login = useCallback(
        async (email: string, password: string): Promise<void> => {
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
                let errorMessage = 'Falha ao tentar fazer login.';
                if (isAxiosError(error) && error.response) {
                    errorMessage = error.response.data.message || 'Credenciais inválidas.';
                }

                setModalTitle('Erro no login');
                setModalMessage(errorMessage);
                setModalVariant('warning');
                setModalOpen(true);

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

    return (
        <AuthContext.Provider value={value}>
            {children}
            <Modal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                variant={modalVariant}
                title={modalTitle}
                message={modalMessage}
            />
        </AuthContext.Provider>
    );
}