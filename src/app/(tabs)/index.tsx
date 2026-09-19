import React from 'react';
import ComingSoon from '@/components/ui/ComingSoon';
import { colors } from '@/theme';

export default function HomeScreen() {
  return (
    <ComingSoon
      icon="git-network-outline"
      title="Nexus"
      description="O grafo de constelações — heróis conectados por seus times — chega na próxima etapa."
      accent={colors.red}
    />
  );
}
