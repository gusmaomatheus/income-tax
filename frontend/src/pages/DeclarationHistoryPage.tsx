import { isAxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Modal from "../components/ui/Modal";
import { api } from "../services/api";

interface Declaration {
    id: string;
    year: number;
    status: string;
}

export function DeclarationsHistoryPage(): React.JSX.Element {
    const [items, setItems] = useState<Declaration[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [modalMessage, setModalMessage] = useState<string>("");
    const [modalVariant, setModalVariant] = useState<"info" | "warning" | "success">("info");
    const [modalTitle, setModalTitle] = useState<string | undefined>(undefined);

    useEffect(() => {
        const fetch = async () => {
            setLoading(true);
            try {
                const res = await api.get<Declaration[]>("/declarations/history");
                setItems(res.data || []);
            } catch (err) {
                let msg = "Erro ao carregar histórico.";
                if (isAxiosError(err) && err.response) {
                    if (err.response.status === 401) msg = "Não autorizado. Faça login novamente.";
                    else msg = err.response.data?.message || msg;
                }
                setModalTitle("Falha");
                setModalMessage(msg);
                setModalVariant("warning");
                setModalOpen(true);
            } finally {
                setLoading(false);
            }
        };

        fetch();
    }, []);

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h1 className="text-2xl font-bold text-slate-800 mb-6">Histórico de Declarações</h1>

            {loading ? (
                <p className="text-slate-600">Carregando...</p>
            ) : items.length === 0 ? (
                <p className="text-slate-600">Nenhuma declaração encontrada.</p>
            ) : (
                <div className="grid grid-cols-1 gap-4">
                    {items.map((d) => (
                        <div key={d.id} className="flex items-center justify-between bg-white p-4 rounded-lg shadow-sm">
                            <div>
                                <div className="text-lg font-semibold text-slate-800">Ano {d.year}</div>
                                <div className="text-sm text-slate-600">Status: {d.status}</div>
                            </div>

                            <div className="flex items-center gap-3">
                                <Link
                                    to={`/declaracoes/editar/${d.id}`}
                                    className="text-emerald-600 bg-emerald-50 rounded-md px-4 py-2 font-medium hover:bg-emerald-100"
                                >
                                    Editar
                                </Link>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <Modal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                variant={modalVariant}
                title={modalTitle}
                message={modalMessage}
            />
        </div>
    );
}