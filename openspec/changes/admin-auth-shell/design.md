## Context

`feature/admin/login` hoje é um Composable estático (`LoginScreen`), sem
`ViewModel`/`domain`/`data`, com um botão "Entrar" no-op e um `combinedClickable`
de long-press que pula direto para `Home` — um atalho de debug que precisa sumir.
Não existe conceito de sessão em lugar nenhum do app: `Navigation.kt` monta um
`NavHost` plano (`Home`, `Search`, `Login`, `BeaconDebug`) sem nenhuma checagem de
autenticação.

O projeto já tem `dev.gitlive:firebase-auth` como dependência (`api` em
`shared/build.gradle.kts`), mas **nunca foi usado** — só `Firebase.firestore` foi
exercitado, e apenas por um spike descartável de healthcheck
(`shared/healthcheck/FirebaseHealthcheck.kt`). Koin está inicializado
(`shared/di/KoinInit.kt`, `initAppKoin` em `:composeApp`) e o único módulo real
registrado hoje é `beaconDebugModule` — é o precedente de padrão a seguir para o
novo `adminLoginModule`. Não existe `shared/domain/` ainda; este change é o
primeiro a precisar dele, porque sessão de admin é lida por 2+ consumidores
(a própria feature de login e a casca de navegação/guarda de rota).

## Goals / Non-Goals

**Goals:**
- Login admin real via Firebase Auth (e-mail/senha), com estado de sessão
  observável em `Flow`.
- Guarda de rota reativa: qualquer rota admin sem sessão válida redireciona ao
  login, sem `if/else` de estado espalhado pelas telas (CLAUDE.md seção 4).
- Casca de navegação admin mínima (`AdminShell`) que sirva de ponto de entrada
  para as features administrativas futuras (Feature 6+), com ação de logout.
- Erros de domínio como valor (`sealed AdminAuthError`), nunca exceção crua
  vazando para a UI.

**Non-Goals:**
- Cadastro/edição de administradores (Feature 6) — o usuário de primeiro acesso é
  provisionado manualmente fora do app (console Firebase), não por fluxo de UI.
- Telas administrativas reais dentro da casca (mapas, beacons, pontos) — a
  `AdminShell` desta feature é um placeholder de navegação, não o conteúdo delas.
- Custom claims / RBAC granular — este change só distingue "autenticado" vs "não
  autenticado"; papéis/permissões ficam para a Feature 7 (Security Rules
  finais) conforme já apontado no proposal.
- Persistência de "lembrar-me" ou biometria — fora do escopo de UC03/RF08.

## Decisions

- **Entidades e porta de sessão em `shared/domain/auth/`** (`AdminSession`,
  `AdminAuthError` sealed, interface `AdminSessionRepository` com
  `observeSession(): Flow<AdminSessionState>`, `suspend fun login(email, password):
  Result<AdminSession>`, `suspend fun logout()`). Justificativa: é o primeiro caso
  real de domínio consumido por 2+ consumidores (login escreve a sessão,
  `:navigation`/`AdminShell` leem para guardar rota) — exatamente o critério da
  seção 1 do `CLAUDE.md` para promover algo a `:shared`. Alternativa descartada:
  deixar a interface só dentro de `feature/admin/login/domain` e o guard importar
  a feature diretamente — violaria a regra "nunca feature → feature" no momento em
  que uma futura feature administrativa precisar checar sessão.
- **`AdminSessionState` como sealed (`Loading`, `Authenticated`,
  `Unauthenticated`)**, não `AdminSession?` nu. Justificativa: o guard de rota
  precisa distinguir "ainda não sei" (evita redirecionar para Login antes do
  Firebase Auth restaurar a sessão persistida, no cold start) de "sei que não há
  sessão" (redireciona de fato).
- **Implementação concreta em `feature/admin/login/data`**
  (`FirebaseAdminSessionRepository`, sobre `dev.gitlive.firebase.auth.FirebaseAuth`,
  expondo `Firebase.auth.authStateChanged` mapeado para `AdminSessionState`),
  registrada no Koin como `single<AdminSessionRepository>`. `admin/login` é a
  única feature "dona" da integração com Firebase Auth; consumidores só
  enxergam a interface via Koin.
- **Guarda de rota via composable reativo, não navegação imperativa isolada.**
  `AdminRouteGuard(content)` injeta `AdminSessionRepository` via Koin,
  `collectAsState` no `observeSession()`, e usa `LaunchedEffect` para chamar
  `navigator.navigateClearingBackStack(Screen.Login)` quando o estado vira
  `Unauthenticated`; só compõe `content()` quando `Authenticated`. Cada rota admin
  (a começar por `Screen.AdminHome`) envolve seu conteúdo nesse guard dentro do
  `composable<...> { }` em `Navigation.kt`. Alternativa descartada: checar sessão
  uma vez só no nível do `NavHost`/`startDestination` — não cobre o caso de a
  sessão expirar/for revogada enquanto o admin já está em uma tela admin.
- **Logout não navega diretamente** — `AdminShellScreenRoute` chama
  `AdminSessionRepository.logout()` (via um `LogoutUseCase` fino em
  `shared/domain/auth`) e deixa o `AdminRouteGuard` reagir à mudança de estado
  para redirecionar. Único caminho de saída da área admin, evitando duplicar
  lógica de navegação de sessão em dois lugares.
