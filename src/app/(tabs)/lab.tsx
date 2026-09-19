import React from 'react';
import ComingSoon from '@/components/ui/ComingSoon';
import { colors } from '@/theme';

export default function LabScreen() {
  return (
    <ComingSoon
      icon="flask-outline"
      title="Laboratório"
      description="Confronto, Multiverso, Sentido Aranha e o Efeito Thanos chegam nas próximas etapas."
      accent={colors.info}
    />
  );
}
