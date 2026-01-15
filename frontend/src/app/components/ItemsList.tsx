"use client";

import { useState } from "react";
import { ItemResponse, useItems } from "../hooks/useItems";
import { Plus, Pencil, Trash2 } from "lucide-react";
import ItemForm from "./ItemForm";

interface ItemsListProps {
    shelfId: string;
    activeItemId?: string;
    onSelectItem?: (id: string) => void;
}

export default function ItemsList({ shelfId, activeItemId, onSelectItem }: ItemsListProps) {
    const items = useItems(shelfId);
    const [ isFormOpen, setIsFormOpen ] = useState(false);
    const [ editingItem, setEditingItem ] = useState<ItemResponse | undefined>(undefined);

    const handleEditItem = (item: ItemResponse) => {
        setEditingItem(item);
        setIsFormOpen(true);
    };

    const handleAddItem = () => {
        setEditingItem(undefined);
        setIsFormOpen(true);
    };

    const handleDeleteItem = (item: ItemResponse) => {
        if (confirm(`Czy na pewno chcesz usunąć ${item.name}?`)) {
            items.deleteItem(item.id);
        }
    };

    return (
        <>
            {isFormOpen && (
                <ItemForm
                    initialData={editingItem ? { name: editingItem.name } : undefined}
                    onSubmit={(data) => {
                        if (editingItem) {
                            items.editItem({ id: editingItem.id, name: data.name });
                        } else {
                            items.saveItem({ name: data.name });
                        }
                        setIsFormOpen(false);
                    }}
                    onClose={() => setIsFormOpen(false)}
                />
            )}
            <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between px-2">
                    <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50">
                        Rzeczy
                    </h2>
                    <button 
                        className="p-1.5 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors"
                        onClick={() => handleAddItem()}
                    >
                        <Plus size={18} />
                    </button>
                </div>

                <ul className="space-y-1">
                    {items.items.content.map((item) => {
                        const isActive = item.id === activeItemId;
                        return (
                            <li key={item.id} className="group relative">
                                <button 
                                    onClick={() => onSelectItem?.(item.id)}
                                    className={`
                                        w-full text-left px-4 py-3 rounded-lg transition-all duration-200
                                        ${isActive 
                                            ? 'bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 ring-1 ring-blue-200 dark:ring-blue-800 shadow-sm' 
                                            : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800 hover:text-blue-200'
                                        }
                                    `}
                                >
                                    <span className="font-medium pr-16 block truncate">{item.name}</span>
                                </button>

                                <div className="absolute right-2 top-1/2 -translate-y-1/2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button onClick={() => handleEditItem(item)} className="p-1.5 hover:bg-white/50 dark:hover:bg-zinc-700 rounded-md text-zinc-600" title="Edytuj">
                                        <Pencil size={14} />
                                    </button>
                                    <button onClick={() => handleDeleteItem(item)} className="p-1.5 hover:bg-red-100 rounded-md text-red-600" title="Usuń">
                                        <Trash2 size={14} />
                                    </button>
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>
        </>
    );
}