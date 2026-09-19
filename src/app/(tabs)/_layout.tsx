import { Tabs, TabList, TabSlot, TabTrigger } from 'expo-router/ui';
import React from 'react';
import { TabBarContainer, TabButton } from '@/components/navigation/TabBar';

export default function TabsLayout() {
  return (
    <Tabs>
      <TabSlot />
      <TabList asChild>
        <TabBarContainer>
          <TabTrigger name="index" href="/" asChild>
            <TabButton icon="git-network-outline" iconActive="git-network" label="Nexus" />
          </TabTrigger>
          <TabTrigger name="explore" href="/explore" asChild>
            <TabButton icon="compass-outline" iconActive="compass" label="Explorar" />
          </TabTrigger>
          <TabTrigger name="arcs" href="/arcs" asChild>
            <TabButton icon="book-outline" iconActive="book" label="Arcos" />
          </TabTrigger>
          <TabTrigger name="lab" href="/lab" asChild>
            <TabButton icon="flask-outline" iconActive="flask" label="Lab" />
          </TabTrigger>
        </TabBarContainer>
      </TabList>
    </Tabs>
  );
}
