"use client";

import { useItems } from "../hooks/useItems";

interface ItemsListProps {
    shelfId: string;
    activeItemId?: string;
    onSelectItem?: (id: string) => void;
}

export default function ItemsList({ shelfId, activeItemId, onSelectItem }: ItemsListProps) {
    const items = useItems(shelfId);

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50 px-2">
                Przedmioty
            </h2>
            <ul className="space-y-1">
                {items.data.content.map((item) => {
                    const isActive = item.id === activeItemId;
                    return (
                        <li key={item.id}>
                            <button 
                                onClick={() => onSelectItem?.(item.id)}
                                className={`
                                    w-full text-left px-4 py-3 rounded-lg transition-all duration-200
                                    ${isActive 
                                        ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 ring-1 ring-blue-200 dark:ring-blue-800' 
                                        : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800'
                                    }
                                `}
                            >
                                <span className="font-medium">{item.name}</span>
                            </button>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}