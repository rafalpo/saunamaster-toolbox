import { useSuspenseQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface ShelfResponse {
    id: string;
    name: string;
    itemsCount: number;
};

export interface ShelvesPagedResponse {
    content: ShelfResponse[];
    totalElements: number;
    totalPages: number;
    number: number;
};

export const useShelves = (page: number = 0, limit: number = 25) => {
    const queryClient = useQueryClient();

    const query = useSuspenseQuery<ShelvesPagedResponse>({
        queryKey: ['shelves', page, limit],
        queryFn: () => api.get(`/shelves?page=${page}&size=${limit}&sort=name,desc`),
    });

    const saveMutation = useMutation({
        mutationFn: (data: { name: string }) => api.post('/shelves', data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        },
    });

    const editMutation = useMutation({
        mutationFn: (data: { id: string; name: string }) => api.put(`/shelves/${data.id}`, { name: data.name }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        },
    });

    const deleteMutation = useMutation({
        mutationFn: (id: string) => api.delete(`/shelves/${id}`),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        },
    });

    return {
        shelves: query.data,
        saveShelf: saveMutation.mutateAsync,
        editShelf: editMutation.mutateAsync,
        deleteShelf: deleteMutation.mutateAsync,
        isPending: saveMutation.isPending || editMutation.isPending || deleteMutation.isPending,
    };
};
