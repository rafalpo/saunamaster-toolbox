import { useShelves } from "../hooks/useShelves";

interface ShelvesListProps {
    onSelectShelf: (id: string) => void;
    activeShelfId?: string;
}

export default function ShelvesList({ onSelectShelf, activeShelfId }: ShelvesListProps) {
    const shelves = useShelves();

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50 px-2">
                Półki
            </h2>
            <ul className="space-y-1">
                {shelves.data.content.map((shelf) => {
                    const isActive = shelf.id === activeShelfId;
                    return (
                        <li key={shelf.id}>
                            <button 
                                onClick={() => onSelectShelf(shelf.id)}
                                className={`
                                    w-full text-left px-4 py-3 rounded-lg transition-all duration-200 group
                                    ${isActive 
                                        ? 'bg-zinc-200 dark:bg-zinc-800 text-zinc-900 dark:text-white shadow-sm' 
                                        : 'text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-900 hover:text-zinc-900 dark:hover:text-zinc-200'
                                    }
                                `}
                            >
                                <div className="flex justify-between items-center">
                                    <span className="font-medium">{shelf.name}</span>
                                    <span className={`
                                        text-xs px-2 py-1 rounded-full 
                                        ${isActive ? 'bg-zinc-300 dark:bg-zinc-700' : 'bg-zinc-100 dark:bg-zinc-800'}
                                    `}>
                                        {shelf.itemsCount}
                                    </span>
                                </div>
                            </button>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}