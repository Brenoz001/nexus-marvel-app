/**
 * Generic paginated-list hook built on top of any Comic Vine list endpoint.
 * Powers useTeams / useStoryArcs / usePowers with a single implementation.
 */

import { useCallback, useEffect, useRef, useState } from 'react';
import { ComicVineError } from '@/services/comicVineApi';
import type { CVListResponse, ListParams } from '@/types';

type Fetcher<T> = (params: ListParams) => Promise<CVListResponse<T>>;

interface Options<T> {
  fetcher: Fetcher<T>;
  params?: ListParams;
  pageSize?: number;
  /** Optional client-side filter (e.g. Marvel only). */
  filter?: (items: T[]) => T[];
  /** Re-run when any of these change. */
  deps?: unknown[];
  enabled?: boolean;
}

interface Result<T> {
  data: T[];
  loading: boolean;
  loadingMore: boolean;
  refreshing: boolean;
  error: string | null;
  hasMore: boolean;
  loadMore: () => void;
  refresh: () => void;
  retry: () => void;
}

export function usePaginatedList<T extends { id: number }>({
  fetcher,
  params = {},
  pageSize = 20,
  filter,
  deps = [],
  enabled = true,
}: Options<T>): Result<T> {
  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(true);

  const offsetRef = useRef(0);
  const totalRef = useRef(Infinity);
  const requestId = useRef(0);
  const inFlight = useRef(false);

  const fetchPage = useCallback(
    async (mode: 'initial' | 'more' | 'refresh') => {
      if (!enabled) return;
      if (inFlight.current && mode === 'more') return;
      inFlight.current = true;
      const currentRequest = ++requestId.current;

      if (mode === 'initial') setLoading(true);
      if (mode === 'more') setLoadingMore(true);
      if (mode === 'refresh') setRefreshing(true);
      setError(null);

      const offset = mode === 'more' ? offsetRef.current : 0;

      try {
        const response = await fetcher({ ...params, limit: pageSize, offset });
        if (currentRequest !== requestId.current) return;

        const items = filter ? filter(response.results) : response.results;
        totalRef.current = response.number_of_total_results;
        offsetRef.current = offset + response.results.length;

        setData((prev) => (mode === 'more' ? dedupe([...prev, ...items]) : items));
        setHasMore(offsetRef.current < totalRef.current && response.results.length > 0);
      } catch (err) {
        if (currentRequest !== requestId.current) return;
        setError(err instanceof ComicVineError ? err.message : 'Falha ao carregar dados.');
        if (mode === 'initial') setData([]);
      } finally {
        if (currentRequest === requestId.current) {
          setLoading(false);
          setLoadingMore(false);
          setRefreshing(false);
        }
        inFlight.current = false;
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [fetcher, pageSize, enabled, ...deps]
  );

  useEffect(() => {
    if (!enabled) return;
    offsetRef.current = 0;
    totalRef.current = Infinity;
    fetchPage('initial');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enabled, ...deps]);

  const loadMore = useCallback(() => {
    if (!hasMore || loading || loadingMore || refreshing) return;
    fetchPage('more');
  }, [hasMore, loading, loadingMore, refreshing, fetchPage]);

  const refresh = useCallback(() => {
    offsetRef.current = 0;
    fetchPage('refresh');
  }, [fetchPage]);

  const retry = useCallback(() => {
    offsetRef.current = 0;
    fetchPage('initial');
  }, [fetchPage]);

  return { data, loading, loadingMore, refreshing, error, hasMore, loadMore, refresh, retry };
}

function dedupe<T extends { id: number }>(items: T[]): T[] {
  const seen = new Set<number>();
  return items.filter((i) => {
    if (seen.has(i.id)) return false;
    seen.add(i.id);
    return true;
  });
}

export default usePaginatedList;
