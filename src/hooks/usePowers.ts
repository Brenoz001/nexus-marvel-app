import { getPowers } from '@/services/comicVineApi';
import usePaginatedList from './usePaginatedList';
import type { CVPower } from '@/types';

const FIELD_LIST = 'id,name,description,characters';

interface Options {
  pageSize?: number;
  enabled?: boolean;
}

export function usePowers({ pageSize = 30, enabled = true }: Options = {}) {
  return usePaginatedList<CVPower>({
    fetcher: getPowers,
    params: { field_list: FIELD_LIST },
    pageSize,
    enabled,
  });
}

export default usePowers;
