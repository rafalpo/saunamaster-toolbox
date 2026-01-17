"use client";

import { useState } from "react";
import { ShelfResponse, useShelves } from "../hooks/useShelves";
import { Plus } from "lucide-react";
import ShelfForm from "./ShelfForm";
import { ShelfListEntry } from "./ShelfListEntry";

interface ShelvesListProps {
    onSelectShelf: (id: string|undefined) => void;
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

    const handleDeleteShelf = (shelf: ShelfResponse) => {
        if (confirm(`Czy na pewno chcesz usunąć ${shelf.name}?`)) {
            shelves.deleteShelf(shelf.id);
            onSelectShelf(undefined);
        }
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
                            <ShelfListEntry
                                key={shelf.id}
                                shelf={shelf}
                                isActive={isActive}
                                onSelectShelf={onSelectShelf}
                                handleEditShelf={handleEditShelf}
                                handleDeleteShelf={handleDeleteShelf}
                            />
                        );
                    })}
                </ul>
            </div>
        </>
    );
}
