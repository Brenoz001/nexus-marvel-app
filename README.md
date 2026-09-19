# NEXUS

> Every hero is connected.

App mobile do universo Marvel construído sobre a [Comic Vine API](https://comicvine.gamespot.com/api/).
Design cinematográfico/editorial — pense HBO Max encontrando um painel da S.H.I.E.L.D.

## Stack

- **Expo** (SDK 57) + **React Native** 0.86 + **TypeScript**
- **Expo Router** (file-based routing, headless Tabs)
- **Reanimated 4** + **Gesture Handler** para animações e gestos
- **React Native SVG** para gráficos (radar, grafo)
- **Expo** Linear Gradient · Haptics · Image · Sensors · Font
- **AsyncStorage** para favoritos/cache local
- **Axios** para HTTP
- Estado com **Context API + hooks** (sem Redux/MobX)
- Estilização com **StyleSheet** nativo (sem libs de UI/Tailwind)

## Configuração

1. Instale as dependências:
   ```bash
   npm install
   ```
2. Crie o arquivo `.env` a partir do exemplo e adicione sua chave da Comic Vine:
   ```bash
   cp .env.example .env
   ```
   ```
   EXPO_PUBLIC_COMIC_VINE_API_KEY=sua_chave_aqui
   ```
   Pegue uma chave gratuita em https://comicvine.gamespot.com/api/.
3. Rode o app:
   ```bash
   npx expo start
   ```

## Estrutura

```
src/
├── app/            # Rotas (Expo Router)
│   ├── (tabs)/     # Nexus · Explorar · Arcos · Lab
│   ├── character/[id].tsx
│   ├── team/[id].tsx
│   └── arc/[id].tsx
├── components/     # ui/ · explore/ · navigation/ ...
├── services/       # comicVineApi.ts · cache.ts
├── hooks/          # useCharacters, useTeams, useFavorites ...
├── types/          # Interfaces da API
├── theme/          # Cores, fontes, spacing, helpers
├── utils/          # marvelFilter, powerCategories, soundtrackMap
└── data/           # soundtracks.json
```

## Camada de API

`src/services/comicVineApi.ts` centraliza tudo:

- Instância axios com `baseURL`, `api_key`, `format=json` e `User-Agent`.
- Cache em memória (TTL de 5 min) para evitar requests repetidas.
- Rate limiting no client (máx. 1 request/segundo).
- Funções tipadas por endpoint (`getCharacters`, `getTeams`, `getStoryArcs`, `getPowers`, `getLocations`...).
- Erros normalizados via `ComicVineError`.

## Status de implementação

- [x] **Etapa 1** — Base (tema, tipos, API service, navegação, componentes UI)
- [x] **Etapa 2** — Tab Explorar (busca, filtros, grid infinito, favoritos)
- [ ] Etapa 3 — Detalhe do personagem completo
- [ ] Etapa 4 — Home (grafo de constelações)
- [ ] Etapa 5 — Arcos épicos (timeline)
- [ ] Etapa 6 — Laboratório (Confronto, Multiverso, Sentido Aranha, Snap)
- [ ] Etapa 7 — Polish (animações, haptics, loading)
- [ ] Etapa 8 — Splash + ícone

> **Nota:** o app filtra o universo Marvel no client (`publisher.name` contém "Marvel").
> Em web pode haver restrições de CORS da Comic Vine — use iOS/Android/Expo Go.