- **`INavigator` ganha `navigateClearingBackStack(screen: Screen)`** (método novo,
  aditivo — não quebra os dois métodos existentes). Necessário para: (a) pós-login,
  ir para `Screen.AdminHome` sem deixar `Login` no back stack; (b) logout/expiração,
  voltar para `Screen.Login` sem permitir "voltar" para uma tela admin com back
  button.
- **`Screen.AdminHome`** (novo `data object` em `shared/navigation/Screen.kt`) é o
  destino único desta feature — a `AdminShell` propriamente dita (menu/estrutura
  para as features futuras) é composta dentro dele. Novas rotas admin (Feature 6+)
  se registram como novos `Screen.Admin*` e reusam `AdminRouteGuard`.
- **Erros mapeados para `AdminAuthError` sealed** (`EmptyFields`,
  `InvalidCredentials`, `NetworkUnavailable`, `Unknown`) dentro de um
  `LoginUseCase` em `feature/admin/login/domain`: valida campos vazios
  localmente (sem round-trip de rede) e classifica exceções do
  `dev.gitlive.firebase.auth` (tipos `FirebaseAuthInvalidCredentialsException`/
  `FirebaseAuthInvalidUserException` e falha de rede) via `runCatching`. Resultado
  como `Result<AdminSession, AdminAuthError>`-like (`kotlin.Result` com erro
  tipado interno, seguindo seção 7 — nunca `!!`, nunca exceção crua na
  `presentation`).
- **Provisionamento do usuário de primeiro acesso é manual** (Firebase Console,
  provedor Email/Senha), documentado como passo de deploy — não há tela de
  cadastro nesta feature (fora de escopo, é a Feature 6).

## Risks / Trade-offs

- [Risco] Corrida entre a restauração de sessão persistida do Firebase Auth e o
  primeiro frame do Compose pode piscar `Login` antes de mostrar `AdminHome` para
  quem já estava logado → Mitigação: estado `Loading` explícito no guard; só
  redireciona em `Unauthenticated`, nunca em `Loading`.
- [Risco] A hierarquia de exceções do GitLive pode variar sutilmente entre
  Android e iOS (SDKs nativos diferentes por baixo) → classificação de erro pode
  deixar passar algum caso para `Unknown` → Mitigação: bucket `Unknown` sempre
  mostra mensagem genérica de erro (nunca falha silenciosa); validar
  manualmente em ambas as plataformas antes do merge.
- [Risco] Extender `INavigator` é uma mudança em contrato compartilhado por
  todas as features → Mitigação: método aditivo, comportamento existente
  inalterado, sem breaking change de assinatura.
- [Risco] Sem tela de provisionamento, um usuário de primeiro acesso mal
  configurado no console trava o acesso admin sem recurso dentro do app →
  Mitigação: documentar o passo manual explicitamente no `tasks.md`/README de
  deploy; aceito como trade-off porque cadastro de admin é a Feature 6.
- [Risco] `firestore.rules` para `administrators` sem o schema final (Feature 7)
  pode ficar restritivo demais e bloquear leitura legítima → Mitigação: regra
  desta feature é propositalmente conservadora (leitura só para autenticado,
  escrita sempre negada) e será revisada quando o schema fechar.

## Migration Plan

1. Habilitar o provedor Email/Senha no Firebase Auth (console, ambientes de dev
   e produção) e criar manualmente o usuário de primeiro acesso — passo de
   infraestrutura, fora do código do app.
2. Adicionar `shared/domain/auth/` (entidades + interface) — aditivo, sem
   impacto em código existente.
3. Adicionar `data`/`domain`/`presentation/viewmodel` de `feature/admin/login` +
   `adminLoginModule` no Koin — aditivo.
4. Trocar `LoginScreen` do botão no-op para `LoginViewModel` real e remover o
   `combinedClickable` de long-press — muda comportamento da tela existente,
   mas isolado a este arquivo.
5. Adicionar `Screen.AdminHome`, `AdminRouteGuard`, `AdminShellScreenRoute` e
   registrar em `Navigation.kt`; adicionar `navigateClearingBackStack` em
   `INavigator`/`NavigatorImpl` — aditivo em rota/contrato, mas o comportamento
   de "long-press vai para Home sem login" deixa de existir (**BREAKING**, já
   sinalizado no proposal).
6. Atualizar `firestore.rules` com a regra de `administrators` ao lado do stub
   de `healthcheck` já existente.

Rollback: todas as mudanças são reversíveis por revert de commit — não há
migração de schema/dado envolvida (usuários do Firebase Auth não são afetados
por um rollback do app).

## Open Questions

- `authStateChanged` vs `idTokenChanges` do GitLive: esta feature usa
  `authStateChanged` (login/logout puro) por ser suficiente para gating binário;
  reavaliar se a Feature 7 (custom claims) precisar reagir a mudança de claims
  sem novo login.
- Estrutura interna da `AdminShell` (menu lateral, tabs, etc.) fica em aberto —
  esta feature entrega só o destino/guarda; o desenho visual real é definido
  quando a primeira feature administrativa de conteúdo (Feature 6) for
  proposta.
