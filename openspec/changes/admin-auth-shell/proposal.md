## Why

Toda funcionalidade administrativa futura (cadastro de mapas, beacons, pontos,
administradores) depende de uma área protegida por autenticação. Hoje `admin/login`
é uma tela estática sem lógica (`presentation` puro, sem `ViewModel`/`domain`/`data`,
sem Koin, sem Firebase Auth) e não existe casca de navegação admin nem qualquer
mecanismo de proteção de rota — o app tem apenas rotas públicas (`Home`, `Search`,
`Login`, `BeaconDebug`) sem checagem de sessão. Esta é a base (UC03/RF08/RNF04) sobre
a qual todas as próximas features administrativas (Feature 6 em diante) serão
construídas.

## What Changes

- Implementar login administrativo real (e-mail + senha) via **Firebase Auth**
  (`dev.gitlive:firebase-auth`, já presente como dependência mas não utilizado),
  substituindo o botão "Entrar" no-op de `LoginScreen`.
- Adicionar `LoginViewModel` (MVI: `LoginState`/`LoginIntent`) com validação de
  campos obrigatórios, tratamento de erro de credencial inválida e de falha de rede,
  seguindo `domain`/`data`/`presentation` conforme `CLAUDE.md` seção 1.
- Introduzir `AdminSessionRepository` (interface em `domain`, implementação em `data`
  sobre `dev.gitlive.firebase.auth.FirebaseAuth`) para expor sessão atual como `Flow`
  e oferecer `login`, `logout`, `observeSession`.
- Adicionar casca de navegação admin (`AdminShell`) com um `NavHost` aninhado próprio
  para rotas administrativas, ponto único de entrada pós-login.
- Adicionar **guarda de rota**: qualquer rota admin sem sessão válida redireciona
  para `Screen.Login`; isso exige estender `INavigator`/`Screen` (novo grupo de
  rotas admin) e a lógica de checagem de sessão no ponto de composição das rotas.
- Adicionar logout que encerra a sessão do Firebase Auth e retorna a `Screen.Login`.
- Publicar `firestore.rules` restritivo já preparando a coleção `administrators`
  (leitura restrita a admins autenticados, escrita negada — será refinado na
  Feature 7) e remover/isolar o healthcheck spike de Firestore da seção 5 do
  `CLAUDE.md`, se conflitar com as regras novas.
- Registrar Koin (`admin-login` module: repositório + ViewModel) seguindo o padrão
  já usado em `feature/debug/beacons`.
- **BREAKING**: `Screen.Login` deixa de ser um "beco sem saída" com atalho de
  long-press para Home; navegar para rotas admin exige sessão válida.

## Capabilities

### New Capabilities
- `admin-auth`: login administrativo (e-mail + senha) via Firebase Auth, gestão de
  sessão (observar/expirar), logout, validação de campos, tratamento de erro de
  credencial e de rede, usuário de primeiro acesso pré-provisionado.
- `admin-shell`: casca de navegação administrativa (ponto de entrada pós-login,
  estrutura de rotas admin) e guarda de rota — bloqueio/redirecionamento de
  qualquer rota admin quando não há sessão válida.

### Modified Capabilities
(nenhuma — não há specs existentes em `openspec/specs/`)

## Impact

- **`feature/admin/login`**: ganha `domain` (entidades `AdminSession`,
  `AuthError`; casos de uso `LoginUseCase`, `LogoutUseCase`,
  `ObserveSessionUseCase`; interface `AdminSessionRepository`), `data`
  (`FirebaseAdminSessionRepository`, DTO/mapper de erro do Firebase Auth),
  `presentation/viewmodel` (`LoginViewModel`, `LoginState`, `LoginIntent`) e um
  módulo Koin (`adminLoginModule`).
- **`:shared`**: primeiro uso real de `shared/domain/` (hoje inexistente) caso
  `AdminSession`/`AuthError` sejam cruzados por `admin-shell`; novos `data object`
  em `Screen.kt` para as rotas admin; `INavigator` pode precisar de um método para
  navegação com limpeza de back stack (necessário para logout/redirecionamento sem
  permitir "voltar" para uma tela admin).
- **`:navigation`**: `Navigation.kt` ganha a checagem de sessão/guarda de rota antes
  de compor as rotas admin; possível `NavHost` aninhado para a casca admin.
- **Firebase**: primeiro uso de `Firebase.auth` (GitLive) no projeto; requer
  habilitar o provedor Email/Senha no console Firebase e criar o usuário de
  primeiro acesso manualmente (fora do app, já que cadastro de admin é Feature 6).
- **`firestore.rules`**: novas regras para `administrators` (leitura restrita a
  autenticados) coexistindo com o stub de `healthcheck` já existente.
- **Testes**: primeiro `commonTest` de `feature/admin/login` (hoje inexistente),
  cobrindo `LoginViewModel`/casos de uso com fake de `AdminSessionRepository`
  (`kotlin.test`, sem Firebase real, conforme seção 8).
- **Acessibilidade**: `LoginScreen` precisa de `contentDescription`, anúncio de
  erro (campo vazio / credencial inválida / falha de rede) e feedback tátil,
  exigindo passagem pelo skill `accessibility-audit`.
