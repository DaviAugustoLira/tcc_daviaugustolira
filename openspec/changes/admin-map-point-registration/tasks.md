## 1. Domínio `points` (`shared/domain/points`)

- [x] 1.1 Criar `Point.kt` (`id, mapId, name, description, x: Int, y: Int`).
- [x] 1.2 Criar `NewPoint.kt` (`mapId, name, description, x: Int, y: Int`).
- [x] 1.3 Criar `CreatePointOutcome.kt` (sealed `Success`/`Failure(error)`).
- [x] 1.4 Criar `CreatePointError.kt` (sealed `EmptyName`, `InvalidCoordinate`,
      `NoActiveSession`, `NetworkUnavailable`, `Unknown(message)`).
- [x] 1.5 Criar interface `PointsRepository.kt` (`fun observePoints(mapId: String):
      Flow<List<Point>>`, `suspend fun createPoint(point: NewPoint, createdByUid: String):
      CreatePointOutcome`).

## 2. Dados e Firestore (`feature/admin/login/data` + `firestore.rules`)

- [x] 2.1 Usar o skill `firestore-collection` para gerar o scaffold da coleção `points`
      (DTO, mapper, regra de segurança), ajustando ao formato definido no design.md.
- [x] 2.2 Criar `PointDto.kt` (`@Serializable`, campos opcionais espelhando o documento:
      `mapId: String? = null, name: String? = null, description: String? = null, x: Long? =
      null, y: Long? = null`).
- [x] 2.3 Criar `NewPointDto.kt` (`mapId, name, description, x: Long, y: Long, createdBy,
      isActive = true, updatedAt: BaseTimestamp = Timestamp.ServerTimestamp`).
- [x] 2.4 Criar `PointMapper.kt` (`PointDto.toDomain(id): Result<Point>`, falha em
      `Result.failure` se `mapId`/`name` ausentes/vazios ou `x`/`y` ausentes — mesmo padrão de
      `MapMapper`).
- [x] 2.5 Criar `FirestorePointsRepository.kt` implementando `PointsRepository`:
      `observePoints(mapId)` via `firestore.collection("points").where("mapId", "==",
      mapId).snapshots`; `createPoint` grava `NewPointDto` e classifica
      `FirebaseNetworkException`/`FirebaseException` em `CreatePointOutcome.Failure`.
- [x] 2.6 Adicionar `match /points/{pointId}` em `firestore.rules` (leitura autenticada;
      criação exige `createdBy == request.auth.uid` e `mapId`/`name` não vazios e `x`/`y`
      numéricos `>= 0`; `update`/`delete` negados).

## 3. Casos de uso (`feature/admin/login/domain`)

- [x] 3.1 Criar `ObservePointsUseCase.kt` (delega a `PointsRepository.observePoints(mapId)`).
- [x] 3.2 Criar `CreatePointUseCase.kt` (valida nome não vazio e coordenada não negativa antes
      do round-trip de rede, chama `PointsRepository.createPoint`, mapeia exceção →
      `CreatePointError`, segue o padrão de validação local já usado por `LoginUseCase`).

## 4. Navegação

- [x] 4.1 Adicionar `mapId: String` a `Screen.AdminMapViewer` em
      `shared/src/commonMain/.../shared/navigation/Screen.kt` (parâmetro aditivo).
- [x] 4.2 Atualizar a chamada de navegação do carousel em `AdminShellScreen.kt`
      (`onOpenMap`) para incluir `mapId = map.id`.
- [x] 4.3 Atualizar `composable<Screen.AdminMapViewer>` em `:navigation` (`Navigation.kt`)
      para repassar `mapId` ao compor `AdminMapViewerScreenRoute` — já satisfeito, `screen =
      backStackEntry.toRoute()` repassa o objeto `Screen.AdminMapViewer` inteiro (agora com
      `mapId`); nenhuma mudança adicional necessária neste arquivo.

