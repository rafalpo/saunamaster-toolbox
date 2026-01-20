import { Plus } from "lucide-react";
import { useUsageFacts } from "../hooks/useUsageFacts";
import { UsageFactListEntry } from "./UsageFactListEntry";

interface UsageFactsListProps {
    shelfId: string | undefined;
    itemId: string | undefined;
};

export default function UsageFactsList({ shelfId, itemId }: UsageFactsListProps) {
    const usageFacts = useUsageFacts(shelfId, itemId);

    const handleAddUsageFact = async () => {
        await usageFacts.saveItem();
    }

    return (
        <>
            <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between px-2">
                    <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50">
                        Fakty Użycia ({usageFacts.items.totalElements})
                    </h2>
                    <button 
                        className="p-1.5 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors"
                        onClick={() => handleAddUsageFact()}
                    >
                        <Plus size={18} />
                    </button>
                </div>
                <ul className="space-y-1">
                    {usageFacts.items.content.map((usageFact) => {
                        return (
                            <UsageFactListEntry
                                key={usageFact.id}
                                item={usageFact}
                            />
                        );
                    })}
                </ul>
            </div>
        </>
    );
}