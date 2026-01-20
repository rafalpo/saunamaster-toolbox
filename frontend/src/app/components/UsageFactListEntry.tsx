import { UsageFactResponse } from "../hooks/useUsageFacts";
import DateTimeEntry from "./global/DateTimeEntry";

interface UsageFactListEntryProps {
    item: UsageFactResponse;
};

export function UsageFactListEntry({ item }: UsageFactListEntryProps) {
  return (
    <li className="p-2 border border-zinc-200 dark:border-zinc-800 rounded bg-white dark:bg-zinc-900">
      <DateTimeEntry dateTime={item.usageTime} />
    </li>
  );
};