## 5. `AdminMapViewerViewModel` (MVI)

- [x] 5.1 Criar `AdminMapViewerState` (`mapId, name, imageUrl, points, isPickModeActive,
      pendingPoint, pointName, pointDescription, isSubmittingPoint, error`) e
      `AdminMapViewerIntent` (`TogglePickMode`, `TapAt(xPx, yPx)`,
      `ManualCoordinateEntered(x, y)`, `PointNameChanged`, `PointDescriptionChanged`,
      `ConfirmPoint`, `CancelPendingPoint`) em `presentation/viewmodel`.
- [x] 5.2 Criar `AdminMapViewerViewModel` usando `ObservePointsUseCase`/`CreatePointUseCase`.
      Nenhum `Dispatchers` hardcoded (mesmo padrão de `AdminShellViewModel`/`CreateMapViewModel`
      — ambos já não injetam um `Dispatchers` explícito, pois as suspend functions do GitLive
      Firestore já são main-safe; a regra do CLAUDE.md seção 6 é não hardcodar `Dispatchers.IO`,
      o que não ocorre aqui).

## 6. Zoom e pan da imagem

- [x] 6.1 Trocar a base de exibição de `ContentScale.None` para escala ajustada à largura da
      viewport (`fitScale`), calculada a partir do tamanho intrínseco reportado pelo Coil
      (`AsyncImagePainter`/`onSuccess`).
- [x] 6.2 Remover `verticalScroll`/`horizontalScroll` e adicionar
      `Modifier.transformable(rememberTransformableState { ... })` + `graphicsLayer(scaleX =
      combinedScale, scaleY = combinedScale, translationX = pan.x, translationY = pan.y)`, com
      zoom limitado a 1×–6× e pan restrito para não perder a imagem de vista (usou a API de
      alto nível `transformable`/`rememberTransformableState` do Compose Foundation em vez de
      `detectTransformGestures` manual — mesmo resultado, menos matemática de gesto reescrita
      à mão).
- [x] 6.3 Implementar a função pura de mapeamento toque → pixel da imagem original
      (`mapTapToImagePixel` em `presentation/viewmodel/MapPixelMapper.kt`), independente de
      Composable.

## 7. Modo de cadastro e captura de toque

- [x] 7.1 Adicionar controle (botão/toggle) "Cadastrar destino" que dispara
      `AdminMapViewerIntent.TogglePickMode`, com `contentDescription` refletindo o estado
      atual (ativar/desativar) e alvo de toque ≥ 48dp.
- [x] 7.2 Adicionar `Modifier.pointerInput(isPickModeActive) { detectTapGestures { offset ->
      ... } }` sobre a imagem, ativo apenas quando `isPickModeActive`, convertendo o toque via
      `mapTapToImagePixel` e disparando `AdminMapViewerIntent.TapAt`.
- [x] 7.3 Renderizar marcador temporário na posição de `pendingPoint` e marcadores
      definitivos para cada item de `state.points`, sobre a imagem, respeitando
      zoom/pan atuais (filhos do mesmo `Box` com `graphicsLayer`, posicionados via
      `Modifier.offset { IntOffset(...) }` em pixels da imagem original).

## 8. Formulário de nome/descrição e entrada manual de coordenada

- [x] 8.1 Criar formulário (`AdminMapViewerPendingForm`, ancorado ao rodapé da tela) com
      campos nome/descrição (`CustomTextField`, mesmo componente de `CreateMapScreen`), exibido
      quando `pendingPoint != null`, com ações "Confirmar"/"Cancelar" disparando `ConfirmPoint`/
      `CancelPendingPoint`.
- [x] 8.2 Adicionar campos numéricos de X/Y ("Inserir coordenada manualmente") que disparam
      `ManualCoordinateEntered`, reaproveitando o mesmo formulário de nome/descrição da
      tarefa 8.1 — visível independente do modo de cadastro estar ativo (alternativa acessível
      ao gesto, Decisão 7 do design.md).
