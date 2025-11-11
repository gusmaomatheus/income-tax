import { isAxiosError } from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import Modal from "../components/ui/Modal";
import { api } from "../services/api";

interface Declaration {
    id: string;
    year: number;
    status?: string;
}

export function EditDeclarationPage(): React.JSX.Element {
    const { id } = useParams<{ id?: string }>();
    const navigate = useNavigate();

    const [declaration, setDeclaration] = useState<Declaration | null>(null);
    const [year, setYear] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(!!id);
    const [saving, setSaving] = useState<boolean>(false);

    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [modalMessage, setModalMessage] = useState<string>("");
    const [modalVariant, setModalVariant] = useState<"info" | "warning" | "success">("info");
    const [modalTitle, setModalTitle] = useState<string | undefined>(undefined);
    const [onModalClose, setOnModalClose] = useState<(() => void) | null>(null);

    useEffect(() => {
        if (!id) return;
        const fetchOne = async () => {
            setLoading(true);
            try {
                const res = await api.get<Declaration>(`/declarations/${id}`);
                setDeclaration(res.data);
                setYear(String(res.data.year));
            } catch (err) {
                let msg = "Erro ao carregar declaração.";
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

        fetchOne();
    }, [id]);

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!id) return;
        const parsed = Number(year);
        if (!Number.isInteger(parsed) || parsed < 1900 || parsed > new Date().getFullYear()) {
            setModalTitle("Ano inválido");
            setModalMessage("Informe um ano válido.");
            setModalVariant("warning");
            setModalOpen(true);
            return;
        }

        setSaving(true);
        try {
            await api.put(`/declarations/${id}`, { year: parsed });
            setModalTitle("Atualizado");
            setModalMessage("Declaração atualizada com sucesso.");
            setModalVariant("success");
            setOnModalClose(() => () => navigate("/declaracoes/editar"));
            setModalOpen(true);
        } catch (err) {
            let msg = "Erro ao atualizar declaração.";
            if (isAxiosError(err) && err.response) {
                if (err.response.status === 401) msg = "Não autorizado. Faça login novamente.";
                else if (err.response.status === 409) msg = "Já existe outra declaração com esse ano.";
                else msg = err.response.data?.message || msg;
            }
            setModalTitle("Falha");
            setModalMessage(msg);
            setModalVariant("warning");
            setModalOpen(true);
        } finally {
            setSaving(false);
        }
    };

    // if no id param, show a small helper to go back to list
    if (!id) {
        return (
            <div className="max-w-4xl mx-auto p-6">
                <h1 className="text-2xl font-bold text-slate-800 mb-4">Editar Declaração</h1>
                <p className="text-slate-600 mb-4">Selecione uma declaração na lista para editar.</p>
                <div>
                    <Button type="button" variant="primary" onClick={() => navigate("/declaracoes/historico")}>
                        Ver Histórico
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-lg mx-auto p-6">
            <h1 className="text-2xl font-bold text-slate-800 mb-4">Editar Declaração</h1>

            {loading ? (
                <p className="text-slate-600">Carregando...</p>
            ) : declaration ? (
                <form onSubmit={handleSave} className="bg-white p-6 rounded-lg shadow-sm space-y-4">
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

                    <div className="text-sm text-slate-600">Status: {declaration.status ?? "—"}</div>

                    <div className="flex justify-end">
                        <Button type="submit" variant="primary" className="!px-6" disabled={saving}>
                            {saving ? "Salvando..." : "Salvar"}
                        </Button>
                    </div>
                </form>
            ) : (
                <p className="text-slate-600">Declaração não encontrada.</p>
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