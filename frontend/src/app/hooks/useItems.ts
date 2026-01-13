import { useSuspenseQuery } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface ItemResponse {
    id: string;
    name: string;
};

export interface ItemsPagedResponse {
    data: {
        content: ItemResponse[];
        totalElements: number;
        totalPages: number;
        number: number;
    }
};

export const useItems = (shelfId: string, page: number = 0, limit: number = 25): ItemsPagedResponse => {
    return useSuspenseQuery({
        queryKey: ['items'],
        queryFn: () => api.get(`/shelves/${shelfId}/items?page=${page}&size=${limit}&sort=name,desc`),
    });
};