- [x] 8.3 Exibir mensagens de erro distintas para nome vazio, coordenada inválida, sessão
      expirada e falha de rede (`CreatePointError` → texto), seguindo o padrão de
      `errorMessage(CreateMapError)` em `CreateMapScreen.kt`.

## 9. Injeção de dependência (Koin)

- [x] 9.1 Registrar `single<PointsRepository> { FirestorePointsRepository() }`,
      `factory { ObservePointsUseCase(get()) }`, `factory { CreatePointUseCase(get(), get()) }`
      e `factory { (mapId, name, imageUrl) -> AdminMapViewerViewModel(mapId, name, imageUrl,
      get(), get()) }` em `AdminLoginModule.kt`.
- [x] 9.2 Ligar `AdminMapViewerScreenRoute` ao `AdminMapViewerViewModel` via
      `koinViewModel { parametersOf(screen.mapId, screen.name, screen.imageUrl) }`.

## 10. Testes (`commonTest`)

- [x] 10.1 `PointMapperTest`: mapeamento válido; falha com `mapId`/`name` ausente/vazio;
      falha com `x`/`y` ausente.
- [x] 10.2 `FakePointsRepository` (em memória, sem Firebase real) em
      `commonTest/domain`, seguindo o padrão de `FakeMapsRepository`.
- [x] 10.3 `ObservePointsUseCaseTest` e `CreatePointUseCaseTest` (campos vazios, coordenada
      inválida, falha de rede, sucesso) usando o fake.
- [x] 10.4 Teste da função pura de mapeamento toque → pixel (tarefa 6.3): mesmo ponto visual
      em zooms/pans diferentes produz a mesma coordenada de pixel; valores são limitados aos
      limites da imagem.
- [x] 10.5 `AdminMapViewerViewModelTest`: transições de estado por `AdminMapViewerIntent`
      (ativar/desativar modo de cadastro, toque gera `pendingPoint`, confirmar persiste e
      mantém o modo ativo, cancelar limpa o pendente, erro de criação é refletido no estado).

## 11. Previews (`:composeApp`)

- [x] 11.1 Atualizar `AdminMapViewerPreview.kt` com estados de exemplo: visualização normal
      com destinos existentes, modo de cadastro ativo com um destino pendente e formulário
      aberto.

## 12. Acessibilidade

- [x] 12.1 Rodar o skill `accessibility-audit` sobre o `AdminMapViewerScreen` atualizado
      (toggle de modo, marcadores, formulário, entrada manual de coordenada). Correções
      aplicadas: ordem de foco priorizando os controles via `traversalIndex = -1f` na barra
      superior; marcadores agrupados em um único nó `mergeDescendants` com resumo ("N destinos
      cadastrados") em vez de um nó por pin; validação com anúncio (`liveRegion` +
      mensagem) na entrada manual de coordenada inválida; `heading()` no título do formulário
      "Novo destino"; anúncio + feedback tátil de sucesso ao cadastrar um destino (antes só
      refletido visualmente, via `AdminMapViewerEffect.PointCreated`).

## 13. Validação manual

- [ ] 13.1 Rodar o app em Android e iOS: abrir um map, ativar o modo de cadastro, tocar em um
      local e cadastrar nome/descrição — o destino aparece como marcador definitivo.
- [ ] 13.2 Validar que o mesmo destino visual, cadastrado em zooms diferentes, resulta na
      mesma coordenada persistida no Firestore.
- [ ] 13.3 Validar a entrada manual de coordenada como caminho alternativo ao toque.
- [ ] 13.4 Validar mensagens de erro para nome vazio e para dispositivo sem rede ao confirmar
      um destino.
- [ ] 13.5 Reabrir um map com destinos já cadastrados e confirmar que os marcadores aparecem
      mesmo fora do modo de cadastro.
