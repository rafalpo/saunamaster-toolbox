import { NextRequest } from 'next/server';

type RouteContext = {
  params: Promise<{ params: string[] }>;
};

async function handler(
  request: NextRequest,
  context: RouteContext
) {
  const { params } = await context.params;
  const path = params.join('/');
  
  const backendUrl = `http://${process.env.BACKEND_URL || 'localhost:8080'}`;
  
  if (!backendUrl) {
    return new Response('Backend URL not configured', { status: 500 });
  }

  const targetUrl = `${backendUrl}/${path}${request.nextUrl.search}`;

  const requestOptions: RequestInit = {
    method: request.method,
    headers: request.headers,
    duplex: 'half', 
  } as RequestInit;

  if (request.method !== 'GET' && request.method !== 'HEAD') {
    requestOptions.body = request.body;
  }

  try {
    const response = await fetch(targetUrl, requestOptions);

    return new Response(response.body, {
      status: response.status,
      headers: response.headers,
    });
  } catch (error) {
    console.error('Proxy error:', error);
    return new Response('Error communicating with backend', { status: 502 });
  }
}

export const GET = handler;
export const POST = handler;
export const PUT = handler;
export const DELETE = handler;
export const PATCH = handler;
