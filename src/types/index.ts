/**
 * Type definitions for the Comic Vine API entities used by NEXUS.
 * Docs: https://comicvine.gamespot.com/api/documentation
 */

/** Standard Comic Vine image object. */
export interface CVImage {
  icon_url?: string;
  medium_url?: string;
  screen_url?: string;
  screen_large_url?: string;
  small_url?: string;
  super_url?: string;
  thumb_url?: string;
  tiny_url?: string;
  original_url?: string;
  image_tags?: string;
}

/** A lightweight reference to another resource (appears in nested arrays). */
export interface CVRef {
  id: number;
  name: string;
  api_detail_url?: string;
  site_detail_url?: string;
  count?: string | number;
}

export interface CVPublisher extends CVRef {}

export interface CVIssueRef {
  id: number;
  name?: string | null;
  issue_number?: string;
  api_detail_url?: string;
}

/** Character resource. */
export interface CVCharacter {
  id: number;
  name: string;
  real_name?: string | null;
  aliases?: string | null;
  deck?: string | null;
  description?: string | null;
  image?: CVImage;
  publisher?: CVPublisher | null;
  powers?: CVRef[];
  teams?: CVRef[];
  character_friends?: CVRef[];
  character_enemies?: CVRef[];
  /** Some payloads expose enemies under this alias. */
  enemies?: CVRef[];
  count_of_issue_appearances?: number;
  first_appeared_in_issue?: CVIssueRef | null;
  birth?: string | null;
  death?: { date?: string } | null;
  gender?: number;
  origin?: CVRef | null;
  story_arc_credits?: CVRef[];
  api_detail_url?: string;
  site_detail_url?: string;
  date_added?: string;
}

/** Team resource. */
export interface CVTeam {
  id: number;
  name: string;
  deck?: string | null;
  description?: string | null;
  image?: CVImage;
  publisher?: CVPublisher | null;
  characters?: CVRef[];
  count_of_issue_appearances?: number;
  count_of_team_members?: number;
  first_appeared_in_issue?: CVIssueRef | null;
  api_detail_url?: string;
  site_detail_url?: string;
}

/** Story arc resource. */
export interface CVStoryArc {
  id: number;
  name: string;
  deck?: string | null;
  description?: string | null;
  image?: CVImage;
  publisher?: CVPublisher | null;
  issues?: CVRef[];
  count_of_issue_appearances?: number;
  first_appeared_in_issue?: CVIssueRef | null;
  characters?: CVRef[];
  api_detail_url?: string;
  site_detail_url?: string;
}

/** Power resource. */
export interface CVPower {
  id: number;
  name: string;
  description?: string | null;
  characters?: CVRef[];
  api_detail_url?: string;
}

/** Location resource. */
export interface CVLocation {
  id: number;
  name: string;
  deck?: string | null;
  description?: string | null;
  image?: CVImage;
  first_appeared_in_issue?: CVIssueRef | null;
  count_of_issue_appearances?: number;
  api_detail_url?: string;
}

/** Issue resource. */
export interface CVIssue {
  id: number;
  name?: string | null;
  issue_number?: string;
  cover_date?: string | null;
  image?: CVImage;
  volume?: CVRef;
  character_credits?: CVRef[];
  person_credits?: CVRef[];
  api_detail_url?: string;
}

/** Generic list response envelope. */
export interface CVListResponse<T> {
  error: string;
  limit: number;
  offset: number;
  number_of_page_results: number;
  number_of_total_results: number;
  status_code: number;
  results: T[];
}

/** Generic single-object response envelope. */
export interface CVDetailResponse<T> {
  error: string;
  limit: number;
  offset: number;
  number_of_page_results: number;
  number_of_total_results: number;
  status_code: number;
  results: T;
}

/** Options accepted by list endpoints. */
export interface ListParams {
  limit?: number;
  offset?: number;
  filter?: string;
  sort?: string;
  field_list?: string;
}

/** The six radar categories used in Confronto / DNA de Poderes. */
export type PowerCategory =
  | 'Força'
  | 'Velocidade'
  | 'Inteligência'
  | 'Energia'
  | 'Resistência'
  | 'Habilidade de Combate';

export type PowerScores = Record<PowerCategory, number>;

/** Soundtrack entry from data/soundtracks.json. */
export interface Soundtrack {
  track: string;
  artist: string;
  movie: string;
}

/** A favorited entity persisted to AsyncStorage. */
export interface FavoriteItem {
  id: number;
  name: string;
  type: 'character' | 'team' | 'arc';
  imageUrl?: string;
  publisher?: string;
  addedAt: number;
}
