## Context

`AdminMapViewerScreen` (`feature/admin/login/.../presentation/screen/AdminMapViewerScreen.kt`,
introduzido pela change `admin-map-listing`, ainda não commitado) hoje é um Composable **sem
ViewModel**: recebe `name`/`imageUrl` via `Screen.AdminMapViewer(name, imageUrl)` e só desenha a
imagem (`AsyncImage`, Coil3, `ContentScale.None`) dentro de um `Box` com
`verticalScroll`+`horizontalScroll`. Não existe zoom, `pointerInput`, `graphicsLayer` ou qualquer
captura de gesto em lugar nenhum do projeto — confirmado por busca completa no repositório. O
non-goal explícito do design de `admin-map-listing` era "zoom por pinça... fora do escopo desta
primeira versão"; esta change é exatamente esse trabalho adiado, agora motivado por uma necessidade
concreta (marcar destino com precisão de pixel), não só por completude.

`IndoorMap` (domínio) só expõe `id`, `name`, `imageUrl` — não guarda largura/altura da imagem. O
tamanho intrínseco real só é conhecido em tempo de execução, quando o Coil termina de decodificar a
imagem no cliente. `Screen.AdminMapViewer` também não carrega `mapId`, porque a change anterior
decidiu deliberadamente não precisar dele (a tela só reexibe nome/URL já carregados pelo carousel).

Não existe, em lugar nenhum do repositório, nenhum conceito de "ponto"/"destino"/"pin" — nem
entidade, nem DTO, nem coleção Firestore, nem regra de segurança. É trabalho novo, seguindo o
mesmo padrão em 3 camadas já estabelecido por `maps` (`shared/domain/maps` → `feature/admin/login
/data` → Firestore rule), que serve de referência direta para `points`.

## Goals / Non-Goals

**Goals:**
- Deixar a imagem do map visivelmente maior por padrão (ajustada à largura da tela em vez do
  tamanho intrínseco) e permitir ampliar mais via pinça, para que o admin consiga apontar um local
  com precisão de pixel.
- Capturar o toque no modo de cadastro e convertê-lo para a coordenada de pixel da imagem
  original, independente do zoom/posição de rolagem no momento do toque.
- Persistir o destino (`points/{id}`: `mapId`, `name`, `description`, `x`, `y`, `createdBy`) no
  Firestore, protegido por regra de segurança própria.
- Mostrar os destinos já cadastrados do map aberto como marcadores sobre a imagem.
- Garantir um caminho de cadastro que não dependa só do gesto de toque (entrada manual de X/Y),
  para não regredir a acessibilidade do painel (Seção 2 do CLAUDE.md).

**Non-Goals:**
- Editar ou excluir um destino já cadastrado — só criação nesta versão (mesmo tratamento
  "write-once" hoje aplicado a `maps`).
- Vincular o destino a beacons/fingerprinting ou a qualquer cálculo de rota — esta change só
  registra a coordenada; consumo pelo algoritmo de roteamento é de uma feature futura.
- Redimensionar/recomprimir a imagem do map ou mudar como `svgUrl`/`imageUrl` é gravado no
  cadastro do map (`CreateMapUseCase`) — fora do escopo, é responsabilidade de `admin-map-listing`.
- Suporte a múltiplos administradores editando o mesmo map simultaneamente (conflito de
  cadastro concorrente) — fora de escopo, mesmo nível de simplicidade do restante do painel hoje.

## Decisions

### 1. Estender `AdminMapViewerScreen` no lugar, em vez de criar uma tela nova
O pedido do usuário é literalmente "dentro da tela do mapa" o admin cadastra destinos. Uma tela
nova duplicaria toda a lógica de zoom/pan/overlay de marcadores que a visualização já precisa ter.
`Screen.AdminMapViewer` continua sendo o único destino ao abrir um map pelo carousel; um toggle
("Cadastrar destino") liga/desliga o modo de cadastro dentro da mesma tela.
**Alternativa rejeitada:** `Screen.AdminRegisterPoint` separada, reaproveitando composables via
extração — mais indireção sem benefício real, e diverge do pedido explícito do usuário.

### 2. `Screen.AdminMapViewer` ganha `mapId: String` (parâmetro aditivo)
Os pontos precisam de `mapId` para saber a qual map pertencem, e a tela precisa saber qual map
está exibindo para `observeMaps`-like query de pontos e para gravar o novo ponto. `AdminShellScreen`
(carousel) já tem `map.id` disponível em memória — só passa a incluí-lo na navegação.
**Alternativa rejeitada:** buscar o `mapId` por nome via nova query — frágil (nomes não são
garantidamente únicos) e reintroduz uma chamada de rede que o design anterior evitou de propósito.

