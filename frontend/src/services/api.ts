import axios from 'axios';

const baseURL: string | undefined = import.meta.env.VITE_API_BASE_URL;

if (!baseURL) {
    throw new Error(
        'VITE_API_BASE_URL não está definida no seu arquivo .env.development ou .env'
    );
}

export const api = axios.create({
    baseURL: baseURL,
});


api.interceptors.request.use(
    (config) => {
        const token: string | null = localStorage.getItem('jwt_token');

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);

// TODO: Adicionar um interceptor de requisição para tratar token expirado