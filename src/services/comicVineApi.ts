/**
 * Centralized Comic Vine API client for NEXUS.
 *
 * Responsibilities:
 *  - Single axios instance with baseURL, api_key and default query/headers.
 *  - In-memory cache (5 min TTL) to avoid repeated requests.
 *  - Client-side rate limiting (max 1 request / second).
 *  - Standardized, typed error handling.
 *  - Typed helper functions per endpoint.
 *
 * Docs: https://comicvine.gamespot.com/api/documentation
 */

import axios, { AxiosError, AxiosInstance } from 'axios';
import memoryCache from './cache';
import type {
  CVCharacter,
  CVDetailResponse,
  CVIssue,
  CVListResponse,
  CVLocation,
  CVPower,
  CVStoryArc,
  CVTeam,
  ListParams,
} from '@/types';

const BASE_URL = 'https://comicvine.gamespot.com/api';
const API_KEY = process.env.EXPO_PUBLIC_COMIC_VINE_API_KEY ?? '';
const MIN_REQUEST_INTERVAL = 1000; // 1 request per second
const CACHE_TTL = 5 * 60 * 1000; // 5 minutes

/** Comic Vine resource id prefixes (used to build detail URLs). */
export const RESOURCE_PREFIX = {
  character: '4005',
  team: '4060',
  story_arc: '4045',
  location: '4020',
  issue: '4000',
  power: '4035',
  publisher: '4010',
  concept: '4015',
} as const;

/** Marvel Comics publisher id. */
export const MARVEL_PUBLISHER_ID = 31;

/** A normalized error thrown by every service function. */
export class ComicVineError extends Error {
  readonly statusCode?: number;
  readonly isNetwork: boolean;

  constructor(message: string, statusCode?: number, isNetwork = false) {
    super(message);
    this.name = 'ComicVineError';
    this.statusCode = statusCode;
    this.isNetwork = isNetwork;
  }
}

/** True when no API key has been configured yet. */
export function isApiKeyConfigured(): boolean {
  return API_KEY.length > 0 && API_KEY !== 'your_api_key_here';
}

// ---------------------------------------------------------------------------
// axios instance
// ---------------------------------------------------------------------------

const client: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  params: {
    api_key: API_KEY,
    format: 'json',
  },
  headers: {
    // Comic Vine requires a descriptive, unique User-Agent.
    'User-Agent': 'NexusMarvelApp/1.0',
  },
});

// ---------------------------------------------------------------------------
// Rate limiter: serialize requests with a minimum interval between them.
// ---------------------------------------------------------------------------

let lastRequestAt = 0;
let queue: Promise<unknown> = Promise.resolve();

function schedule<T>(task: () => Promise<T>): Promise<T> {
  const run = async (): Promise<T> => {
    const now = Date.now();
    const wait = Math.max(0, lastRequestAt + MIN_REQUEST_INTERVAL - now);
    if (wait > 0) {
      await new Promise((resolve) => setTimeout(resolve, wait));
    }
    lastRequestAt = Date.now();
    return task();
  };
  // Chain onto the queue so requests fire one after another.
  const result = queue.then(run, run);
  queue = result.catch(() => undefined);
  return result;
}

// ---------------------------------------------------------------------------
// Core request helper (cache + rate limit + error handling)
// ---------------------------------------------------------------------------

function cacheKey(path: string, params: Record<string, unknown>): string {
  return `${path}?${JSON.stringify(params)}`;
}

async function request<T>(
  path: string,
  params: Record<string, unknown> = {}
): Promise<T> {
  if (!isApiKeyConfigured()) {
    throw new ComicVineError(
      'Comic Vine API key não configurada. Adicione EXPO_PUBLIC_COMIC_VINE_API_KEY ao arquivo .env.'
    );
  }

  const key = cacheKey(path, params);
  const cached = memoryCache.get<T>(key);
  if (cached !== undefined) return cached;

  return schedule(async () => {
    // Re-check cache in case a concurrent call populated it while queued.
    const fresh = memoryCache.get<T>(key);
    if (fresh !== undefined) return fresh;

    try {
      const response = await client.get<T & { error?: string; status_code?: number }>(
        path,
        { params }
      );
      const data = response.data;

      if (data && data.error && data.error !== 'OK') {
        throw new ComicVineError(
          `Comic Vine respondeu com erro: ${data.error}`,
          data.status_code
        );
      }

      memoryCache.set<T>(key, data as T, CACHE_TTL);
      return data as T;
    } catch (err) {
      throw normalizeError(err);
    }
  });
}

