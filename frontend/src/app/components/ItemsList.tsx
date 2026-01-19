"use client";

import { useState } from "react";
import { ItemResponse, useItems } from "../hooks/useItems";
import { Plus } from "lucide-react";
import ItemForm from "./ItemForm";
import ItemListEntry from "./ItemListEntry";

interface ItemsListProps {
    shelfId: string;
    activeItemId: string;
    onSelectItem: (id: string | undefined) => void;
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
            if (activeItemId === item.id) {
                onSelectItem(undefined);
            }
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
                            <ItemListEntry
                                key={item.id}
                                item={item}
                                isActive={isActive}
                                onSelectItem={onSelectItem}
                                handleEditItem={handleEditItem}
                                handleDeleteItem={handleDeleteItem}
                            />
                        );
                    })}
                </ul>
            </div>
        </>
    );
}