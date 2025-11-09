import { AlertTriangle, CheckCircle, Info } from "lucide-react";
import React, { useEffect } from "react";

type Variant = "info" | "warning" | "success";

interface ModalProps {
    open: boolean;
    onClose: () => void;
    variant?: Variant;
    message: string;
    title?: string;
}

const icons: Record<Variant, { icon: React.JSX.Element; circleClass: string; iconClass: string; btnClass: string }> = {
    info: {
        icon: <Info size={28} />,
        circleClass: "bg-blue-100",
        iconClass: "text-blue-600",
        btnClass: "bg-blue-600 hover:bg-blue-700 text-white",
    },
    warning: {
        icon: <AlertTriangle size={28} />,
        circleClass: "bg-yellow-100",
        iconClass: "text-yellow-600",
        btnClass: "bg-yellow-500 hover:bg-yellow-600 text-white",
    },
    success: {
        icon: <CheckCircle size={28} />,
        circleClass: "bg-green-100",
        iconClass: "text-green-600",
        btnClass: "bg-green-600 hover:bg-green-700 text-white",
    },
};

export default function Modal({
    open,
    onClose,
    variant = "info",
    message,
    title,
}: ModalProps) {
    useEffect(() => {
        if (!open) return;
        const onKey = (e: KeyboardEvent) => {
            if (e.key === "Escape") onClose();
        };
        document.addEventListener("keydown", onKey);
        return () => document.removeEventListener("keydown", onKey);
    }, [open, onClose]);

    if (!open) return null;

    const v = icons[variant];

    return (
        <div
            role="dialog"
            aria-modal="true"
            aria-labelledby="modal-title"
            className="fixed inset-0 z-50 flex items-center justify-center p-4"
        >
            <div
                className="fixed inset-0 bg-black/40 backdrop-blur-sm"
                onClick={onClose}
                aria-hidden
            />
            <div className="relative z-10 max-w-md w-full bg-white rounded-lg shadow-lg p-6 flex flex-col items-center text-center">
                <div
                    className={`flex items-center justify-center w-16 h-16 rounded-full mb-4 ${v.circleClass}`}
                >
                    <span className={`${v.iconClass}`}>{v.icon}</span>
                </div>

                {title ? (
                    <h3 id="modal-title" className="text-lg font-semibold mb-2 text-slate-800">
                        {title}
                    </h3>
                ) : null}

                <p className="text-sm text-slate-700 mb-6">{message}</p>

                <button
                    type="button"
                    className={`px-4 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-opacity-50 ${v.btnClass} focus:ring-gray-300`}
                    onClick={onClose}
                >
                    Ok
                </button>
            </div>
        </div>
    );
}