function normalizeError(err: unknown): ComicVineError {
  if (err instanceof ComicVineError) return err;

  const axiosErr = err as AxiosError;
  if (axiosErr?.isAxiosError) {
    if (axiosErr.response) {
      const status = axiosErr.response.status;
      if (status === 401 || status === 403) {
        return new ComicVineError(
          'Acesso negado pela Comic Vine. Verifique sua API key.',
          status
        );
      }
      if (status === 420 || status === 429) {
        return new ComicVineError(
          'Limite de requisições atingido. Tente novamente em instantes.',
          status
        );
      }
      return new ComicVineError(
        `Erro ${status} ao consultar a Comic Vine.`,
        status
      );
    }
    if (axiosErr.code === 'ECONNABORTED') {
      return new ComicVineError('A requisição demorou demais. Tente de novo.', undefined, true);
    }
    return new ComicVineError(
      'Sem conexão com a Comic Vine. Verifique sua internet.',
      undefined,
      true
    );
  }

  return new ComicVineError('Ocorreu um erro inesperado.');
}

/** Build the standard list params, dropping undefined values. */
function listParams(params: ListParams = {}): Record<string, unknown> {
  const out: Record<string, unknown> = {
    limit: params.limit ?? 20,
    offset: params.offset ?? 0,
  };
  if (params.filter) out.filter = params.filter;
  if (params.sort) out.sort = params.sort;
  if (params.field_list) out.field_list = params.field_list;
  return out;
}

// ---------------------------------------------------------------------------
// Endpoints
// ---------------------------------------------------------------------------

export async function getCharacters(
  params: ListParams = {}
): Promise<CVListResponse<CVCharacter>> {
  return request<CVListResponse<CVCharacter>>('/characters/', listParams(params));
}

export async function getCharacterById(
  id: number,
  fieldList?: string
): Promise<CVCharacter> {
  const params: Record<string, unknown> = {};
  if (fieldList) params.field_list = fieldList;
  const res = await request<CVDetailResponse<CVCharacter>>(
    `/character/${RESOURCE_PREFIX.character}-${id}/`,
    params
  );
  return res.results;
}

export async function searchCharacters(
  query: string,
  params: ListParams = {}
): Promise<CVListResponse<CVCharacter>> {
  return getCharacters({
    ...params,
    filter: `name:${query}`,
  });
}

export async function getTeams(
  params: ListParams = {}
): Promise<CVListResponse<CVTeam>> {
  return request<CVListResponse<CVTeam>>('/teams/', listParams(params));
}

export async function getTeamById(id: number, fieldList?: string): Promise<CVTeam> {
  const params: Record<string, unknown> = {};
  if (fieldList) params.field_list = fieldList;
  const res = await request<CVDetailResponse<CVTeam>>(
    `/team/${RESOURCE_PREFIX.team}-${id}/`,
    params
  );
  return res.results;
}

export async function getStoryArcs(
  params: ListParams = {}
): Promise<CVListResponse<CVStoryArc>> {
  return request<CVListResponse<CVStoryArc>>('/story_arcs/', listParams(params));
}

export async function getStoryArcById(
  id: number,
  fieldList?: string
): Promise<CVStoryArc> {
  const params: Record<string, unknown> = {};
  if (fieldList) params.field_list = fieldList;
  const res = await request<CVDetailResponse<CVStoryArc>>(
    `/story_arc/${RESOURCE_PREFIX.story_arc}-${id}/`,
    params
  );
  return res.results;
}

export async function getPowers(
  params: ListParams = {}
): Promise<CVListResponse<CVPower>> {
  return request<CVListResponse<CVPower>>('/powers/', listParams(params));
}

export async function getLocations(
  params: ListParams = {}
): Promise<CVListResponse<CVLocation>> {
  return request<CVListResponse<CVLocation>>('/locations/', listParams(params));
}

export async function getIssues(
  params: ListParams = {}
): Promise<CVListResponse<CVIssue>> {
  return request<CVListResponse<CVIssue>>('/issues/', listParams(params));
}

export const comicVineApi = {
  getCharacters,
  getCharacterById,
  searchCharacters,
  getTeams,
  getTeamById,
  getStoryArcs,
  getStoryArcById,
  getPowers,
  getLocations,
  getIssues,
  isApiKeyConfigured,
};

export default comicVineApi;
