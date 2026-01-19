import { useSuspenseQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface UsageFactResponse {
    id: string;
    usageTime: string;
};

export interface UsageFactPagedResponse {
    content: UsageFactResponse[];
    totalElements: number;
    totalPages: number;
    number: number;
};

export const useUsageFacts = (shelfId: string, itemId: string, page: number = 0, limit: number = 25) => {
    const queryClient = useQueryClient();

    const query = useSuspenseQuery<UsageFactPagedResponse>({
        queryKey: ['items', shelfId, itemId, page, limit],
        queryFn: () => api.get(`/shelves/${shelfId}/items/${itemId}/usage-facts?page=${page}&size=${limit}&sort=usageTime,desc`),
    });

    const saveItem = useMutation({
        mutationFn: () => api.post(`/shelves/${shelfId}/items/${itemId}/usage-facts`, {}),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['items', shelfId] });
            queryClient.invalidateQueries({ queryKey: ['shelves'] });
        }
    });

    return {
        items: query.data,
        saveItem: saveItem.mutateAsync,
        isPending: saveItem.isPending,
    };
};