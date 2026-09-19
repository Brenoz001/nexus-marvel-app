import { Ionicons } from '@expo/vector-icons';
import React, { memo } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, { FadeInDown } from 'react-native-reanimated';
import { Text, withAlpha } from '@/components/ui';
import { colors, radius, spacing } from '@/theme';
import type { CVPower } from '@/types';

function PowerCardBase({ power, index = 0 }: { power: CVPower; index?: number }) {
  const count = power.characters?.length;
  return (
    <Animated.View
      entering={FadeInDown.delay(Math.min(index, 12) * 40).duration(400)}
      style={styles.card}
    >
      <View style={styles.iconWrap}>
        <Ionicons name="flash" size={20} color={colors.gold} />
      </View>
      <View style={styles.body}>
        <Text variant="heading" numberOfLines={1}>
          {power.name}
        </Text>
        {typeof count === 'number' && (
          <Text variant="mono" color={colors.textSecondary} style={styles.meta}>
            {count}+ personagens
          </Text>
        )}
      </View>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.md,
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    padding: spacing.md,
  },
  iconWrap: {
    width: 40,
    height: 40,
    borderRadius: radius.md,
    backgroundColor: withAlpha(colors.gold, 0.14),
    alignItems: 'center',
    justifyContent: 'center',
  },
  body: { flex: 1, gap: 2 },
  meta: { fontSize: 10 },
});

export const PowerCard = memo(PowerCardBase);
export default PowerCard;
