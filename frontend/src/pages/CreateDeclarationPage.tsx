import { isAxiosError } from "axios";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import Modal from "../components/ui/Modal";
import { api } from "../services/api";

export function CreateDeclarationPage(): React.JSX.Element {
    const navigate = useNavigate();

    const [year, setYear] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [modalMessage, setModalMessage] = useState<string>("");
    const [modalVariant, setModalVariant] = useState<"info" | "warning" | "success">("info");
    const [modalTitle, setModalTitle] = useState<string | undefined>(undefined);
    const [onModalClose, setOnModalClose] = useState<(() => void) | null>(null);

    const validateYear = (y: number): boolean => {
        const currentYear = new Date().getFullYear();
        return Number.isInteger(y) && y >= 1900 && y <= currentYear;
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const parsed = Number(year);
        if (!validateYear(parsed)) {
            setModalTitle("Ano inválido");
            setModalMessage("Informe um ano válido (ex: 2024).");
            setModalVariant("warning");
            setOnModalClose(null);
            setModalOpen(true);
            return;
        }

        setIsLoading(true);

        try {
            const response = await api.post("/declarations", { year: parsed });

            if (response.status === 201) {
                setModalTitle("Declaração criada");
                setModalMessage("Declaração criada com sucesso.");
                setModalVariant("success");
                setOnModalClose(() => () => {
                    navigate("/declaracoes/editar");
                });
                setModalOpen(true);
                setYear("");
                return;
            }

            setModalTitle("Erro");
            setModalMessage("Resposta inesperada do servidor.");
            setModalVariant("warning");
            setOnModalClose(null);
            setModalOpen(true);
        } catch (error) {
            let errorMessage = "Erro ao criar declaração.";
            if (isAxiosError(error) && error.response) {
                const status = error.response.status;
                if (status === 401) {
                    errorMessage = "Não autorizado. Faça login novamente.";
                    setOnModalClose(() => () => navigate("/login"));
                } else if (status === 409) {
                    errorMessage = "Já existe uma declaração para esse ano.";
                    setOnModalClose(null);
                } else {
                    errorMessage = error.response.data?.message || "Erro no servidor.";
                    setOnModalClose(null);
                }
            } else {
                setOnModalClose(null);
            }

            setModalTitle("Falha");
            setModalMessage(errorMessage);
            setModalVariant("warning");
            setModalOpen(true);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-lg mx-auto p-6">
            <h1 className="text-2xl font-bold text-slate-800 mb-4">Nova Declaração</h1>

            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-sm space-y-4">
                <Input
                    label="Ano"
                    id="year"
                    name="year"
                    type="number"
                    value={year}
                    onChange={(e) => setYear(e.target.value)}
                    placeholder="ex: 2024"
                    required
                />

                <div className="flex justify-end">
                    <Button type="submit" variant="primary" className="!px-6">
                        {isLoading ? "Criando..." : "Criar Declaração"}
                    </Button>
                </div>
            </form>

            <Modal
                open={modalOpen}
                onClose={() => {
                    setModalOpen(false);
                    if (onModalClose) {
                        onModalClose();
                        setOnModalClose(null);
                    }
                }}
                variant={modalVariant}
                title={modalTitle}
                message={modalMessage}
            />
        </div>
    );
}
