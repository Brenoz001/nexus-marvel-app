/**
 * Typed Text wrapper with design-system variants.
 * Usage: <Text variant="display">NEXUS</Text>
 */

import React from 'react';
import { StyleSheet, Text as RNText, TextProps as RNTextProps } from 'react-native';
import { colors, fonts, fontSizes } from '@/theme';

export type TextVariant =
  | 'hero'
  | 'display'
  | 'section'
  | 'cardTitle'
  | 'heading'
  | 'body'
  | 'bodyMedium'
  | 'caption'
  | 'micro'
  | 'mono'
  | 'monoBold';

export interface TextProps extends RNTextProps {
  variant?: TextVariant;
  color?: string;
  center?: boolean;
  uppercase?: boolean;
}

export function Text({
  variant = 'body',
  color = colors.textPrimary,
  center,
  uppercase,
  style,
  children,
  ...rest
}: TextProps) {
  return (
    <RNText
      style={[
        styles[variant],
        { color },
        center && styles.center,
        uppercase && styles.uppercase,
        style,
      ]}
      {...rest}
    >
      {children}
    </RNText>
  );
}

const styles = StyleSheet.create({
  hero: {
    fontFamily: fonts.display,
    fontSize: fontSizes.heroTitle,
    letterSpacing: 2,
  },
  display: {
    fontFamily: fonts.display,
    fontSize: fontSizes.sectionTitle + 6,
    letterSpacing: 1.5,
  },
  section: {
    fontFamily: fonts.display,
    fontSize: fontSizes.sectionTitle,
    letterSpacing: 1,
  },
  cardTitle: {
    fontFamily: fonts.heading,
    fontSize: fontSizes.cardTitle,
  },
  heading: {
    fontFamily: fonts.heading,
    fontSize: fontSizes.body + 2,
  },
  body: {
    fontFamily: fonts.body,
    fontSize: fontSizes.body,
    lineHeight: fontSizes.body * 1.5,
  },
  bodyMedium: {
    fontFamily: fonts.bodyMedium,
    fontSize: fontSizes.body,
    lineHeight: fontSizes.body * 1.5,
  },
  caption: {
    fontFamily: fonts.caption,
    fontSize: fontSizes.caption,
    letterSpacing: 0.4,
  },
  micro: {
    fontFamily: fonts.caption,
    fontSize: fontSizes.micro,
    letterSpacing: 0.5,
  },
  mono: {
    fontFamily: fonts.mono,
    fontSize: fontSizes.caption,
  },
  monoBold: {
    fontFamily: fonts.monoBold,
    fontSize: fontSizes.cardTitle,
  },
  center: { textAlign: 'center' },
  uppercase: { textTransform: 'uppercase' },
});

export default Text;
