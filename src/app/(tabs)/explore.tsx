/**
 * EXPLORAR — search + browse the Marvel universe.
 * Filters switch between characters (all / heroes / villains), teams and powers.
 */

import React, { useCallback, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  RefreshControl,
  ScrollView,
  StyleSheet,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import CharacterCard from '@/components/explore/CharacterCard';
import PowerCard from '@/components/explore/PowerCard';
import TeamCard from '@/components/explore/TeamCard';
import {
  EmptyState,
  ErrorState,
  FilterChip,
  SearchBar,
  Skeleton,
  Text,
} from '@/components/ui';
import useCharacters, { AlignmentFilter } from '@/hooks/useCharacters';
import useDebounce from '@/hooks/useDebounce';
import usePowers from '@/hooks/usePowers';
import useTeams from '@/hooks/useTeams';
import { colors, spacing } from '@/theme';

type FilterKey = 'all' | 'hero' | 'villain' | 'teams' | 'powers';

const FILTERS: { key: FilterKey; label: string }[] = [
  { key: 'all', label: 'Todos' },
  { key: 'hero', label: 'Heróis' },
  { key: 'villain', label: 'Vilões' },
  { key: 'teams', label: 'Times' },
  { key: 'powers', label: 'Poderes' },
];

const COLUMN_GAP = spacing.md;

export default function ExploreScreen() {
  const insets = useSafeAreaInsets();
  const [query, setQuery] = useState('');
  const [filter, setFilter] = useState<FilterKey>('all');
  const debouncedQuery = useDebounce(query, 500);

  const mode: 'characters' | 'teams' | 'powers' =
    filter === 'teams' ? 'teams' : filter === 'powers' ? 'powers' : 'characters';

  const alignment: AlignmentFilter =
    filter === 'hero' ? 'hero' : filter === 'villain' ? 'villain' : 'all';

  const characters = useCharacters({
    query: debouncedQuery,
    alignment,
    // Only fetch characters while in a character-based filter.
  });
  const teams = useTeams({ enabled: mode === 'teams' });
  const powers = usePowers({ enabled: mode === 'powers' });

  const active =
    mode === 'teams' ? teams : mode === 'powers' ? powers : characters;

  const bottomPad = insets.bottom + 110;

  const renderCharacter = useCallback(
    ({ item, index }: { item: any; index: number }) => (
      <CharacterCard character={item} index={index} />
    ),
    []
  );
  const renderTeam = useCallback(
    ({ item, index }: { item: any; index: number }) => (
      <TeamCard team={item} index={index} />
    ),
    []
  );
  const renderPower = useCallback(
    ({ item, index }: { item: any; index: number }) => (
      <PowerCard power={item} index={index} />
    ),
    []
  );

  const data = useMemo(() => {
    if (mode === 'teams') return teams.data;
    if (mode === 'powers') return powers.data;
    return characters.characters;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mode, teams.data, powers.data, characters.characters]);

  const isGrid = mode !== 'powers';
  const numColumns = isGrid ? 2 : 1;

  const listFooter =
    active.loadingMore ? (
      <View style={styles.footer}>
        <ActivityIndicator color={colors.red} />
      </View>
    ) : null;

  return (
    <View style={[styles.container, { paddingTop: insets.top + spacing.md }]}>
      {/* Header */}
      <View style={styles.header}>
        <Text variant="section" uppercase>
          Explorar
        </Text>
        <Text variant="body" color={colors.textSecondary}>
          O universo Marvel, conectado.
        </Text>
      </View>

      <View style={styles.searchWrap}>
        <SearchBar value={query} onChangeText={setQuery} />
      </View>

      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.chips}
        style={styles.chipsRow}
      >
        {FILTERS.map((f) => (
          <FilterChip
            key={f.key}
            label={f.label}
            active={filter === f.key}
            onPress={() => setFilter(f.key)}
          />
        ))}
      </ScrollView>

      {/* Content */}
      {active.loading ? (
        <GridSkeleton isGrid={isGrid} bottomPad={bottomPad} />
      ) : active.error ? (
        <ErrorState message={active.error} onRetry={active.retry} />
      ) : data.length === 0 ? (
        <EmptyState
          title="Nenhum resultado"
          message={
            query
              ? `Nada encontrado para "${query}".`
              : 'Tente outro filtro para explorar.'
          }
        />
      ) : (
        <FlatList
          key={mode}
          data={data as any[]}
          keyExtractor={(item) => String(item.id)}
          renderItem={
            mode === 'teams'
              ? renderTeam
              : mode === 'powers'
              ? renderPower
              : renderCharacter
          }
          numColumns={numColumns}
          columnWrapperStyle={isGrid ? styles.column : undefined}
          contentContainerStyle={[styles.list, { paddingBottom: bottomPad }]}
          showsVerticalScrollIndicator={false}
          onEndReached={active.loadMore}
          onEndReachedThreshold={0.6}
          ListFooterComponent={listFooter}
          removeClippedSubviews
          windowSize={7}
          initialNumToRender={8}
          maxToRenderPerBatch={8}
          refreshControl={
            <RefreshControl
              refreshing={active.refreshing}
              onRefresh={active.refresh}
              tintColor={colors.red}
              colors={[colors.red]}
            />
          }
        />
      )}
    </View>
  );
}

function GridSkeleton({ isGrid, bottomPad }: { isGrid: boolean; bottomPad: number }) {
  const items = Array.from({ length: 8 });
  return (
    <View style={[styles.list, { paddingBottom: bottomPad }]}>
      {isGrid ? (
        <View style={styles.skeletonGrid}>
          {items.map((_, i) => (
            <View key={i} style={styles.skeletonGridItem}>
              <Skeleton height={undefined as any} style={styles.skeletonCard} />
            </View>
          ))}
        </View>
      ) : (
        <View style={{ gap: spacing.sm }}>
          {items.map((_, i) => (
            <Skeleton key={i} height={72} borderRadius={10} />
          ))}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    paddingHorizontal: spacing.md,
  },
  header: {
    gap: 2,
    marginBottom: spacing.md,
  },
  searchWrap: {
    marginBottom: spacing.md,
  },
  chipsRow: {
    flexGrow: 0,
    marginBottom: spacing.md,
  },
  chips: {
    gap: spacing.sm,
    paddingRight: spacing.md,
  },
  list: {
    gap: COLUMN_GAP,
  },
  column: {
    gap: COLUMN_GAP,
  },
  footer: {
    paddingVertical: spacing.lg,
  },
  skeletonGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: COLUMN_GAP,
  },
  skeletonGridItem: {
    width: '47.5%',
  },
  skeletonCard: {
    aspectRatio: 3 / 4,
    width: '100%',
    borderRadius: 16,
  },
});
