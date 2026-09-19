/**
 * Paginated characters hook for the Explore screen.
 * Handles search, infinite scroll, pull-to-refresh, loading and error states.
 */

import { useCallback, useEffect, useRef, useState } from 'react';
import { getCharacters, searchCharacters, ComicVineError } from '@/services/comicVineApi';
import { classifyAlignment, onlyMarvel } from '@/utils/marvelFilter';
import type { CVCharacter } from '@/types';

export type AlignmentFilter = 'all' | 'hero' | 'villain';

const FIELD_LIST =
  'id,name,real_name,deck,image,publisher,count_of_issue_appearances,powers,teams,character_friends,character_enemies,first_appeared_in_issue';

interface UseCharactersOptions {
  query?: string;
  alignment?: AlignmentFilter;
  pageSize?: number;
  /** Only keep Marvel characters (default true). */
  marvelOnly?: boolean;
}

interface UseCharactersResult {
  characters: CVCharacter[];
  loading: boolean;
  loadingMore: boolean;
  refreshing: boolean;
  error: string | null;
  hasMore: boolean;
  loadMore: () => void;
  refresh: () => void;
  retry: () => void;
}

export function useCharacters({
  query = '',
  alignment = 'all',
  pageSize = 20,
  marvelOnly = true,
}: UseCharactersOptions = {}): UseCharactersResult {
  const [characters, setCharacters] = useState<CVCharacter[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(true);

  const offsetRef = useRef(0);
  const totalRef = useRef(Infinity);
  // Guards against overlapping requests / stale responses.
  const requestId = useRef(0);
  const inFlight = useRef(false);

  const applyFilters = useCallback(
    (items: CVCharacter[]): CVCharacter[] => {
      let out = marvelOnly ? onlyMarvel(items) : items;
      if (alignment !== 'all') {
        out = out.filter((c) => classifyAlignment(c) === alignment);
      }
      return out;
    },
    [alignment, marvelOnly]
  );

  const fetchPage = useCallback(
    async (mode: 'initial' | 'more' | 'refresh') => {
      if (inFlight.current && mode === 'more') return;
      inFlight.current = true;
      const currentRequest = ++requestId.current;

      if (mode === 'initial') setLoading(true);
      if (mode === 'more') setLoadingMore(true);
      if (mode === 'refresh') setRefreshing(true);
      setError(null);

      const offset = mode === 'more' ? offsetRef.current : 0;

      try {
        const trimmed = query.trim();
        const response = trimmed
          ? await searchCharacters(trimmed, {
              limit: pageSize,
              offset,
              field_list: FIELD_LIST,
            })
          : await getCharacters({
              limit: pageSize,
              offset,
              sort: 'count_of_issue_appearances:desc',
              field_list: FIELD_LIST,
            });

        // Ignore responses from superseded requests.
        if (currentRequest !== requestId.current) return;

        const filtered = applyFilters(response.results);
        totalRef.current = response.number_of_total_results;
        offsetRef.current = offset + response.results.length;

        setCharacters((prev) => (mode === 'more' ? dedupe([...prev, ...filtered]) : filtered));
        setHasMore(offsetRef.current < totalRef.current && response.results.length > 0);
      } catch (err) {
        if (currentRequest !== requestId.current) return;
        const message =
          err instanceof ComicVineError ? err.message : 'Falha ao carregar personagens.';
        setError(message);
        if (mode === 'initial') setCharacters([]);
      } finally {
        if (currentRequest === requestId.current) {
          setLoading(false);
          setLoadingMore(false);
          setRefreshing(false);
        }
        inFlight.current = false;
      }
    },
    [applyFilters, pageSize, query]
  );

  // Reload from scratch whenever the query or filter changes.
  useEffect(() => {
    offsetRef.current = 0;
    totalRef.current = Infinity;
    fetchPage('initial');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [query, alignment, marvelOnly]);

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

  return {
    characters,
    loading,
    loadingMore,
    refreshing,
    error,
    hasMore,
    loadMore,
    refresh,
    retry,
  };
}

function dedupe(items: CVCharacter[]): CVCharacter[] {
  const seen = new Set<number>();
  return items.filter((c) => {
    if (seen.has(c.id)) return false;
    seen.add(c.id);
    return true;
  });
}

export default useCharacters;
