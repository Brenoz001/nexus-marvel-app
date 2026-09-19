import { useMemo } from 'react';
import { getTeams } from '@/services/comicVineApi';
import { onlyMarvel } from '@/utils/marvelFilter';
import usePaginatedList from './usePaginatedList';
import type { CVTeam } from '@/types';

const FIELD_LIST =
  'id,name,deck,image,publisher,count_of_issue_appearances,count_of_team_members,characters';

interface Options {
  marvelOnly?: boolean;
  pageSize?: number;
  enabled?: boolean;
}

export function useTeams({ marvelOnly = true, pageSize = 20, enabled = true }: Options = {}) {
  const filter = useMemo(
    () => (marvelOnly ? (items: CVTeam[]) => onlyMarvel(items) : undefined),
    [marvelOnly]
  );

  return usePaginatedList<CVTeam>({
    fetcher: getTeams,
    params: { sort: 'count_of_issue_appearances:desc', field_list: FIELD_LIST },
    pageSize,
    filter,
    enabled,
    deps: [marvelOnly],
  });
}

export default useTeams;
