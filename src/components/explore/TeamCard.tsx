import { Image } from 'expo-image';
import * as Haptics from 'expo-haptics';
import { useRouter } from 'expo-router';
import React, { memo } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import Animated, { FadeInDown } from 'react-native-reanimated';
import { Badge, GradientOverlay, Text } from '@/components/ui';
import { teamColor, colors, radius, spacing } from '@/theme';
import type { CVTeam } from '@/types';

const BLURHASH = 'L184i9~q00_3?bxuIU%M00M{%MRj';

function TeamCardBase({ team, index = 0 }: { team: CVTeam; index?: number }) {
  const router = useRouter();
  const imageUrl = team.image?.medium_url ?? team.image?.small_url;
  const accent = teamColor(team.name);
  const members = team.count_of_team_members ?? team.characters?.length;

  const onOpen = () => {
    Haptics.selectionAsync().catch(() => undefined);
    router.push(`/team/${team.id}`);
  };

  return (
    <Animated.View
      entering={FadeInDown.delay(Math.min(index, 12) * 50).duration(400)}
      style={styles.wrapper}
    >
      <Pressable onPress={onOpen} style={[styles.card, { borderColor: accent + '55' }]}>
        <Image
          source={imageUrl ? { uri: imageUrl } : undefined}
          placeholder={{ blurhash: BLURHASH }}
          contentFit="cover"
          transition={250}
          style={styles.image}
        />
        <GradientOverlay direction="bottom" intensity={0.92} />
        <View style={[styles.accentBar, { backgroundColor: accent }]} />
        <View style={styles.info}>
          <Badge label="Time" color={accent} size="sm" />
          <Text variant="cardTitle" numberOfLines={2}>
            {team.name}
          </Text>
          {typeof members === 'number' && members > 0 && (
            <Text variant="mono" color={colors.textSecondary} style={styles.meta}>
              {members} membros
            </Text>
          )}
        </View>
      </Pressable>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  wrapper: { flex: 1 },
  card: {
    aspectRatio: 3 / 4,
    borderRadius: radius.lg,
    overflow: 'hidden',
    backgroundColor: colors.surfaceLight,
    borderWidth: 1,
  },
  image: { position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 },
  accentBar: {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: 3,
  },
  info: {
    position: 'absolute',
    left: spacing.sm,
    right: spacing.sm,
    bottom: spacing.sm,
    gap: 4,
  },
  meta: { fontSize: 10 },
});

export const TeamCard = memo(TeamCardBase);
export default TeamCard;
