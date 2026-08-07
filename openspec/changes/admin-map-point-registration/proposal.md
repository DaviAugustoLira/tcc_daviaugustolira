## Why

O painel administrativo já cadastra e visualiza *maps* (change `admin-map-listing`), mas não existe
nenhuma forma de marcar **onde**, dentro de um map, ficam os destinos navegáveis — pré-requisito
direto para o algoritmo de roteamento (Dijkstra) e para o usuário final buscar um destino (UC01).
Hoje a tela cheia do map (`AdminMapViewerScreen`) só exibe a imagem em tamanho intrínseco
(`ContentScale.None`) dentro de um contêiner de scroll, sem zoom — o que deixa o floor plan pequeno
demais na tela para o admin apontar um local com a precisão de pixel exigida.

## What Changes

- `AdminMapViewerScreen` (`feature/admin/login`) ganha um **modo de cadastro de destino**: a base de
  exibição do map deixa de ser o tamanho intrínseco (`ContentScale.None`) e passa a ajustar à largura
  da tela por padrão, com **pinça para zoom + arraste para navegar** (substituindo o scroll simples
  atual), permitindo ao admin ampliar até o nível de precisão de pixel necessário.
- Ao tocar no map com o modo de cadastro ativo, o sistema captura a posição do toque, converte para
  as **coordenadas de pixel da imagem original** (independente do zoom/posição atuais) e abre um
  formulário (nome + descrição) para confirmar ou descartar o novo destino.
- Ao confirmar, um documento `points/{id}` é gravado no Firestore com `mapId`, `name`, `description`,
  `x`, `y` (pixel), `createdBy`, `isActive`, `updatedAt`.
- Destinos já cadastrados no map aberto aparecem como marcadores sobre a imagem (mesmo fora do modo
  de cadastro), para o admin não duplicar cadastro e conferir o que já existe.
- Alternativa acessível ao toque: campos numéricos de X/Y para inserir a coordenada manualmente,
  seguindo o mesmo fluxo de confirmação (nome/descrição) — necessário porque o gesto de toque sozinho
  não é uma via garantida para quem usa leitor de tela (Seção 2 do CLAUDE.md).
- Novo domínio `points` em `shared/domain/points` (`Point`, `PointsRepository`, `NewPoint`,
  `CreatePointOutcome`, `CreatePointError`), espelhando o padrão já usado por `maps` — é dado do
  schema do TCC consumido futuramente pela navegação do usuário final, não só pelo admin.
- Nova implementação `FirestorePointsRepository` + DTOs (`feature/admin/login/data`) e regra de
  segurança para a coleção `points` em `firestore.rules` (hoje fechada pelo catch-all).
- `Screen.AdminMapViewer` passa a receber também `mapId` (parâmetro aditivo), necessário para saber a
  qual map associar os pontos cadastrados nessa tela.
- `AdminMapViewerScreen` deixa de ser um Composable sem lógica de negócio e ganha
  `AdminMapViewerViewModel` (MVI: observa pontos do map, gerencia estado do modo de cadastro e do
  formulário, chama `CreatePointUseCase`).

## Capabilities

### New Capabilities
- `admin-map-point-registration`: visualização ampliável (zoom/pan) de um map com marcação de
  destinos por toque ou coordenada manual, cadastro de nome/descrição e persistência em
  `points/{id}` no Firestore, incluindo exibição dos destinos já cadastrados sobre a imagem.

### Modified Capabilities
(nenhuma — `openspec/specs/` ainda não tem nenhuma capability arquivada; a mudança de comportamento
da tela cheia de map introduzida por `admin-map-listing`, ainda não commitada/arquivada, é tratada
como parte do trabalho desta change em vez de um delta formal contra uma spec inexistente.)

## Impact

- `shared/src/commonMain/.../shared/domain/points/`: novo — `Point.kt`, `PointsRepository.kt`,
  `NewPoint.kt`, `CreatePointOutcome.kt`, `CreatePointError.kt`.
- `shared/src/commonMain/.../shared/navigation/Screen.kt`: `AdminMapViewer` ganha `mapId: String`.
- `navigation/.../Navigation.kt`: repassa `mapId` ao compor `AdminMapViewerScreenRoute`.
- `feature/admin/login/.../data/`: novos `PointDto.kt`, `NewPointDto.kt`, `PointMapper.kt`,
  `FirestorePointsRepository.kt`.
- `feature/admin/login/.../domain/`: novos `ObservePointsUseCase.kt`, `CreatePointUseCase.kt`.
- `feature/admin/login/.../presentation/viewmodel/`: novo `AdminMapViewerViewModel.kt`
  (`AdminMapViewerState`, `AdminMapViewerIntent`).
- `feature/admin/login/.../presentation/screen/AdminMapViewerScreen.kt`: reescrita para
  zoom/pan, overlay de marcadores, toggle de modo de cadastro, formulário de nome/descrição e
  entrada manual de coordenada.
- `feature/admin/login/.../presentation/screen/AdminShellScreen.kt`: chamada de navegação do
  carousel passa a incluir `map.id`.
- `feature/admin/login/di/AdminLoginModule.kt`: registra `PointsRepository`,
  `ObservePointsUseCase`, `CreatePointUseCase`, e injeta o novo use case no
  `AdminMapViewerViewModel`.
- `firestore.rules`: nova regra `match /points/{pointId}`.
- `composeApp` (Android): atualiza `AdminMapViewerPreview.kt` com estados de exemplo (modo normal,
  modo de cadastro, com pontos já existentes).
- Sem mudança em `maps`/`administrators` além do já existente.
