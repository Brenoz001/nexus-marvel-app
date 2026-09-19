/**
 * NEXUS design system.
 * Cinematic / editorial. Think HBO Max meets a S.H.I.E.L.D. dashboard.
 * Single source of truth for colors, typography, spacing, radii and helpers.
 */

export const colors = {
  // Base
  background: '#08080F', // near-black with a blue tint
  surface: '#12121F', // cards and containers
  surfaceLight: '#1C1C2E', // elevation 2
  surfaceHover: '#252540', // elevation 3 / hover

  // Brand
  red: '#C0101A', // primary Marvel red
  redDark: '#8C0C13', // dark variant
  redLight: '#FF4D5A', // light variant (text on dark)
  gold: '#E6B800', // gold accent (highlights, badges)
  goldDim: '#B8930A', // soft gold

  // Text
  textPrimary: '#F0F0F5',
  textSecondary: '#8888A0',
  textMuted: '#555570',

  // Semantic
  success: '#3D8B3D',
  warning: '#E6B800',
  danger: '#C0101A',
  info: '#4A90D9',

  // Overlay
  overlay: 'rgba(8, 8, 15, 0.85)',
  overlayLight: 'rgba(8, 8, 15, 0.5)',

  // Utility
  white: '#FFFFFF',
  black: '#000000',
  transparent: 'transparent',
  border: 'rgba(255, 255, 255, 0.08)',
  borderStrong: 'rgba(255, 255, 255, 0.16)',
} as const;

/**
 * Font family keys. The actual PostScript names are provided by the
 * @expo-google-fonts packages and registered in the root layout.
 */
export const fonts = {
  display: 'BebasNeue_400Regular', // large titles, section headers
  heading: 'Inter_700Bold', // subtitles
  body: 'Inter_400Regular', // body copy
  bodyMedium: 'Inter_500Medium',
  caption: 'Inter_500Medium', // labels, badges
  captionBold: 'Inter_600SemiBold',
  mono: 'JetBrainsMono_400Regular', // technical data, IDs, stats
  monoBold: 'JetBrainsMono_700Bold',
} as const;

export const fontSizes = {
  heroTitle: 48,
  sectionTitle: 28,
  cardTitle: 18,
  body: 15,
  caption: 12,
  micro: 10,
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
} as const;

export const radius = {
  sm: 6,
  md: 10,
  lg: 16,
  xl: 24,
  full: 9999,
} as const;

/**
 * Team accent colors used across the app (constellation graph, badges).
 */
export const teamColors: Record<string, string> = {
  Avengers: '#C0101A',
  'X-Men': '#4A90D9',
  Defenders: '#3D8B3D',
  'Guardians of the Galaxy': '#E6B800',
  'Fantastic Four': '#4AB0D9',
  Illuminati: '#9B59B6',
  Inhumans: '#1ABC9C',
  Thunderbolts: '#E67E22',
  default: '#8888A0',
};

export function teamColor(name?: string): string {
  if (!name) return teamColors.default;
  const key = Object.keys(teamColors).find((k) =>
    name.toLowerCase().includes(k.toLowerCase())
  );
  return key ? teamColors[key] : teamColors.default;
}

/**
 * Subtle shadow presets. We favor gradients + opacity for depth, but a soft
 * black shadow is sometimes useful.
 */
export const shadow = {
  soft: {
    shadowColor: '#000000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.4,
    shadowRadius: 16,
    elevation: 8,
  },
  glowRed: {
    shadowColor: colors.red,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.6,
    shadowRadius: 20,
    elevation: 10,
  },
} as const;

export const theme = {
  colors,
  fonts,
  fontSizes,
  spacing,
  radius,
  shadow,
  teamColor,
} as const;

export type Theme = typeof theme;
export default theme;
