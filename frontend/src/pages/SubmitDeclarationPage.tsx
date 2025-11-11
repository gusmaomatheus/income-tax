import { isAxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { Button } from "../components/ui/Button";
import Modal from "../components/ui/Modal";
import { api } from "../services/api";

interface Declaration {
    id: string;
    year: number;
    status: string;
}

export function SubmitDeclarationPage(): React.JSX.Element {
    const [declarations, setDeclarations] = useState<Declaration[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [sendingId, setSendingId] = useState<string | null>(null);

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [modalMessage, setModalMessage] = useState<string>("");
    const [modalVariant, setModalVariant] = useState<"info" | "warning" | "success">("info");
    const [modalTitle, setModalTitle] = useState<string | undefined>(undefined);
    const [onModalClose, setOnModalClose] = useState<(() => void) | null>(null);

    useEffect(() => {
        const fetchHistory = async () => {
            setLoading(true);
            try {
                const res = await api.get<Declaration[]>("/declarations/history");
                setDeclarations(res.data || []);
            } catch (err) {
                let msg = "Erro ao carregar declarações.";
                if (isAxiosError(err) && err.response) {
                    if (err.response.status === 401) {
                        msg = "Não autorizado. Faça login novamente.";
                    } else {
                        msg = err.response.data?.message || msg;
                    }
                }
                setModalTitle("Falha");
                setModalMessage(msg);
                setModalVariant("warning");
                setOnModalClose(null);
                setModalOpen(true);
            } finally {
                setLoading(false);
            }
        };

        fetchHistory();
    }, []);

    const handleSubmit = async (id: string) => {
        setSendingId(id);
        try {
            const res = await api.post(`/declarations/${id}/submit`);
            const updatedStatus = res.data?.status || "submitted";

            setDeclarations((prev) =>
                prev.map((d) => (d.id === id ? { ...d, status: updatedStatus } : d))
            );

            setModalTitle("Envio realizado");
            setModalMessage(`Declaração enviada com sucesso (${updatedStatus}).`);
            setModalVariant("success");
            setOnModalClose(null);
            setModalOpen(true);
        } catch (err) {
            let msg = "Erro ao enviar declaração.";
            if (isAxiosError(err) && err.response) {
                const status = err.response.status;
                if (status === 401) {
                    msg = "Não autorizado. Faça login novamente.";
                } else if (status === 409) {
                    msg = "Conflito ao enviar declaração.";
                } else {
                    msg = err.response.data?.message || msg;
                }
            }
            setModalTitle("Falha no envio");
            setModalMessage(msg);
            setModalVariant("warning");
            setOnModalClose(null);
            setModalOpen(true);
        } finally {
            setSendingId(null);
        }
    };

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h1 className="text-2xl font-bold text-slate-800 mb-6">Enviar Declaração</h1>

            {loading ? (
                <p className="text-slate-600">Carregando declarações...</p>
            ) : declarations.length === 0 ? (
                <p className="text-slate-600">Nenhuma declaração encontrada.</p>
            ) : (
                <div className="grid grid-cols-1 gap-4">
                    {declarations.map((d) => (
                        <div
                            key={d.id}
                            className="flex items-center justify-between bg-white p-4 rounded-lg shadow-sm"
                        >
                            <div>
                                <div className="text-lg font-semibold text-slate-800">Ano {d.year}</div>
                                <div className="text-sm text-slate-600">Status: {d.status}</div>
                            </div>

                            <div className="flex items-center gap-3">
                                <Button
                                    type="button"
                                    variant="primary"
                                    onClick={() => handleSubmit(d.id)}
                                    disabled={sendingId === d.id}
                                >
                                    {sendingId === d.id ? "Enviando..." : "Enviar"}
                                </Button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

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