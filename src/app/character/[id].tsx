/**
 * Character detail. Minimal build for step 2 (hero, stats, about) — the full
 * profile (power radar, connections, teams, soundtrack) arrives in step 3.
 */

import { Ionicons } from '@expo/vector-icons';
import { Image } from 'expo-image';
import * as Haptics from 'expo-haptics';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useEffect, useState } from 'react';
import { ScrollView, StyleSheet, View, useWindowDimensions } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  Badge,
  Divider,
  ErrorState,
  GradientOverlay,
  IconButton,
  Skeleton,
  Text,
  withAlpha,
} from '@/components/ui';
import { useFavorites } from '@/hooks/useFavorites';
import { getCharacterById, ComicVineError } from '@/services/comicVineApi';
import { isMarvel } from '@/utils/marvelFilter';
import { colors, spacing } from '@/theme';
import type { CVCharacter } from '@/types';

const FIELD_LIST =
  'id,name,real_name,aliases,deck,image,publisher,powers,teams,character_friends,character_enemies,count_of_issue_appearances,first_appeared_in_issue,birth';

export default function CharacterDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { width } = useWindowDimensions();
  const { isFavorite, toggleFavorite } = useFavorites();

  const [character, setCharacter] = useState<CVCharacter | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const numericId = Number(id);

  const load = React.useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getCharacterById(numericId, FIELD_LIST);
      setCharacter(data);
    } catch (err) {
      setError(err instanceof ComicVineError ? err.message : 'Falha ao carregar o personagem.');
    } finally {
      setLoading(false);
    }
  }, [numericId]);

  useEffect(() => {
    load();
  }, [load]);

  const heroHeight = width * 1.15;
  const favorite = character ? isFavorite(character.id) : false;

  const onFavorite = () => {
    if (!character) return;
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium).catch(() => undefined);
    toggleFavorite({
      id: character.id,
      name: character.name,
      type: 'character',
      imageUrl: character.image?.medium_url,
      publisher: character.publisher?.name,
    });
  };

  if (error) {
    return (
      <View style={styles.container}>
        <BackButton top={insets.top} onPress={() => router.back()} />
        <ErrorState message={error} onRetry={load} />
      </View>
    );
  }

  const imageUrl = character?.image?.original_url ?? character?.image?.super_url;
  const teamsCount = character?.teams?.length ?? 0;
  const firstYear = extractYear(character?.first_appeared_in_issue?.name);

  return (
    <View style={styles.container}>
      <ScrollView
        showsVerticalScrollIndicator={false}
        contentContainerStyle={{ paddingBottom: insets.bottom + spacing.xxl }}
      >
        {/* Hero */}
        <View style={{ height: heroHeight }}>
          {loading ? (
            <Skeleton width="100%" height={heroHeight} borderRadius={0} />
          ) : (
            <Image
              source={imageUrl ? { uri: imageUrl } : undefined}
              contentFit="cover"
              transition={300}
              style={StyleSheet.absoluteFill}
            />
          )}
          <GradientOverlay direction="bottom" intensity={0.98} />
          <GradientOverlay direction="top" intensity={0.5} />

          <View style={[styles.heroContent]}>
            {loading ? (
              <>
                <Skeleton width={180} height={40} />
                <View style={{ height: spacing.sm }} />
                <Skeleton width={120} height={16} />
              </>
            ) : (
              <>
                <View style={styles.badgeRow}>
                  {character && isMarvel(character) && <Badge label="Marvel" color={colors.red} />}
                  {character?.publisher?.name && !isMarvel(character) && (
                    <Badge label={character.publisher.name} color={colors.info} />
                  )}
                </View>
                <Text variant="hero" numberOfLines={2} style={styles.heroTitle}>
                  {character?.name}
                </Text>
                {!!character?.real_name && (
                  <Text variant="body" color={colors.textSecondary}>
                    {character.real_name}
                  </Text>
                )}
              </>
            )}
          </View>
        </View>

        {/* Body */}
        <View style={styles.body}>
          {!loading && (
            <>
              <View style={styles.statsRow}>
                <Stat
                  value={character?.count_of_issue_appearances?.toLocaleString('pt-BR') ?? '—'}
                  label="Aparições"
                />
                <Stat value={teamsCount ? String(teamsCount) : '—'} label="Times" />
                <Stat value={firstYear ?? '—'} label="1ª Aparição" />
              </View>

              <Divider />

              {!!character?.deck && (
                <View style={styles.section}>
                  <Text variant="section" uppercase>
                    Sobre
                  </Text>
                  <Text variant="body" color={colors.textSecondary} style={styles.deck}>
                    {character.deck}
                  </Text>
                </View>
              )}

              <View style={styles.comingSoon}>
                <Ionicons name="sparkles-outline" size={18} color={colors.gold} />
                <Text variant="caption" color={colors.textSecondary}>
                  DNA de Poderes, Trilha do Herói e Conexões chegam na etapa 3.
                </Text>
              </View>
            </>
          )}
        </View>
      </ScrollView>

      {/* Floating controls */}
      <BackButton top={insets.top} onPress={() => router.back()} />
      {!loading && character && (
        <View style={[styles.favWrap, { top: insets.top + spacing.sm }]}>
          <IconButton
            name={favorite ? 'heart' : 'heart-outline'}
            color={favorite ? colors.redLight : colors.white}
            onPress={onFavorite}
            accessibilityLabel="Favoritar"
          />
        </View>
      )}
    </View>
  );
}

function BackButton({ top, onPress }: { top: number; onPress: () => void }) {
  return (
    <View style={[styles.backWrap, { top: top + spacing.sm }]}>
      <IconButton name="chevron-back" onPress={onPress} accessibilityLabel="Voltar" />
    </View>
  );
}

function Stat({ value, label }: { value: string; label: string }) {
  return (
    <View style={styles.stat}>
      <Text variant="monoBold" color={colors.textPrimary}>
        {value}
      </Text>
      <Text variant="micro" color={colors.textMuted} uppercase>
        {label}
      </Text>
    </View>
  );
}

function extractYear(issueName?: string | null): string | null {
  if (!issueName) return null;
  const match = issueName.match(/\b(19|20)\d{2}\b/);
  return match ? match[0] : null;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  heroContent: {
    position: 'absolute',
    left: spacing.md,
    right: spacing.md,
    bottom: spacing.md,
    gap: 4,
  },
  badgeRow: {
    flexDirection: 'row',
    gap: spacing.sm,
    marginBottom: spacing.sm,
  },
  heroTitle: {
    color: colors.textPrimary,
  },
  body: {
    padding: spacing.md,
  },
  statsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: spacing.md,
  },
  stat: {
    flex: 1,
    alignItems: 'center',
    gap: 4,
  },
  section: {
    gap: spacing.sm,
    marginBottom: spacing.lg,
  },
  deck: {
    lineHeight: 24,
  },
  comingSoon: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.sm,
    backgroundColor: withAlpha(colors.gold, 0.08),
    borderRadius: 12,
    padding: spacing.md,
  },
  backWrap: {
    position: 'absolute',
    left: spacing.md,
  },
  favWrap: {
    position: 'absolute',
    right: spacing.md,
  },
});
