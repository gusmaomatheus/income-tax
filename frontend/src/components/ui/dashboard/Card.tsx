import React from "react";
import { Link } from "react-router-dom";

interface CardProps {
    title: string;
    description: string;
    path: string;
}

export function Card({ title, description, path }: CardProps): React.JSX.Element {
    return (
        <div
            className="rounded-lg bg-white p-6 shadow-sm hover:shadow-md transition-shadow focus:outline-none"
        >
            <h3 className="text-lg font-semibold text-slate-800 mb-2">{title}</h3>
            <p className="text-sm text-slate-600 mb-4">{description}</p>
            <div className="flex justify-end">
                <Link to={path}>
                    Acessar
                </Link>
            </div>
        </div>
    );
}

export default Card;