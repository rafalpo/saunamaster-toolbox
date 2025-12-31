"use client";

import { MessageResponse, useTest } from "../hooks/useTest";

export default function TestComponent() {
    const result: MessageResponse = useTest();

    return (
        <h2 className="max-w-xs text-3xl font-semibold leading-10 tracking-tight text-black dark:text-zinc-50">
            {result.data.message}
        </h2>
    );
}