import { Pencil, Trash2 } from "lucide-react";
import { ShelfResponse } from "../hooks/useShelves";

interface ShelfListEntryProps {
    shelf: ShelfResponse;
    isActive: boolean;
    onSelectShelf: (id: string) => void;
    handleEditShelf: (shelf: ShelfResponse) => void;
    handleDeleteShelf: (shelf: ShelfResponse) => void;
};

export function ShelfListEntry({ shelf, isActive, onSelectShelf, handleEditShelf, handleDeleteShelf }: ShelfListEntryProps) {
  return (
    <li className="group relative">
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
            <button onClick={() => handleDeleteShelf(shelf)} className="p-1.5 hover:bg-red-100 dark:hover:bg-red-900/30 rounded-md text-red-600" title="Usuń">
                <Trash2 size={14} />
            </button>
        </div>
    </li>
  );
}