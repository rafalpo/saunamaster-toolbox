interface DateTimeEntryProps {
    dateTime: string;
}

export default function DateTimeEntry({ dateTime }: DateTimeEntryProps) {
    const date = new Date(dateTime);
    return (
        <span>{date.toLocaleString()}</span>
    );
}