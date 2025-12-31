import axios from "axios"

export const api = axios.create({
    baseURL: '/api/proxy',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.response.use(
    (response) => response.data,
    (error) => {
        console.error(error);
        return Promise.reject(error);
    }
)