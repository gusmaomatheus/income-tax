import React from "react";
import Card from "../components/ui/dashboard/Card";

export function DashboardPage(): React.JSX.Element {
    return (
        <main>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <Card
                    title="Nova Declaração"
                    description="Criar uma nova declaração de imposto de renda."
                    path="/declaracoes/criar"
                />

                <Card
                    title="Enviar Declaração"
                    description="Enviar uma declaração já pronta para processamento."
                    path="/declaracoes/enviar"
                />

                <Card
                    title="Editar Declaração"
                    description="Editar uma declaração existente."
                    path="/declaracoes/editar"
                />

                <Card
                    title="Histórico"
                    description="Ver o histórico de declarações enviadas."
                    path="/declaracoes/historico"
                />
            </div>
        </main>
    );
}