## ADDED Requirements

### Requirement: Casca de navegação administrativa
O sistema DEVE fornecer um ponto de entrada único de navegação admin
(`AdminHome`) para onde administradores autenticados são levados após o login,
servindo de base para as futuras features administrativas.

#### Scenario: Login bem-sucedido leva à casca admin
- **WHEN** um administrador se autentica com sucesso
- **THEN** o sistema navega para a casca de navegação admin e remove a tela de
  login do back stack, de modo que o botão de voltar não retorna ao login

### Requirement: Guarda de rota administrativa
O sistema DEVE bloquear o acesso a qualquer rota admin quando não houver uma
sessão autenticada válida, redirecionando para a tela de login em vez de
renderizar o conteúdo admin.

#### Scenario: Acesso a rota admin sem sessão válida
- **WHEN** um usuário sem sessão válida tenta acessar qualquer rota admin
- **THEN** o sistema redireciona para a tela de login e não renderiza o
  conteúdo da rota admin

#### Scenario: Sessão expira ou é revogada durante navegação em rota admin
- **WHEN** o estado de sessão transiciona para não autenticado enquanto o
  administrador já está em uma rota admin
- **THEN** o sistema redireciona imediatamente para a tela de login, removendo
  a rota admin do back stack

### Requirement: Logout retorna ao login sem acesso residual
O sistema DEVE, ao efetuar logout, limpar o back stack de navegação admin para
que o administrador não consiga voltar a rotas admin usando o botão de voltar.

#### Scenario: Botão de voltar após logout
- **WHEN** um administrador faz logout e em seguida aciona o botão de voltar
- **THEN** o sistema não exibe nenhuma rota admin visitada anteriormente,
  permanecendo ou retornando à tela de login
