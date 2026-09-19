import React from 'react';
import ComingSoon from '@/components/ui/ComingSoon';
import { colors } from '@/theme';

export default function ArcsScreen() {
  return (
    <ComingSoon
      icon="book-outline"
      title="Arcos Épicos"
      description="A linha do tempo horizontal dos grandes arcos da Marvel chega na próxima etapa."
      accent={colors.gold}
    />
  );
}
