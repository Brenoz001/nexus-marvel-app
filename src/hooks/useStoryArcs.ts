import { useMemo } from 'react';
import { getStoryArcs } from '@/services/comicVineApi';
import { onlyMarvel } from '@/utils/marvelFilter';
import usePaginatedList from './usePaginatedList';
import type { CVStoryArc } from '@/types';

const FIELD_LIST =
  'id,name,deck,image,publisher,count_of_issue_appearances,first_appeared_in_issue,issues';

interface Options {
  marvelOnly?: boolean;
  pageSize?: number;
  enabled?: boolean;
}

export function useStoryArcs({ marvelOnly = true, pageSize = 20, enabled = true }: Options = {}) {
  const filter = useMemo(
    () => (marvelOnly ? (items: CVStoryArc[]) => onlyMarvel(items) : undefined),
    [marvelOnly]
  );

  return usePaginatedList<CVStoryArc>({
    fetcher: getStoryArcs,
    params: { sort: 'count_of_issue_appearances:desc', field_list: FIELD_LIST },
    pageSize,
    filter,
    enabled,
    deps: [marvelOnly],
  });
}

export default useStoryArcs;
