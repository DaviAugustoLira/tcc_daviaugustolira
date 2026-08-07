## 1. Dependência de imagem (Coil3)

- [x] 1.1 Adicionar versão/catálogo do Coil3 (`coil`, `coil-compose`, `coil-network-ktor3` ou
      equivalente) em `gradle/libs.versions.toml`.
- [x] 1.2 Adicionar as dependências em `shared/build.gradle.kts` (`commonMain`), expostas como
      `api` para os consumidores (`feature/admin/login`), seguindo o padrão já usado para as
      demais libs de UI/rede do módulo.

## 2. Domínio e mapeamento de dados

- [x] 2.1 Adicionar `imageUrl: String` a `IndoorMap`
      (`shared/src/commonMain/.../shared/domain/maps/IndoorMap.kt`).
- [x] 2.2 Adicionar `svgUrl: String?` a `MapDto`
      (`feature/admin/login/.../admin/login/data/MapDto.kt`).
- [x] 2.3 Atualizar `MapMapper.toDomain` para exigir `svgUrl` não vazio (mesma regra já
      aplicada a `name`) e popular `IndoorMap.imageUrl`; documento sem imagem válida vira
      `Result.failure` (fica de fora da listagem), não crash.
- [x] 2.4 Atualizar `MapMapperTest` com casos: mapeia `svgUrl` para `imageUrl` com sucesso;
      falha quando `svgUrl` é nulo; falha quando `svgUrl` é vazio/blank.

## 3. Navegação

- [x] 3.1 Adicionar `Screen.AdminMapViewer(name: String, imageUrl: String) : Screen()` em
      `shared/src/commonMain/.../shared/navigation/Screen.kt`.
- [x] 3.2 Registrar `composable<Screen.AdminMapViewer>` em `:navigation` (`Navigation.kt`),
      envolvido pelo mesmo `AdminRouteGuard` usado por `AdminHome`/`AdminCreateMap`.

## 4. Carousel na casca admin

- [x] 4.1 Substituir `AdminShellMapsList` (texto simples) por um carousel vertical: cada item
      mostra o nome do map e uma `AsyncImage` (Coil) da `imageUrl` como miniatura, mantendo os
      estados de loading/erro/vazio já tratados por `AdminShellMapsSection`.
- [x] 4.2 Cada item do carousel recebe `Modifier.clickable`/`selectable` com
      `contentDescription` significativo (ex.: "Abrir mapa {nome} em tela cheia") e navega
      para `Screen.AdminMapViewer(name = map.name, imageUrl = map.imageUrl)` via `INavigator`.
- [x] 4.3 Alvo de toque do item ≥ 48dp (Seção 2 do CLAUDE.md) e miniatura com
      `contentDescription` nulo/decorativo (o texto do nome já descreve o item, evitando
      leitura duplicada pelo TalkBack/VoiceOver).

## 5. Tela cheia do map

- [x] 5.1 Criar `AdminMapViewerScreen`/`AdminMapViewerScreenRoute` em
      `feature/admin/login/.../admin/login/presentation/screen/`, recebendo `name` e
      `imageUrl` como parâmetros (sem ViewModel — não há lógica de negócio, só exibição).
- [x] 5.2 Exibir a imagem (`AsyncImage`, Coil) dentro de um contêiner com
      `Modifier.horizontalScroll` + `Modifier.verticalScroll`, ocupando toda a tela
      (`fillMaxSize`), permitindo rolar quando a imagem for maior que a viewport.
- [x] 5.3 Adicionar affordance de voltar (ex.: botão "Fechar"/topo) com
      `contentDescription` claro, já que a tela cheia não tem outra navegação visível.

## 6. Previews (`:composeApp`)

- [x] 6.1 Atualizar `AdminShellPreview.kt` com dados de exemplo incluindo maps com miniatura
      (estado com lista preenchida).
- [x] 6.2 Criar `AdminMapViewerPreview.kt` mostrando a tela cheia com uma imagem de exemplo.

## 7. Acessibilidade

- [x] 7.1 Rodar o skill `accessibility-audit` sobre o carousel atualizado e sobre
      `AdminMapViewerScreen`, aplicando os ajustes que ele apontar antes do merge.

## 8. Validação manual

- [ ] 8.1 Rodar o app em Android e iOS: carousel mostra nome + miniatura de cada map
      cadastrado.
- [ ] 8.2 Validar que tocar em um item abre a imagem em tela cheia e que é possível rolar
      quando a imagem é maior que a tela.
- [ ] 8.3 Validar que um map com imagem inválida/ausente não aparece na listagem, sem quebrar
      a exibição dos demais.
