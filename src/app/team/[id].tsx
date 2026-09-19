import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { StyleSheet, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import ComingSoon from '@/components/ui/ComingSoon';
import { IconButton } from '@/components/ui';
import { colors, spacing } from '@/theme';

export default function TeamDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const insets = useSafeAreaInsets();

  return (
    <View style={styles.container}>
      <ComingSoon
        icon="people-outline"
        title="Time"
        description={`O detalhe do time (#${id}) — membros e estatísticas — chega na etapa 6.`}
        accent={colors.info}
      />
      <View style={[styles.back, { top: insets.top + spacing.sm }]}>
        <IconButton name="chevron-back" onPress={() => router.back()} accessibilityLabel="Voltar" />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  back: { position: 'absolute', left: spacing.md },
});
