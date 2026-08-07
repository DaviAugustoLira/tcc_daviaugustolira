## Context

`AdminShellScreen` (change `admin-auth-shell`) já observa `MapsRepository.observeMaps()` e
renderiza uma `LazyColumn` com o nome de cada `IndoorMap`. O documento `maps/{id}` no
Firestore já tem um campo `svgUrl` (gravado por `CreateMapUseCase`/`FirestoreMapsRepository`
ao cadastrar um map), mas o modelo de domínio (`IndoorMap`) e o DTO (`MapDto`) nunca leem esse
campo de volta — só `name`. Não existe nenhuma biblioteca de carregamento de imagem no
projeto ainda.

O pedido é: (1) trocar a lista de nomes por um carousel vertical com nome + miniatura, e
(2) ao tocar em um item, abrir a imagem em uma tela cheia onde o usuário pode rolar pelo
mapa. Ambos são descritos como MVU ("por enquanto") — sem zoom por pinça, sem cache
avançado, sem edição.

## Goals / Non-Goals

**Goals:**
- Expor a imagem de cada map (`imageUrl`) no domínio, mapeada a partir do `svgUrl` já
  persistido.
- Carousel vertical na casca admin: nome + miniatura por item, mantendo os estados de
  loading/erro/vazio que já existem.
- Tela cheia dedicada por map, navegável a partir do carousel, com rolagem
  vertical/horizontal quando a imagem for maior que a viewport.
- Acessibilidade: cada item do carousel e a tela cheia têm `contentDescription`
  significativo (nome do map, "abrir mapa em tela cheia", etc.) — passagem pelo skill
  `accessibility-audit` antes do merge, como exigido pela Seção 2 do CLAUDE.md.

**Non-Goals:**
- Zoom por pinça / `graphicsLayer` scale gesture — só rolagem (scroll) nesta versão.
- Cache de imagem em disco além do que a lib de carregamento já faz por padrão, CDN,
  pré-carregamento, ou otimização de miniatura no backend (redimensionar no Storage).
- Editar/excluir map, reordenar o carousel, paginação — a listagem hoje é "todos os maps",
  sem paginação (ver `tasks.md` para reavaliar se a base crescer).
- Suporte real a renderização de SVG vetorial em todas as plataformas — ver Decisão sobre
  Coil3 abaixo.

## Decisions

### 1. Adicionar Coil3 como lib de imagem (`AsyncImage`)
Não existe carregamento de imagem no projeto. Coil3 (`io.coil-kt.coil3`) é a única opção KMP
madura para Compose Multiplatform (Android + iOS) com cache de memória/disco embutido, e evita
reinventar download+decode+cache manualmente com `Ktor` + `ImageBitmap`. Vai em
`shared/build.gradle.kts` `commonMain` (`coil-compose`, `coil-network-ktor3`) porque miniatura
e tela cheia estão em módulos diferentes hoje (`AdminShellScreen` já é `feature/admin/login`,
mas o padrão do projeto é subir para `:shared` dependência usada por mais de um ponto de
carregamento de imagem — aqui, o próprio carousel e o viewer).

**Alternativa considerada e rejeitada:** implementar download manual via
`Firebase.storage`/`Ktor` + decodificar bytes para `ImageBitmap`. Rejeitada por reinventar
cache/erro/retry que Coil já resolve, e por não ser isso que o time quer manter.

### 2. `svgUrl` é tratado como URL de imagem genérica (raster), não SVG vetorial
O campo já se chama `svgUrl` (decisão herdada do `CreateMapUseCase`, fora do escopo desta
change). Coil3 decodifica bem PNG/JPEG/WebP nas duas plataformas; decodificação de SVG
vetorial de verdade exigiria um decoder adicional com suporte real limitado/inconsistente
em KMP. Esta change assume que a URL resolve para um formato raster suportado — se o time
realmente for gravar SVG vetorial no Storage, isso é um problema pré-existente do
`admin-auth-shell`/cadastro de map, não desta change (ver Open Question).

### 3. `IndoorMap` ganha `imageUrl: String` (não `String?`)
Assim como `name`, a leitura de `MapDto` para `IndoorMap` falha (`Result.failure`) se
`svgUrl` estiver ausente/vazio — mesmo padrão de "documento incompleto não vira crash,
vira map filtrado da lista" já usado por `MapMapper.toDomain`. Isso é consistente com a regra
do Firestore, que já exige `svgUrl` não vazio para `create` — um documento sem isso é
inconsistente e não deveria aparecer na listagem.

### 4. Tela cheia recebe `name` + `imageUrl` como argumentos de navegação, não busca de novo
`Screen.AdminMapViewer(name: String, imageUrl: String)` (tipos primitivos, serializável via
`kotlinx.serialization`, mesmo padrão do restante de `Screen.kt`). O carousel já tem esses
dois campos carregados em memória (via `observeMaps()`); refazer uma busca por `mapId` só
para redesenhar a mesma imagem seria uma chamada de rede/repositório redundante sem
benefício — não há necessidade de manter a tela cheia "viva" após navegar de volta.

**Alternativa considerada e rejeitada:** `Screen.AdminMapViewer(mapId: String)` + nova
`ObserveMapUseCase`/repositório por id. Rejeitada por adicionar uma camada de domínio nova
sem necessidade real agora (o dado já está disponível) — reavaliar se no futuro a tela cheia
precisar de mais dados do map que não estejam no carousel (ex.: metadados, pontos).

### 5. Tela cheia = `Modifier.horizontalScroll` + `verticalScroll` ao redor da `AsyncImage`
Sem zoom, "rolar pelo mapa" é implementado com um `Box`/`Column` scrollável nas duas direções
contendo a imagem desenhada com `ContentScale.None` em seu tamanho intrínseco (ou
`Modifier.fillMaxWidth()` mantendo proporção, o que for maior que a viewport habilita o
scroll correspondente). Ambos os scrolls relatam sua posição via `semantics` para não quebrar
navegação linear do TalkBack/VoiceOver (Seção 2 do CLAUDE.md) — validado no
`accessibility-audit`.

## Risks / Trade-offs

- [Risco] `svgUrl` pode de fato ser um arquivo `.svg` vetorial em produção, e Coil3 exibiria
  isso incorretamente ou falharia ao decodificar → Mitigação: assumido como raster nesta
  change (Decisão 2); se for vetorial de verdade, é um follow-up sobre o cadastro de map, não
  sobre a listagem.
- [Risco] Carousel "todos os maps" sem paginação pode ficar lento/pesado se a coleção
  crescer muito → Mitigação: aceitável para o volume esperado de um TCC; reavaliar
  paginação/lazy-loading de imagem (Coil já faz lazy load por item da `LazyColumn`) se isso
  virar problema real.
- [Risco] Rolagem em duas direções (`horizontalScroll` + `verticalScroll`) pode conflitar
  com gestos de navegação do sistema (back gesture) em algumas plataformas → Mitigação:
  mesmo padrão de scroll simples já usado em outras telas do app; validar manualmente em
  Android e iOS antes do merge.

## Open Questions

- O campo deveria ser renomeado de `svgUrl` para algo mais genérico (`imageUrl`) no próprio
  Firestore/`NewMapDto`/`CreateMapUseCase`? Fora do escopo desta change (que só lê o campo já
  existente) — sinalizar para o time decidir num follow-up se o formato real acabar não sendo
  SVG.
