## Why

Hoje a casca administrativa (`AdminShellScreen`, change `admin-auth-shell`) só lista o
**nome** de cada map cadastrado, em uma lista de texto sem miniatura e sem forma de abrir o
mapa. Para o admin conseguir revisar um map já cadastrado (checar se o floor plan subiu
correto, comparar mapas de andares diferentes) ele precisa ver a imagem, não só o nome — e
hoje isso não existe em nenhuma tela do app.

## What Changes

- `IndoorMap` (entidade em `shared/domain/maps`) passa a expor a URL da imagem do map
  (`imageUrl`), além do `id`/`name` que já existia — `MapDto`/`MapMapper` (data, em
  `feature/admin/login`) são atualizados para ler o campo `svgUrl` do documento
  `maps/{id}` e mapear para esse novo campo, com a mesma validação de "campo ausente/vazio
  vira falha de mapeamento" já usada para `name`.
- `AdminShellScreen` troca a lista de texto simples por um **carousel vertical**: cada item
  mostra o nome do map e uma miniatura da imagem (thumbnail), preservando os estados já
  existentes de loading/erro/vazio.
- Tocar em um item do carousel abre uma **nova tela em tela cheia** (`AdminMapViewerScreen`)
  mostrando a imagem do map em escala ocupando toda a tela; o usuário pode rolar
  (scroll vertical/horizontal) para navegar pela imagem quando ela for maior que a viewport.
  Zoom por pinça e outras interações de imagem fica fora do escopo desta primeira versão
  ("por enquanto").
- Nova rota `Screen.AdminMapViewer` registrada em `:navigation`, protegida pelo mesmo
  `AdminRouteGuard` das demais rotas admin.
- Adiciona uma biblioteca de carregamento de imagem assíncrona (Coil3, KMP) ao `:shared`,
  já que hoje não existe nenhuma no projeto e tanto a miniatura quanto a tela cheia
  precisam carregar a imagem a partir de uma URL remota.

## Capabilities

### New Capabilities
- `admin-map-listing`: carousel vertical de maps cadastrados (nome + miniatura) na casca
  admin, e visualização em tela cheia com rolagem de um map selecionado.

### Modified Capabilities
(nenhuma — `admin-shell` continua descrevendo só a casca de navegação/guarda de rota; o
conteúdo da listagem de maps é uma capability nova, não uma mudança de requisito da casca.)

## Impact

- `shared/domain/maps/IndoorMap.kt`: novo campo `imageUrl`.
- `shared/navigation/Screen.kt` e `:navigation` (`Navigation.kt`): nova rota
  `AdminMapViewer`.
- `feature/admin/login`: `MapDto`, `MapMapper` (+ testes), `AdminShellScreen` (carousel),
  novo `AdminMapViewerScreen`/`AdminMapViewerScreenRoute`.
- `gradle/libs.versions.toml` + `shared/build.gradle.kts`: nova dependência Coil3
  (`coil-compose`, `coil-network-ktor3` ou equivalente) em `commonMain`.
- `composeApp` (Android): novos `@Preview` para o carousel atualizado e para o viewer em
  tela cheia, seguindo o padrão já usado (`AdminShellPreview.kt`, `CreateMapPreview.kt`).
- Sem mudança em `firestore.rules`: `svgUrl` já é lido/gravado pela regra existente de
  `maps/{mapId}`; esta mudança só passa a consumir esse campo no cliente.
