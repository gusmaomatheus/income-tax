import React, { useId } from 'react';

export type InputProps = {
    label?: string;
    error?: string;
    containerClassName?: string;
    className?: string;
} & React.InputHTMLAttributes<HTMLInputElement>;

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
    (
        {
            label,
            error,
            id,
            type = 'text',
            className = '',
            containerClassName = '',
            ...rest
        },
        ref,
    ): React.JSX.Element => {

        const autoId: string = useId();
        const inputId: string = id || autoId;

        const baseInputStyles: string =
            'block w-full px-3 py-2 border border-slate-300 rounded-md shadow-sm placeholder-slate-400 transition-all duration-150 ease-in-out sm:text-sm focus:outline-none focus:ring-1 disabled:bg-slate-50 disabled:text-slate-500';

        const focusStyles: string =
            'focus:ring-emerald-500 focus:border-emerald-500';

        const errorStyles: string =
            'border-red-500 text-red-600 focus:ring-red-500 focus:border-red-500';

        return (
            <div className={`w-full ${containerClassName}`}>
                {label && (
                    <label
                        htmlFor={inputId}
                        className="block text-sm font-medium text-slate-700 mb-1"
                    >
                        {label}
                    </label>
                )}

                <input
                    id={inputId}
                    type={type}
                    ref={ref}
                    className={`${baseInputStyles} ${error ? errorStyles : focusStyles
                        } ${className}`}
                    aria-invalid={!!error}
                    aria-describedby={error ? `${inputId}-error` : undefined}
                    {...rest}
                />

                {error && (
                    <p
                        className="mt-1 text-sm text-red-600"
                        id={`${inputId}-error`}
                    >
                        {error}
                    </p>
                )}
            </div>
        );
    },
);