### 3. `AdminMapViewerScreen` ganha `AdminMapViewerViewModel` (MVI)
A tela deixa de ser "sem lógica de negócio" no momento em que precisa observar pontos existentes,
gerenciar o estado do modo de cadastro/formulário e chamar `CreatePointUseCase` — exatamente o
gatilho que a seção "Estado atual vs. especificação" do CLAUDE.md prevê para adicionar camadas.
Segue o padrão MVI já usado por `AdminShellViewModel`/`CreateMapViewModel`: `AdminMapViewerState`
(imutável) + `AdminMapViewerIntent` (sealed) + `Dispatchers` injetados.

`AdminMapViewerState` (esboço):
```kotlin
data class AdminMapViewerState(
    val mapId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val points: List<Point> = emptyList(),
    val isPickModeActive: Boolean = false,
    val pendingPoint: PendingPixel? = null, // x, y capturados, aguardando confirmação
    val pointName: String = "",
    val pointDescription: String = "",
    val isSubmittingPoint: Boolean = false,
    val error: CreatePointError? = null,
)
```
`AdminMapViewerIntent`: `TogglePickMode`, `TapAt(xPx: Int, yPx: Int)`, `ManualCoordinateEntered(x:
Int, y: Int)`, `PointNameChanged`, `PointDescriptionChanged`, `ConfirmPoint`, `CancelPendingPoint`.

### 4. Base de exibição muda de `ContentScale.None` para ajuste à largura + zoom por pinça/arraste
`ContentScale.None` desenha a imagem no tamanho intrínseco, que pode ser bem menor que a tela para
um floor plan de resolução modesta — é o "mapa pequeno" relatado. A nova base exibe a imagem
ajustada à largura disponível (`fitScale = viewportWidthPx / imageIntrinsicWidthPx`, altura
proporcional) como zoom = 1×, e um `Modifier.pointerInput { detectTransformGestures { ... } }`
aplica um `graphicsLayer(scaleX = zoom, scaleY = zoom, translationX = panX, translationY = panY)`
por cima, com zoom limitado a um intervalo razoável (1×–6×, ajuste de pedido posterior do usuário:
zoom **inicial** de 2× — o mapa já nasce ampliado, sem exigir pinça para o primeiro toque de
precisão — permanecendo livre para arrastar/ampliar/reduzir dentro do intervalo) e pan restrito
para não perder a imagem de vista completamente. O scroll de dois eixos existente é removido: pan
por arraste (parte do mesmo gesto de transformação) cobre o mesmo caso de uso em qualquer nível de
zoom, e não dá para
compor scroll com pan/zoom de forma consistente.
**Alternativa rejeitada:** manter `ContentScale.None` + só adicionar zoom em cima — a imagem já
nasceria pequena demais, exigindo zoom excessivo para ficar utilizável.

### 5. Mapeamento de toque → pixel da imagem original
Coordenadas de toque chegam em espaço de tela (pós-transformação). Fórmula de conversão, calculada
com o `zoom`/`pan` correntes e o `fitScale` (goal 4) e o tamanho intrínseco reportado pelo Coil
(via `AsyncImagePainter` -> `onState`/`onSuccess`, capturando `intrinsicSize` assim que a imagem
carrega):
```
contentLocal = (tapScreenOffset - pan) / zoom   // desfaz pan/zoom
imagePixel   = contentLocal / fitScale           // desfaz o ajuste à largura
```
Resultado arredondado para `Int` e limitado a `[0, intrinsicWidth-1] x [0, intrinsicHeight-1]`. É
uma função pura (`Offset, Offset, Float, Float, IntSize -> IntOffset`), testável em `commonTest`
sem depender de plataforma, mesmo não sendo um algoritmo de posicionamento RF — é geometria de UI,
mas ainda assim testável e sem estado.
**Alternativa rejeitada:** gravar coordenadas relativas à viewport/zoom no momento do toque — inútil
para o consumidor final (a navegação real precisa do pixel na imagem original, não de um valor que
muda a cada sessão de zoom diferente).

### 6. Fluxo de confirmação e modo de cadastro persistente entre cadastros
Toque válido no modo de cadastro → marcador temporário + formulário (nome, descrição) abre.
Confirmar chama `CreatePointUseCase`; sucesso adiciona o ponto à lista observada (via
`ObservePointsUseCase`, que já vai refletir o novo doc do Firestore), limpa o formulário e
**permanece no modo de cadastro** para o próximo destino — sair do modo exige toggle explícito
("Concluir cadastro"). Reduz atrito para o caso comum de cadastrar vários destinos em sequência no
mesmo map.
**Alternativa rejeitada:** sair do modo de cadastro automaticamente após cada confirmação —
obrigaria reativar o toggle a cada destino.

