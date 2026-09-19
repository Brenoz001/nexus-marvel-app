# NEXUS

> Every hero is connected.

App Android **nativo** do universo Marvel, consumindo a [Comic Vine API](https://comicvine.gamespot.com/api/).
Design cinematográfico/editorial — pense HBO Max encontrando um painel da S.H.I.E.L.D.

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **AGP 9 / Gradle 9.1**, `compileSdk 36`, `minSdk 24`
- **MVVM** com `ViewModel` + `StateFlow` (sem Hilt — service locator simples em `di/Graph`)
- **Navigation Compose** (tabs custom + telas de detalhe)
- **Retrofit + OkHttp** (interceptors de auth e rate limit) + **Gson**
- **Coil 3** para imagens
- **DataStore** para favoritos
- **Coroutines / Flow**

## Configuração da API key

A chave da Comic Vine é lida do `local.properties` (que fica **fora** do controle de versão) e exposta via `BuildConfig`.

1. Abra `local.properties` na raiz do projeto.
2. Adicione a linha:
   ```
   COMIC_VINE_API_KEY=sua_chave_aqui
   ```
   Pegue uma chave gratuita em https://comicvine.gamespot.com/api/.
3. Sincronize o Gradle e rode o app (▶ no Android Studio, ou `gradlew :app:installDebug`).

Sem a chave, a tela Explorar mostra um estado de erro explicando como configurá-la.

## Estrutura (`app/src/main/java/com/example/nexus_marvel_app/`)

```
MainActivity.kt / NexusApplication.kt
data/
├── remote/        Retrofit (ComicVineApi, ComicVineClient, dto/)
├── repository/    ComicVineRepository (cache 5min + erros normalizados)
├── mapper/        DTO -> domínio
├── local/         FavoritesStore (DataStore)
└── MemoryCache.kt · ComicVineException.kt
domain/model/      Character, Team, StoryArc, Power, FavoriteItem
di/Graph.kt        Service locator
ui/
├── theme/         Cores, tipografia (Bebas/JetBrains Mono), spacing
├── components/    Badge, SearchBar, FilterChip, States, Shimmer, Images...
├── navigation/    Rotas + tab bar custom
├── favorites/     FavoritesViewModel (compartilhado via CompositionLocal)
└── screens/       home · explore · arcs · lab · character · team · arc
util/              MarvelFilter, PowerCategories (radar), Soundtracks
```

## Camada de rede

`data/remote/ComicVineClient.kt` centraliza o OkHttp/Retrofit:
- injeta `api_key` + `format=json` e o header `User-Agent: NexusMarvelApp/1.0`;
- **rate limit** no client (mín. 1s entre requisições);
- `ComicVineRepository` adiciona **cache em memória (TTL 5 min)** e normaliza erros para `ComicVineException`.

## Status de implementação

- [x] **Etapa 1** — Base (tema, rede, repositório, favoritos, navegação, componentes)
- [x] **Etapa 2** — Tela Explorar (busca com debounce, filtros, grid infinito, favoritar)
- [x] **Etapa 3** — Detalhe completo do personagem (radar de poderes DNA, trilha sonora, conexões aliados/inimigos, times)
- [x] **Etapa 4** — Home: grafo de constelações interativo (pan/zoom/tap/duplo-toque, nós conectados por time, fundo de estrelas)
- [x] **Etapa 5** — Arcos épicos: timeline horizontal com cards largos e linha do tempo
- [x] **Etapa 6** — Laboratório: Confronto (radar duplo), Multiverso (variantes), Sentido Aranha (shake/acelerômetro) e Efeito Thanos (snap)
- [ ] Etapa 7 — Polish (animações, haptics, transições)
- [ ] Etapa 8 — Splash + ícone finais
