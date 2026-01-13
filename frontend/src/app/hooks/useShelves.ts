import { useSuspenseQuery } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface ShelfResponse {
    id: string;
    name: string;
    itemsCount: number;
};

export interface ShelvesPagedResponse {
    data: {
        content: ShelfResponse[];
        totalElements: number;
        totalPages: number;
        number: number;
    }
};

export const useShelves = (page: number = 0, limit: number = 25): ShelvesPagedResponse => {
    return useSuspenseQuery({
        queryKey: ['shelves'],
        queryFn: () => api.get(`/shelves?page=${page}&size=${limit}&sort=name,desc`),
    });
};
