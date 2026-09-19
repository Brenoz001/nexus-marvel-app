/**
 * Maps Comic Vine power names to the six NEXUS radar categories and computes a
 * 0-10 score per category for a given set of powers.
 *
 * Categories: Força, Velocidade, Inteligência, Energia, Resistência,
 * Habilidade de Combate.
 */

import type { CVRef, PowerCategory, PowerScores } from '@/types';

interface PowerMapping {
  category: PowerCategory;
  points: number;
}

/**
 * Each entry maps a lowercase power keyword to a category + point contribution.
 * Multiple matching powers in the same category stack, capped at 10.
 */
const POWER_MAP: Record<string, PowerMapping> = {
  // Força
  'super strength': { category: 'Força', points: 6 },
  'superhuman strength': { category: 'Força', points: 6 },
  'enhanced strength': { category: 'Força', points: 5 },
  strength: { category: 'Força', points: 4 },
  size: { category: 'Força', points: 3 },
  density: { category: 'Força', points: 3 },

  // Velocidade
  'super speed': { category: 'Velocidade', points: 6 },
  'enhanced speed': { category: 'Velocidade', points: 5 },
  speed: { category: 'Velocidade', points: 4 },
  flight: { category: 'Velocidade', points: 5 },
  teleportation: { category: 'Velocidade', points: 5 },
  reflexes: { category: 'Velocidade', points: 3 },

  // Inteligência
  'super intellect': { category: 'Inteligência', points: 6 },
  'genius level intellect': { category: 'Inteligência', points: 6 },
  intellect: { category: 'Inteligência', points: 5 },
  telepathy: { category: 'Inteligência', points: 6 },
  telekinesis: { category: 'Inteligência', points: 5 },
  psychic: { category: 'Inteligência', points: 5 },
  'mind control': { category: 'Inteligência', points: 5 },
  'power mimicry': { category: 'Inteligência', points: 4 },

  // Energia
  'energy blast': { category: 'Energia', points: 6 },
  'energy absorption': { category: 'Energia', points: 5 },
  'energy manipulation': { category: 'Energia', points: 6 },
  energy: { category: 'Energia', points: 4 },
  magic: { category: 'Energia', points: 6 },
  electricity: { category: 'Energia', points: 5 },
  'fire control': { category: 'Energia', points: 5 },
  'ice control': { category: 'Energia', points: 5 },
  radiation: { category: 'Energia', points: 5 },
  'weather control': { category: 'Energia', points: 5 },

  // Resistência
  invulnerability: { category: 'Resistência', points: 7 },
  'healing factor': { category: 'Resistência', points: 6 },
  immortality: { category: 'Resistência', points: 6 },
  durability: { category: 'Resistência', points: 5 },
  stamina: { category: 'Resistência', points: 4 },
  'regeneration': { category: 'Resistência', points: 5 },
  'toxin immunity': { category: 'Resistência', points: 3 },

  // Habilidade de Combate
  'martial arts': { category: 'Habilidade de Combate', points: 6 },
  'weapon master': { category: 'Habilidade de Combate', points: 6 },
  marksmanship: { category: 'Habilidade de Combate', points: 5 },
  agility: { category: 'Habilidade de Combate', points: 4 },
  acrobat: { category: 'Habilidade de Combate', points: 3 },
  'stealth': { category: 'Habilidade de Combate', points: 3 },
  'gadgets': { category: 'Habilidade de Combate', points: 4 },
};

export const RADAR_CATEGORIES: PowerCategory[] = [
  'Força',
  'Velocidade',
  'Inteligência',
  'Energia',
  'Resistência',
  'Habilidade de Combate',
];

export const MAX_SCORE = 10;

function emptyScores(): PowerScores {
  return {
    Força: 0,
    Velocidade: 0,
    Inteligência: 0,
    Energia: 0,
    Resistência: 0,
    'Habilidade de Combate': 0,
  };
}

/**
 * Compute the six radar scores (0-10) from a character's powers.
 * A baseline of 1 is applied to every category so radars are never fully empty,
 * which keeps the visualization readable even for sparsely-documented heroes.
 */
export function computePowerScores(powers?: CVRef[]): PowerScores {
  const scores = emptyScores();
  if (powers && powers.length > 0) {
    for (const power of powers) {
      const name = power.name?.toLowerCase() ?? '';
      for (const keyword of Object.keys(POWER_MAP)) {
        if (name.includes(keyword)) {
          const { category, points } = POWER_MAP[keyword];
          scores[category] = Math.min(MAX_SCORE, scores[category] + points);
        }
      }
    }
  }

  // Baseline so every axis has a minimum presence.
  for (const cat of RADAR_CATEGORIES) {
    if (scores[cat] === 0) scores[cat] = 1;
  }
  return scores;
}

/** Overall power rating (average of the six categories, 0-10). */
export function overallRating(scores: PowerScores): number {
  const total = RADAR_CATEGORIES.reduce((sum, c) => sum + scores[c], 0);
  return Math.round((total / RADAR_CATEGORIES.length) * 10) / 10;
}
