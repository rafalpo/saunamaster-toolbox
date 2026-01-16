import { useSuspenseQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface ItemResponse {
    id: string;
    name: string;
};

export interface ItemsPagedResponse {
    content: ItemResponse[];
    totalElements: number;
    totalPages: number;
    number: number;
};

export const useItems = (shelfId: string, page: number = 0, limit: number = 25) => {
    const queryClient = useQueryClient();

    const query = useSuspenseQuery<ItemsPagedResponse>({
        queryKey: ['items', shelfId, page, limit],
        queryFn: () => api.get(`/shelves/${shelfId}/items?page=${page}&size=${limit}&sort=name,desc`),
    });

    const saveItem = useMutation({
        mutationFn: (data: { name: string }) => api.post(`/shelves/${shelfId}/items`, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['items', shelfId] });
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        }
    });

    const editItem = useMutation({
        mutationFn: ({ id, name }: { id: string; name: string }) => api.put(`/shelves/${shelfId}/items/${id}`, { name }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['items', shelfId] });
        }
    });

    const deleteItem = useMutation({
        mutationFn: (id: string) => api.delete(`/shelves/${shelfId}/items/${id}`),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['items', shelfId] });
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        }
    });

    return {
        items: query.data,
        saveItem: saveItem.mutateAsync,
        editItem: editItem.mutateAsync,
        deleteItem: deleteItem.mutateAsync,
        isPending: saveItem.isPending || editItem.isPending || deleteItem.isPending,
    };
};