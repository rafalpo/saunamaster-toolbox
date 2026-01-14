"use client";

import { useState } from "react";
import { ShelfResponse, useShelves } from "../hooks/useShelves";
import { Plus, Pencil, Trash2 } from "lucide-react";
import ShelfForm from "./ShelfForm";

interface ShelvesListProps {
    onSelectShelf: (id: string) => void;
    activeShelfId?: string;
}

export default function ShelvesList({ onSelectShelf, activeShelfId }: ShelvesListProps) {
    const shelves = useShelves();
    const [ isFormOpen, setIsFormOpen ] = useState(false);
    const [ editingShelf, setEditingShelf ] = useState<ShelfResponse | undefined>(undefined);

    const handleEditShelf = (shelf: ShelfResponse) => {
        setEditingShelf(shelf);
        setIsFormOpen(true);
    };

    const handleAddShelf = () => {
        setEditingShelf(undefined);
        setIsFormOpen(true);
    };

    return (
        <>
            {isFormOpen && (
                <ShelfForm
                    initialData={editingShelf ? { name: editingShelf.name } : undefined}
                    onSubmit={(data) => {
                        if (editingShelf) {
                            shelves.editShelf({ id: editingShelf.id, name: data.name });
                        } else {
                            shelves.saveShelf({ name: data.name });
                        }
                        setIsFormOpen(false);
                    }}
                    onClose={() => setIsFormOpen(false)}
                />
            )}
            <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between px-2">
                    <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50">
                        Półki
                    </h2>
                    <button 
                        className="p-1.5 rounded-md bg-zinc-900 dark:bg-zinc-100 text-white dark:text-zinc-900 hover:opacity-80 transition-opacity"
                        onClick={() => handleAddShelf()}
                    >
                        <Plus size={18} />
                    </button>
                </div>

                <ul className="space-y-1">
                    {shelves.shelves.content.map((shelf) => {
                        const isActive = shelf.id === activeShelfId;
                        return (
                            <li key={shelf.id} className="group relative">
                                <button 
                                    onClick={() => onSelectShelf(shelf.id)}
                                    className={`
                                        w-full text-left px-4 py-3 rounded-lg transition-all duration-200
                                        ${isActive 
                                            ? 'bg-zinc-200 dark:bg-zinc-800 text-zinc-900 dark:text-white shadow-sm' 
                                            : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-900'
                                        }
                                    `}
                                >
                                    <div className="flex justify-between items-center pr-16">
                                        <span className="font-medium truncate">{shelf.name}</span>
                                        <span className="text-xs px-2 py-1 rounded-full text-zinc-600 dark:text-zinc-400 bg-zinc-100 dark:bg-zinc-800 group-hover:bg-zinc-200 group-hover:text-blue-800">
                                            {shelf.itemsCount}
                                        </span>
                                    </div>
                                </button>
                                <div className="absolute right-2 top-1/2 -translate-y-1/2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button onClick={() => handleEditShelf(shelf)} className="p-1.5 hover:bg-zinc-300 dark:hover:bg-zinc-700 rounded-md text-zinc-600 dark:text-zinc-400" title="Edytuj">
                                        <Pencil size={14} />
                                    </button>
                                    <button onClick={() => shelves.deleteShelf(shelf.id)} className="p-1.5 hover:bg-red-100 dark:hover:bg-red-900/30 rounded-md text-red-600" title="Usuń">
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
