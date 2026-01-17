import { Pencil, Trash2 } from "lucide-react";
import { ItemResponse } from "../hooks/useItems";

interface ItemListEntryProps {
    item: ItemResponse;
    isActive: boolean;
    onSelectItem?: (id: string) => void;
    handleEditItem: (item: ItemResponse) => void;
    handleDeleteItem: (item: ItemResponse) => void;
};

export default function ItemListEntry({ item, isActive, onSelectItem, handleEditItem, handleDeleteItem }: ItemListEntryProps) {
    return (
        <li className="group relative">
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
}
