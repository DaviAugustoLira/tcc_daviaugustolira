## 1. Infraestrutura (fora do app)

- [ ] 1.1 Habilitar o provedor Email/Senha no Firebase Auth (console, ambiente
      de dev e produção)
- [ ] 1.2 Criar manualmente o usuário administrador de primeiro acesso
      (fora do app; documentar e-mail/processo de reset de senha usado)

## 2. `shared/domain/auth` (kernel comum)

- [x] 2.1 Criar `AdminSession` (entidade: uid, e-mail) em
      `shared/domain/auth/AdminSession.kt`
- [x] 2.2 Criar `AdminSessionState` sealed (`Loading`, `Authenticated(session)`,
      `Unauthenticated`) em `shared/domain/auth/AdminSessionState.kt`
- [x] 2.3 Criar `AdminAuthError` sealed (`EmptyFields`, `InvalidCredentials`,
      `NetworkUnavailable`, `Unknown`) em `shared/domain/auth/AdminAuthError.kt`
- [x] 2.4 Criar interface `AdminSessionRepository` (`observeSession():
      Flow<AdminSessionState>`, `suspend fun login(email, password):
      Result<AdminSession>`, `suspend fun logout()`) em
      `shared/domain/auth/AdminSessionRepository.kt` — implementado com
      `LoginOutcome` (sealed Success/Failure) em vez de `kotlin.Result`, para
      carregar `AdminAuthError` tipado em vez de `Throwable` (ver design.md)
- [x] 2.5 Adicionar `Screen.AdminHome` em `shared/navigation/Screen.kt`
- [x] 2.6 Adicionar `navigateClearingBackStack(screen: Screen)` a `INavigator`
      em `shared/navigation/INavigator.kt`

## 3. `feature/admin/login` — domain e data

- [x] 3.1 Criar `LoginUseCase` (valida campos vazios sem round-trip de rede,
      chama `AdminSessionRepository.login`, mapeia exceção → `AdminAuthError`)
      em `feature/admin/login/domain`
- [x] 3.2 Criar `LogoutUseCase` (delega a `AdminSessionRepository.logout`) em
      `feature/admin/login/domain`
- [x] 3.3 Criar `ObserveSessionUseCase` (delega a
      `AdminSessionRepository.observeSession`) em `feature/admin/login/domain`
- [x] 3.4 Implementar `FirebaseAdminSessionRepository` sobre
      `dev.gitlive.firebase.auth.FirebaseAuth`, mapeando
      `Firebase.auth.authStateChanged` para `AdminSessionState` e classificando
      exceções de login em `feature/admin/login/data`
- [x] 3.5 Criar `adminLoginModule` (Koin) registrando
      `single<AdminSessionRepository>` e os use cases, seguindo o padrão de
      `beaconDebugModule`; registrar o módulo em `AppKoin.kt`

## 4. `feature/admin/login` — presentation (login)

- [x] 4.1 Criar `LoginState` (campos, erro atual, loading de submit) e
      `LoginIntent` (`EmailChanged`, `PasswordChanged`, `Submit`) em
      `presentation/viewmodel`
- [x] 4.2 Criar `LoginViewModel` usando `LoginUseCase`, expondo `LoginState`
      imutável e recebendo `LoginIntent`, com `Dispatchers` injetados (não
      hardcoded)
- [x] 4.3 Ligar `LoginScreenRoute`/`LoginScreen` ao `LoginViewModel` via Koin,
      substituindo o botão "Entrar" no-op pelo `Intent.Submit` real
- [x] 4.4 Remover o `combinedClickable`/atalho de long-press para `Home` de
      `LoginScreen`
- [x] 4.5 Exibir mensagens de erro distintas para campo vazio, credencial
      inválida e falha de rede na UI de login

## 5. Casca de navegação admin (`AdminShell`)

- [x] 5.1 Criar `AdminRouteGuard` (composable que injeta
      `ObserveSessionUseCase` via Koin, `collectAsState` no
      `AdminSessionState`, redireciona via `navigateClearingBackStack(Screen.Login)`
      em `Unauthenticated`, só renderiza conteúdo em `Authenticated`) em
      `:navigation` (ou `:shared/platform`/`ui` conforme conveniência de
      dependência — decidir no PR mantendo zero lógica de negócio embutida além
      da observação/redirecionamento) — ficou em `:navigation`
- [x] 5.2 Criar `AdminShellScreen`/`AdminShellScreenRoute` (placeholder mínimo
      com ação de logout chamando `LogoutUseCase`) em
      `feature/admin/login/presentation/screen` (ou novo módulo, se o escopo de
      "casca" crescer além do login — reavaliar durante o PR) — ficou dentro de
      `feature/admin/login`
- [x] 5.3 Registrar `composable<Screen.AdminHome> { AdminRouteGuard {
      AdminShellScreenRoute(navigator) } }` em `Navigation.kt`
- [x] 5.4 Após login bem-sucedido, navegar para `Screen.AdminHome` via
      `navigateClearingBackStack`

## 6. Segurança

- [x] 6.1 Adicionar regra em `firestore.rules` para a coleção
      `administrators`: leitura restrita a usuários autenticados, escrita
      sempre negada — mantendo o stub de `healthcheck` já existente
      intacto/isolado

## 7. Acessibilidade

- [x] 7.1 Rodar o skill `accessibility-audit` sobre `LoginScreen` e
      `AdminShellScreen` (contentDescription significativo, ordem de foco,
      alvo de toque ≥48dp, anúncio de erro por áudio/tátil) e aplicar as
      correções apontadas — corrigido: contentDescription redundante no campo
      de senha, senha sem `visualTransformation`, alvo de toque do "Voltar"
      abaixo de 48dp, e adicionado feedback tátil no logout

## 8. Testes

- [x] 8.1 Testes de `LoginUseCase` em `commonTest` (campos vazios, credencial
      inválida, falha de rede, sucesso) usando fake de `AdminSessionRepository`
      — sem Firebase real
- [x] 8.2 Testes de `LoginViewModel` (transições de `LoginState` por
      `LoginIntent`) em `commonTest`
- [x] 8.3 Teste do mapeamento `AdminSessionState` (Loading/Authenticated/
      Unauthenticated) a partir de um fake de sessão observável — coberto por
      `ObserveSessionUseCaseTest` (mais `LogoutUseCaseTest` para a transição
      para `Unauthenticated`)

## 9. Verificação manual

- [ ] 9.1 Rodar o app em Android e iOS: login com credenciais válidas leva à
      `AdminHome`; back button não retorna ao login
- [ ] 9.2 Validar redirecionamento ao login ao tentar acessar `Screen.AdminHome`
      sem sessão (ex.: deep link/estado limpo)
- [ ] 9.3 Validar logout: sessão encerrada, retorno ao login, back button não
      reexibe rota admin
- [ ] 9.4 Validar mensagens de erro para credencial inválida e para
      dispositivo sem rede em ambas as plataformas
