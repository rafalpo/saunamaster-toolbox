interface UsageFactsListProps {
    itemId: string;
};

export default function UsageFactsList({ itemId }: UsageFactsListProps) {
    return (
        <div className="p-8 border-2 border-dashed border-zinc-200 dark:border-zinc-800 rounded-lg text-center text-zinc-500">
        Miejsce na listę faktów użycia przedmiotu: {itemId}
        </div>
    );
}