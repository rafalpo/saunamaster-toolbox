"use client";

import { useForm } from "react-hook-form";

interface ItemFormProps {
  initialData?: { name: string };
  onSubmit: (data: { name: string }) => void;
  onClose: () => void;
}

export default function ItemForm({ initialData, onSubmit, onClose }: ItemFormProps) {
  const { register, handleSubmit } = useForm({
    defaultValues: initialData || { name: "" }
  });

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
      <div className="bg-white dark:bg-zinc-900 w-full max-w-md rounded-xl shadow-2xl border border-zinc-200 dark:border-zinc-800">
        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <h3 className="text-xl font-semibold mb-6 text-zinc-900 dark:text-zinc-50">
            {initialData ? "Edytuj przedmiot" : "Dodaj nowy przedmiot"}
          </h3>
          
          <div className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium mb-1 text-zinc-700 dark:text-zinc-300">
                Nazwa przedmiotu
              </label>
              <input 
                {...register("name", { required: true })}
                autoFocus
                className="w-full px-3 py-2 rounded-lg border border-zinc-300 dark:border-zinc-700 bg-transparent focus:ring-2 focus:ring-blue-500 outline-none"
                placeholder="np. Karafka do wody"
              />
            </div>
          </div>

          <div className="mt-8 flex justify-end gap-3">
            <button 
              type="button" 
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-zinc-600 dark:text-zinc-400 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-lg transition-colors"
            >
              Anuluj
            </button>
            <button 
              type="submit"
              className="px-4 py-2 text-sm font-medium bg-blue-600 text-white hover:bg-blue-700 rounded-lg shadow-lg shadow-blue-500/20 transition-colors"
            >
              {initialData ? "Zapisz zmiany" : "Utwórz przedmiot"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}