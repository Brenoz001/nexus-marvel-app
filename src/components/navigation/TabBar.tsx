/**
 * Custom tab bar built on the headless expo-router/ui Tabs API.
 * Exposes a container (used as the TabList child) and a slot button (used as
 * each TabTrigger child). Active tab shows a red icon + label and springs in.
 */

import { Ionicons } from '@expo/vector-icons';
import * as Haptics from 'expo-haptics';
import React, { forwardRef, useEffect } from 'react';
import { Pressable, PressableProps, StyleSheet, View } from 'react-native';
import Animated, {
  useAnimatedStyle,
  useSharedValue,
  withSpring,
} from 'react-native-reanimated';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { withAlpha } from '@/components/ui/Badge';
import Text from '@/components/ui/Text';
import { colors, radius, spacing } from '@/theme';

/** Container rendered as the TabList child (asChild). */
export const TabBarContainer = forwardRef<View, { children?: React.ReactNode }>(
  function TabBarContainer({ children, ...rest }, ref) {
    const insets = useSafeAreaInsets();
    return (
      <View
        ref={ref}
        style={[styles.container, { paddingBottom: insets.bottom + spacing.sm }]}
        {...rest}
      >
        <View style={styles.bar}>{children}</View>
      </View>
    );
  }
);

export interface TabButtonProps extends PressableProps {
  /** Injected by TabTrigger via asChild. */
  isFocused?: boolean;
  icon: keyof typeof Ionicons.glyphMap;
  iconActive: keyof typeof Ionicons.glyphMap;
  label: string;
}

/** Slot button rendered as each TabTrigger child (asChild). */
export const TabButton = forwardRef<View, TabButtonProps>(function TabButton(
  { isFocused, icon, iconActive, label, onPress, ...rest },
  ref
) {
  const focused = !!isFocused;
  const scale = useSharedValue(focused ? 1 : 0.9);

  useEffect(() => {
    scale.value = withSpring(focused ? 1 : 0.9, { damping: 12, stiffness: 180 });
  }, [focused, scale]);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scale.value }],
  }));

  const handlePress: PressableProps['onPress'] = (e) => {
    Haptics.selectionAsync().catch(() => undefined);
    onPress?.(e);
  };

  return (
    <Pressable
      ref={ref}
      onPress={handlePress}
      accessibilityRole="button"
      accessibilityState={{ selected: focused }}
      style={styles.tab}
      {...rest}
    >
      <Animated.View style={[styles.tabInner, animatedStyle]}>
        <View style={[styles.iconWrap, focused && styles.iconWrapActive]}>
          <Ionicons
            name={focused ? iconActive : icon}
            size={22}
            color={focused ? colors.redLight : colors.textMuted}
          />
        </View>
        {focused && (
          <Text variant="micro" color={colors.textPrimary} uppercase style={styles.label}>
            {label}
          </Text>
        )}
      </Animated.View>
    </Pressable>
  );
});

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: spacing.md,
    backgroundColor: colors.transparent,
  },
  bar: {
    flexDirection: 'row',
    backgroundColor: withAlpha(colors.surface, 0.96),
    borderRadius: radius.xl,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.borderStrong,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.xs,
  },
  tab: {
    flex: 1,
    alignItems: 'center',
  },
  tabInner: {
    alignItems: 'center',
    gap: 2,
  },
  iconWrap: {
    width: 44,
    height: 32,
    borderRadius: radius.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconWrapActive: {
    backgroundColor: withAlpha(colors.red, 0.14),
  },
  label: {
    fontWeight: '700',
    fontSize: 9,
  },
});
