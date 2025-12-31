import { useSuspenseQuery } from "@tanstack/react-query"
import { api } from "../lib/api";

export interface MessageResponse {
    data: {
        message: string;
    };
};

export const useTest = (): MessageResponse => {
    return useSuspenseQuery({
        queryKey: ['test'],
        queryFn: () => api.get('/test'),
    });
}