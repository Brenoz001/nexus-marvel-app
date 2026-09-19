/** Cinematic dark gradient placed over imagery. */

import { LinearGradient } from 'expo-linear-gradient';
import React from 'react';
import { StyleProp, StyleSheet, ViewStyle } from 'react-native';
import { colors } from '@/theme';
import { withAlpha } from './Badge';

export interface GradientOverlayProps {
  /** Where the darkest part sits. */
  direction?: 'bottom' | 'top' | 'full';
  /** Base color to fade to (defaults to the app background). */
  color?: string;
  intensity?: number; // 0..1 max opacity
  style?: StyleProp<ViewStyle>;
}

export function GradientOverlay({
  direction = 'bottom',
  color = colors.background,
  intensity = 0.95,
  style,
}: GradientOverlayProps) {
  const transparent = withAlpha(color, 0);
  const mid = withAlpha(color, intensity * 0.5);
  const solid = withAlpha(color, intensity);

  let gradientColors: [string, string, ...string[]];
  let start = { x: 0.5, y: 0 };
  let end = { x: 0.5, y: 1 };

  if (direction === 'bottom') {
    gradientColors = [transparent, mid, solid];
    start = { x: 0.5, y: 0 };
    end = { x: 0.5, y: 1 };
  } else if (direction === 'top') {
    gradientColors = [solid, mid, transparent];
    start = { x: 0.5, y: 0 };
    end = { x: 0.5, y: 1 };
  } else {
    gradientColors = [solid, transparent, solid];
  }

  return (
    <LinearGradient
      colors={gradientColors}
      start={start}
      end={end}
      style={[StyleSheet.absoluteFill, style]}
      pointerEvents="none"
    />
  );
}

export default GradientOverlay;