### 7. Entrada manual de coordenada como alternativa acessível ao toque
Ao lado do toggle de modo de cadastro, um campo "Inserir coordenada manualmente" (dois
`CustomTextField` numéricos, X e Y) alimenta o mesmo `pendingPoint`/formulário de
nome-descrição — o toque na imagem é uma forma rápida de preencher esses mesmos campos, não o
único caminho possível. Necessário porque depender só de um gesto de toque livre sobre uma imagem
grande não é um fluxo confiável para quem usa TalkBack/VoiceOver (Seção 2 do CLAUDE.md exige que
nenhuma decisão de código degrade a experiência com leitor de tela). Roda pelo skill
`accessibility-audit` antes do merge, como todas as telas tocadas por esta change.

### 8. Domínio `points` em `shared/domain/points/`, espelhando `maps`
```kotlin
data class Point(val id: String, val mapId: String, val name: String, val description: String,
                  val x: Int, val y: Int)

interface PointsRepository {
    fun observePoints(mapId: String): Flow<List<Point>>
    suspend fun createPoint(point: NewPoint, createdByUid: String): CreatePointOutcome
}

data class NewPoint(val mapId: String, val name: String, val description: String,
                     val x: Int, val y: Int)

sealed interface CreatePointOutcome {
    data object Success : CreatePointOutcome
    data class Failure(val error: CreatePointError) : CreatePointOutcome
}

sealed interface CreatePointError {
    data object EmptyName : CreatePointError
    data object InvalidCoordinate : CreatePointError
    data object NoActiveSession : CreatePointError
    data object NetworkUnavailable : CreatePointError
    data class Unknown(val message: String?) : CreatePointError
}
```
Vai para `:shared` (não fica só em `feature/admin/login/domain`) porque `points` é, pelo próprio
schema do TCC (CLAUDE.md Seção 9), dado consumido depois pela navegação do usuário final — mesmo
critério que promoveu `maps` para `:shared` (uso por 2+ consumidores, presente ou futuro
já-nomeado). Implementação concreta (`FirestorePointsRepository`, sobre
`Firebase.firestore.collection("points").where("mapId", "==", mapId)`) fica em
`feature/admin/login/data`, único dono da integração de escrita hoje — mesmo padrão de `maps`.

### 9. Documento `points/{autoId}` e regra de segurança
Campos gravados (`NewPointDto`): `mapId: String, name: String, description: String, x: Int (Long no
Firestore), y: Int, createdBy: String, isActive: Boolean = true, updatedAt: BaseTimestamp =
Timestamp.ServerTimestamp` — mesmo formato de `NewMapDto`. Regra em `firestore.rules` (gerada via
skill `firestore-collection` para manter o padrão do projeto):
```
match /points/{pointId} {
  allow read: if request.auth != null;
  allow create: if request.auth != null
    && request.resource.data.createdBy == request.auth.uid
    && request.resource.data.mapId is string && request.resource.data.mapId.size() > 0
    && request.resource.data.name is string && request.resource.data.name.size() > 0
    && request.resource.data.x is int && request.resource.data.x >= 0
    && request.resource.data.y is int && request.resource.data.y >= 0;
  allow update, delete: if false;
}
```
Não valida que `mapId` referencia um `maps/{mapId}` existente (exigiria um `get()` cruzado, custo
extra sem necessidade real hoje — só o admin autenticado escreve, e a UI só oferece mapIds válidos
vindos do carousel).

### 10. Marcadores de destinos existentes sempre visíveis (dentro ou fora do modo de cadastro)
`ObservePointsUseCase(mapId)` já precisa rodar para o cadastro funcionar (evitar duplicar); exibir
os marcadores mesmo fora do modo de cadastro é custo marginal zero e evita que o admin cadastre o
mesmo destino duas vezes por não saber que ele já existe.

## Risks / Trade-offs

- [Risco] Precisão do toque continua limitada pelo dedo/tela mesmo após zoom → Mitigação: zoom até
  6× + entrada manual de X/Y para quando precisão exata for necessária (Decisão 7).
- [Risco] Regra do Firestore não valida existência do `mapId` referenciado → Mitigação: aceitável
  porque só a UI do admin (que sempre usa um `mapId` de um map real já carregado) escreve nessa
  coleção hoje; reavaliar se surgir escrita programática externa.
- [Risco] Se o map for recadastrado com uma imagem de dimensões diferentes no futuro, pixels salvos
  em `points` ficam desalinhados → Mitigação: `maps` já é write-once (sem update/delete) — mesma
  limitação pré-existente, não introduzida por esta change.
- [Risco] Trocar scroll por gesto de pan/zoom pode conflitar com o gesto de voltar do sistema em
  algumas plataformas → Mitigação: mesmo cuidado já sinalizado em `admin-map-listing`; validar
  manualmente em Android e iOS antes do merge.

## Open Questions

- Limite máximo de zoom (proposto 6×) e sensibilidade do pan são um ponto de partida — ajustar
  durante a implementação/`accessibility-audit` se a leitura dos marcadores ficar prejudicada em
  algum nível de zoom.
