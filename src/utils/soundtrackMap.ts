/**
 * Maps a character name to an iconic movie/trailer soundtrack.
 * Data lives in src/data/soundtracks.json.
 */

import soundtracks from '@/data/soundtracks.json';
import type { Soundtrack } from '@/types';

const map = soundtracks as Record<string, Soundtrack>;

/**
 * Resolve a soundtrack for a character. Tries an exact match first, then a
 * loose "name contains key / key contains name" match so "Spider-Man (2099)"
 * still resolves to the Spider-Man track.
 */
export function getSoundtrack(characterName?: string): Soundtrack | undefined {
  if (!characterName) return undefined;

  if (map[characterName]) return map[characterName];

  const lower = characterName.toLowerCase();
  const key = Object.keys(map).find((k) => {
    const kl = k.toLowerCase();
    return lower.includes(kl) || kl.includes(lower);
  });
  return key ? map[key] : undefined;
}

export default getSoundtrack;
