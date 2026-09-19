/**
 * Favorites store backed by AsyncStorage, shared app-wide via Context API.
 * Wrap the app in <FavoritesProvider> (done in the root layout) and read it
 * with the useFavorites() hook.
 */

import AsyncStorage from '@react-native-async-storage/async-storage';
import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import type { FavoriteItem } from '@/types';

const STORAGE_KEY = '@nexus/favorites';

interface FavoritesContextValue {
  favorites: FavoriteItem[];
  ready: boolean;
  isFavorite: (id: number) => boolean;
  toggleFavorite: (item: Omit<FavoriteItem, 'addedAt'>) => void;
  removeFavorite: (id: number) => void;
  clearFavorites: () => void;
}

const FavoritesContext = createContext<FavoritesContextValue | undefined>(undefined);

export function FavoritesProvider({ children }: { children: React.ReactNode }) {
  const [favorites, setFavorites] = useState<FavoriteItem[]>([]);
  const [ready, setReady] = useState(false);

  // Load once on mount.
  useEffect(() => {
    (async () => {
      try {
        const raw = await AsyncStorage.getItem(STORAGE_KEY);
        if (raw) setFavorites(JSON.parse(raw));
      } catch {
        // Ignore corrupt storage; start empty.
      } finally {
        setReady(true);
      }
    })();
  }, []);

  // Persist whenever favorites change (after initial load).
  useEffect(() => {
    if (!ready) return;
    AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(favorites)).catch(() => undefined);
  }, [favorites, ready]);

  const isFavorite = useCallback(
    (id: number) => favorites.some((f) => f.id === id),
    [favorites]
  );

  const toggleFavorite = useCallback((item: Omit<FavoriteItem, 'addedAt'>) => {
    setFavorites((prev) => {
      if (prev.some((f) => f.id === item.id)) {
        return prev.filter((f) => f.id !== item.id);
      }
      return [{ ...item, addedAt: Date.now() }, ...prev];
    });
  }, []);

  const removeFavorite = useCallback((id: number) => {
    setFavorites((prev) => prev.filter((f) => f.id !== id));
  }, []);

  const clearFavorites = useCallback(() => setFavorites([]), []);

  const value = useMemo(
    () => ({ favorites, ready, isFavorite, toggleFavorite, removeFavorite, clearFavorites }),
    [favorites, ready, isFavorite, toggleFavorite, removeFavorite, clearFavorites]
  );

  return <FavoritesContext.Provider value={value}>{children}</FavoritesContext.Provider>;
}

export function useFavorites(): FavoritesContextValue {
  const ctx = useContext(FavoritesContext);
  if (!ctx) {
    throw new Error('useFavorites must be used within a FavoritesProvider');
  }
  return ctx;
}

export default useFavorites;
