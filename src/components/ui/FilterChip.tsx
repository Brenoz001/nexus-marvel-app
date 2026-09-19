import React from 'react';
import { Pressable, StyleSheet } from 'react-native';
import * as Haptics from 'expo-haptics';
import { colors, radius, spacing } from '@/theme';
import { withAlpha } from './Badge';
import Text from './Text';

export interface FilterChipProps {
  label: string;
  active?: boolean;
  onPress?: () => void;
  color?: string;
}

export function FilterChip({ label, active, onPress, color = colors.red }: FilterChipProps) {
  const handlePress = () => {
    Haptics.selectionAsync().catch(() => undefined);
    onPress?.();
  };

  return (
    <Pressable
      onPress={handlePress}
      style={[
        styles.chip,
        {
          backgroundColor: active ? withAlpha(color, 0.2) : colors.surface,
          borderColor: active ? withAlpha(color, 0.5) : colors.border,
        },
      ]}
    >
      <Text
        variant="caption"
        color={active ? colors.textPrimary : colors.textSecondary}
        uppercase
        style={styles.label}
      >
        {label}
      </Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  chip: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderRadius: radius.full,
    borderWidth: StyleSheet.hairlineWidth,
  },
  label: {
    fontWeight: '600',
  },
});

export default FilterChip;
