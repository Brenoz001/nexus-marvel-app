import React from 'react';
import { StyleSheet, View, ViewStyle } from 'react-native';
import { colors, radius, spacing } from '@/theme';
import Text from './Text';

export interface BadgeProps {
  label: string;
  color?: string;
  tint?: string;
  size?: 'sm' | 'md';
  style?: ViewStyle;
}

/** Pill with a semi-transparent tinted background. */
export function Badge({ label, color = colors.red, tint, size = 'md', style }: BadgeProps) {
  const bg = tint ?? withAlpha(color, 0.18);
  return (
    <View
      style={[
        styles.badge,
        size === 'sm' ? styles.sm : styles.md,
        { backgroundColor: bg, borderColor: withAlpha(color, 0.35) },
        style,
      ]}
    >
      <Text variant="micro" color={color} uppercase style={styles.text}>
        {label}
      </Text>
    </View>
  );
}

/** Add alpha to a #RRGGBB hex color. */
export function withAlpha(hex: string, alpha: number): string {
  if (!hex.startsWith('#') || hex.length !== 7) return hex;
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

const styles = StyleSheet.create({
  badge: {
    alignSelf: 'flex-start',
    borderRadius: radius.full,
    borderWidth: StyleSheet.hairlineWidth,
  },
  sm: {
    paddingHorizontal: spacing.sm,
    paddingVertical: 2,
  },
  md: {
    paddingHorizontal: spacing.md - 4,
    paddingVertical: spacing.xs,
  },
  text: {
    fontWeight: '700',
  },
});

export default Badge;
