"use client";

import dynamic from "next/dynamic";
import { useState } from "react";

const ShelvesList = dynamic(() => import('@/app/components/ShelvesList'), { ssr: false });
const ItemsList = dynamic(() => import('@/app/components/ItemsList'), { ssr: false });
const UsageFactsList = dynamic(() => import('@/app/components/UsageFactsList'), { ssr: false });

export default function CRUDPage() {
  const [selectedShelfId, setSelectedShelfId] = useState<string | undefined>(undefined);
  const [selectedItemId, setSelectedItemId] = useState<string | undefined>(undefined);

  return (
    <main className="h-screen flex flex-col bg-zinc-50 dark:bg-zinc-950 overflow-hidden">
      
      <header className="h-16 flex-none border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 px-6 flex items-center">
        <h1 className="text-xl font-bold text-zinc-900 dark:text-white">ShelfManager Pro</h1>
      </header>

      <div className="flex-1 flex flex-col md:flex-row overflow-hidden">
        
        <section className="w-full md:w-1/3 lg:w-1/4 flex flex-col border-r border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-zinc-950">
          <div className="flex-1 overflow-y-auto p-4">
            <ShelvesList 
              onSelectShelf={(id) => {
                setSelectedShelfId(id);
                setSelectedItemId(undefined);
              }} 
              activeShelfId={selectedShelfId}
            />
          </div>
        </section>

        <section className="w-full md:w-1/3 lg:w-1/4 flex flex-col border-r border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
          <div className="flex-1 overflow-y-auto p-4">
            {selectedShelfId ? (
              <ItemsList 
                shelfId={selectedShelfId} 
                onSelectItem={setSelectedItemId}
                activeItemId={selectedItemId}
              />
            ) : (
              <div className="flex items-center justify-center h-full text-zinc-500 text-sm italic">
                Wybierz półkę...
              </div>
            )}
          </div>
        </section>

        <section className="hidden md:flex md:flex-1 flex-col bg-zinc-50 dark:bg-zinc-950">
          <div className="flex-1 overflow-y-auto p-6">
            {selectedItemId ? (
              <UsageFactsList itemId={selectedItemId} />
            ) : (
              <div className="flex items-center justify-center h-full text-zinc-500 text-sm italic">
                Wybierz przedmiot z listy...
              </div>
            )}
          </div>
        </section>

      </div>
    </main>
  );
}