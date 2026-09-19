import { Ionicons } from '@expo/vector-icons';
import { Image } from 'expo-image';
import * as Haptics from 'expo-haptics';
import { useRouter } from 'expo-router';
import React, { memo, useEffect } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import Animated, {
  FadeInDown,
  useAnimatedStyle,
  useSharedValue,
  withSequence,
  withSpring,
} from 'react-native-reanimated';
import { Badge, GradientOverlay, Text, withAlpha } from '@/components/ui';
import { useFavorites } from '@/hooks/useFavorites';
import { isMarvel } from '@/utils/marvelFilter';
import { colors, radius, spacing } from '@/theme';
import type { CVCharacter } from '@/types';

const BLURHASH = 'L184i9~q00_3?bxuIU%M00M{%MRj';

interface CharacterCardProps {
  character: CVCharacter;
  index?: number;
}

function CharacterCardBase({ character, index = 0 }: CharacterCardProps) {
  const router = useRouter();
  const { isFavorite, toggleFavorite } = useFavorites();
  const favorite = isFavorite(character.id);

  const heartScale = useSharedValue(1);
  const heartStyle = useAnimatedStyle(() => ({
    transform: [{ scale: heartScale.value }],
  }));

  useEffect(() => {
    if (favorite) {
      heartScale.value = withSequence(
        withSpring(1.4, { damping: 6, stiffness: 260 }),
        withSpring(1, { damping: 10 })
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [favorite]);

  const imageUrl = character.image?.medium_url ?? character.image?.small_url;
  const marvel = isMarvel(character);

  const onFavorite = () => {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium).catch(() => undefined);
    toggleFavorite({
      id: character.id,
      name: character.name,
      type: 'character',
      imageUrl,
      publisher: character.publisher?.name,
    });
  };

  const onOpen = () => {
    Haptics.selectionAsync().catch(() => undefined);
    router.push(`/character/${character.id}`);
  };

  return (
    <Animated.View
      entering={FadeInDown.delay(Math.min(index, 12) * 50).duration(400)}
      style={styles.wrapper}
    >
      <Pressable onPress={onOpen} style={styles.card} accessibilityRole="button">
        <Image
          source={imageUrl ? { uri: imageUrl } : undefined}
          placeholder={{ blurhash: BLURHASH }}
          contentFit="cover"
          transition={250}
          style={styles.image}
        />
        <GradientOverlay direction="bottom" intensity={0.9} />

        <Pressable onPress={onFavorite} hitSlop={10} style={styles.heart}>
          <Animated.View style={heartStyle}>
            <Ionicons
              name={favorite ? 'heart' : 'heart-outline'}
              size={20}
              color={favorite ? colors.redLight : colors.white}
            />
          </Animated.View>
        </Pressable>

        <View style={styles.info}>
          {marvel && <Badge label="Marvel" color={colors.red} size="sm" />}
          <Text variant="cardTitle" numberOfLines={2} style={styles.name}>
            {character.name}
          </Text>
          {typeof character.count_of_issue_appearances === 'number' && (
            <Text variant="mono" color={colors.textSecondary} style={styles.appearances}>
              {character.count_of_issue_appearances.toLocaleString('pt-BR')} aparições
            </Text>
          )}
        </View>
      </Pressable>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
  },
  card: {
    aspectRatio: 3 / 4,
    borderRadius: radius.lg,
    overflow: 'hidden',
    backgroundColor: colors.surfaceLight,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  image: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  heart: {
    position: 'absolute',
    top: spacing.sm,
    right: spacing.sm,
    width: 34,
    height: 34,
    borderRadius: radius.full,
    backgroundColor: withAlpha(colors.black, 0.4),
    alignItems: 'center',
    justifyContent: 'center',
  },
  info: {
    position: 'absolute',
    left: spacing.sm,
    right: spacing.sm,
    bottom: spacing.sm,
    gap: 4,
  },
  name: {
    color: colors.textPrimary,
  },
  appearances: {
    fontSize: 10,
  },
});

export const CharacterCard = memo(CharacterCardBase);
export default CharacterCard;
