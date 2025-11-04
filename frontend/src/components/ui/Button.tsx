import React from 'react';

type ButtonVariant = 'primary' | 'outline';

export type ButtonProps = {
    children: React.ReactNode;
    variant?: ButtonVariant;
    className?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;


export function Button({
    children,
    variant = 'primary',
    className = '',
    ...rest
}: ButtonProps): React.JSX.Element {

    const baseStyles: string =
        'py-2 px-4 rounded-md font-semibold transition-all duration-150 ease-in-out focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

    const variantStyles: Record<ButtonVariant, string> = {
        primary:
            'bg-emerald-500 text-white hover:bg-emerald-600 focus:ring-emerald-400',
        outline:
            'bg-white text-emerald-600 border border-emerald-500 hover:bg-emerald-50 focus:ring-emerald-400',
    };

    return (
        <button
            className={`${baseStyles} ${variantStyles[variant]} ${className}`}
            {...rest}
        >
            {children}
        </button>
    );
}