import { Ionicons } from '@expo/vector-icons';
import React from 'react';
import { StyleSheet, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { colors, radius, spacing } from '@/theme';
import { withAlpha } from './Badge';
import Text from './Text';

export interface ComingSoonProps {
  icon?: keyof typeof Ionicons.glyphMap;
  title: string;
  description?: string;
  accent?: string;
}

/** Branded placeholder for screens still under construction. */
export function ComingSoon({
  icon = 'construct-outline',
  title,
  description = 'Esta seção está a caminho.',
  accent = colors.red,
}: ComingSoonProps) {
  const insets = useSafeAreaInsets();
  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={[styles.iconWrap, { backgroundColor: withAlpha(accent, 0.14) }]}>
        <Ionicons name={icon} size={48} color={accent} />
      </View>
      <Text variant="display" uppercase center>
        {title}
      </Text>
      <Text variant="body" color={colors.textSecondary} center style={styles.desc}>
        {description}
      </Text>
      <View style={[styles.pill, { borderColor: withAlpha(accent, 0.4) }]}>
        <Text variant="micro" color={accent} uppercase>
          Em breve
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.xl,
    gap: spacing.sm,
  },
  iconWrap: {
    width: 104,
    height: 104,
    borderRadius: radius.full,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.md,
  },
  desc: {
    maxWidth: 300,
  },
  pill: {
    marginTop: spacing.md,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.xs,
    borderRadius: radius.full,
    borderWidth: StyleSheet.hairlineWidth,
  },
});

export default ComingSoon;
