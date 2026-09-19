import { Ionicons } from '@expo/vector-icons';
import React from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import { colors, radius, spacing } from '@/theme';
import { withAlpha } from './Badge';
import Text from './Text';

export interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = 'Algo deu errado',
  message = 'Não foi possível carregar os dados.',
  onRetry,
}: ErrorStateProps) {
  return (
    <View style={styles.container}>
      <View style={styles.iconWrap}>
        <Ionicons name="alert-circle-outline" size={44} color={colors.redLight} />
      </View>
      <Text variant="section" center>
        {title}
      </Text>
      <Text variant="body" color={colors.textSecondary} center style={styles.message}>
        {message}
      </Text>
      {onRetry && (
        <Pressable onPress={onRetry} style={styles.button}>
          <Ionicons name="refresh" size={16} color={colors.textPrimary} />
          <Text variant="caption" uppercase style={styles.buttonText}>
            Tentar novamente
          </Text>
        </Pressable>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.xl,
    gap: spacing.sm,
  },
  iconWrap: {
    width: 88,
    height: 88,
    borderRadius: radius.full,
    backgroundColor: withAlpha(colors.red, 0.12),
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.sm,
  },
  message: {
    marginBottom: spacing.md,
    maxWidth: 300,
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.sm,
    backgroundColor: colors.red,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.sm + 2,
    borderRadius: radius.full,
  },
  buttonText: {
    fontWeight: '700',
  },
});

export default ErrorState;
