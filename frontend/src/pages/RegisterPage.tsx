import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';

type FormData = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
};


export function RegisterPage(): React.JSX.Element {
    const [formData, setFormData] = useState<FormData>({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        // TODO: Adicionar lógica de validação

        console.log('Dados do formulário de cadastro:', formData);
        alert('Cadastro realizado com sucesso! (Verifique o console)');

        // TODO: Enviar dados para a API usando axios
    };

    return (
        <>
            <div className="text-center mb-10">
                <h1 className="text-5xl font-bold text-emerald-600">
                    Taxfy
                </h1>
                <p className="text-xl text-slate-500 mt-2">
                    Nunca foi tão fácil escapar do leãozinho
                </p>
            </div>

            <div className="w-full max-w-lg bg-white p-8 md:p-10 rounded-xl shadow-xl">
                <h1 className="text-3xl font-bold text-slate-800 mb-2 text-center">
                    Criar Conta
                </h1>
                <p className="text-slate-600 text-center mb-6">
                    Preencha seus dados para acessar a plataforma.
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">

                    <div className="flex flex-col sm:flex-row gap-4">
                        <Input
                            label="Nome"
                            id="firstName"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            placeholder="Seu nome"
                            containerClassName="w-full"
                            required
                        />
                        <Input
                            label="Sobrenome"
                            id="lastName"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            placeholder="Seu sobrenome"
                            containerClassName="w-full"
                            required
                        />
                    </div>

                    <Input
                        label="E-mail"
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="voce@exemplo.com"
                        required
                    />

                    <Input
                        label="Senha"
                        type="password"
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="********"
                        required
                    />

                    <Button
                        type="submit"
                        variant="primary"
                        className="w-full !mt-6"
                    >
                        Cadastrar
                    </Button>

                    <p className="text-sm text-center text-slate-600 !mt-5">
                        Já possui uma conta?{' '}
                        <Link
                            to="/login"
                            className="font-medium text-emerald-600 hover:text-emerald-700"
                        >
                            Faça login
                        </Link>
                    </p>
                </form>
            </div>
        </>

    );
}