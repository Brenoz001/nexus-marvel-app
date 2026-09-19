/**
 * Helpers for filtering Comic Vine results down to the Marvel universe and
 * classifying characters as heroes / villains (a heuristic based on how many
 * friends vs enemies they have plus a curated villain list).
 */

import type { CVCharacter } from '@/types';

/** True when the entity's publisher is Marvel Comics. */
export function isMarvel(entity: { publisher?: { name?: string } | null }): boolean {
  const name = entity.publisher?.name?.toLowerCase() ?? '';
  return name.includes('marvel');
}

/** Keep only Marvel entities from a list. */
export function onlyMarvel<T extends { publisher?: { name?: string } | null }>(
  items: T[]
): T[] {
  return items.filter(isMarvel);
}

/** Curated set of well-known Marvel villains (lowercased). */
const KNOWN_VILLAINS = new Set([
  'thanos',
  'loki',
  'magneto',
  'venom',
  'green goblin',
  'doctor doom',
  'doctor octopus',
  'red skull',
  'ultron',
  'kingpin',
  'carnage',
  'kang',
  'galactus',
  'mystique',
  'sabretooth',
  'juggernaut',
  'apocalypse',
  'dormammu',
  'hela',
  'mysterio',
  'vulture',
  'rhino',
  'sandman',
]);

/**
 * Rough hero/villain classification.
 * We can't fully trust the API, so we combine a curated list with the ratio of
 * enemies to friends.
 */
export function classifyAlignment(character: CVCharacter): 'hero' | 'villain' {
  const name = character.name?.toLowerCase() ?? '';
  if (KNOWN_VILLAINS.has(name)) return 'villain';
  if ([...KNOWN_VILLAINS].some((v) => name.includes(v))) return 'villain';

  const friends = character.character_friends?.length ?? 0;
  const enemies =
    character.character_enemies?.length ?? character.enemies?.length ?? 0;
  if (enemies > 0 && enemies > friends * 1.5) return 'villain';
  return 'hero';
}

export function isHero(character: CVCharacter): boolean {
  return classifyAlignment(character) === 'hero';
}

export function isVillain(character: CVCharacter): boolean {
  return classifyAlignment(character) === 'villain';
}
