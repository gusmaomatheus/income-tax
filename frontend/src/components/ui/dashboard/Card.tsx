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
            className="group rounded-lg bg-white p-6 shadow-sm hover:shadow-md focus:outline-none"
        >
            <h3 className="text-lg font-semibold text-slate-800 mb-2">{title}</h3>
            <p className="text-sm text-slate-600 mb-4">{description}</p>
            <div className="flex justify-end">
                <Link
                    to={path}
                    className="inline-block text-emerald-600 bg-emerald-50 rounded-md px-4 py-2 font-medium hover:bg-emerald-100 transition"
                >
                    Acessar
                </Link>
            </div>
        </div>
    );
}

export default Card;