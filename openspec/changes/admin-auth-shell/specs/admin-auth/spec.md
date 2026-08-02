## ADDED Requirements

### Requirement: Autenticação de administrador por e-mail e senha
O sistema DEVE autenticar administradores por e-mail e senha via Firebase Auth,
concedendo uma sessão administrativa autenticada somente quando as credenciais
forem válidas.

#### Scenario: Login com credenciais válidas
- **WHEN** um administrador envia um e-mail cadastrado e a senha correta
- **THEN** o sistema concede uma sessão administrativa autenticada e libera
  acesso ao painel admin

#### Scenario: Login com credenciais inválidas
- **WHEN** um administrador envia uma combinação de e-mail/senha rejeitada pelo
  Firebase Auth
- **THEN** o sistema nega o acesso, exibe uma mensagem de erro clara e mantém o
  administrador na tela de login

### Requirement: Validação de campos obrigatórios
O sistema DEVE validar que e-mail e senha estejam preenchidos antes de tentar
autenticar, sem realizar chamada de rede quando algum campo estiver vazio.

#### Scenario: Campo obrigatório vazio
- **WHEN** o administrador envia o formulário de login com e-mail ou senha em
  branco
- **THEN** o sistema exibe uma mensagem de validação e impede o envio sem
  contatar o Firebase Auth

### Requirement: Falha de rede durante autenticação
O sistema DEVE exigir conectividade de rede para autenticar e DEVE comunicar
falhas de conectividade de forma distinta de falhas de credencial inválida.

#### Scenario: Sem conexão ao tentar login
- **WHEN** o administrador envia credenciais válidas enquanto o dispositivo
  está sem conectividade de rede
- **THEN** o sistema exibe uma mensagem de falha de rede distinta da mensagem
  de credenciais inválidas e mantém o administrador na tela de login

### Requirement: Encerramento de sessão (logout)
O sistema DEVE permitir que um administrador autenticado encerre sua sessão a
qualquer momento.

#### Scenario: Logout a partir da área admin
- **WHEN** um administrador autenticado aciona o logout
- **THEN** o sistema encerra a sessão no Firebase Auth e o administrador deixa
  de ter acesso a rotas admin até autenticar novamente

### Requirement: Observabilidade reativa do estado de sessão
O sistema DEVE expor o estado atual da sessão administrativa como um estado
observável com três valores explícitos — carregando (ainda não se sabe),
autenticado e não autenticado — nunca como uma sessão nula sem distinção de
carregamento.

#### Scenario: Estado inicial antes do Firebase Auth restaurar a sessão
- **WHEN** o app inicia e o Firebase Auth ainda não informou se existe uma
  sessão persistida
- **THEN** o estado de sessão exposto é "carregando" e consumidores não devem
  tratá-lo como não autenticado

#### Scenario: Sessão restaurada automaticamente
- **WHEN** o Firebase Auth reporta uma sessão previamente persistida e válida
  logo na inicialização do app
- **THEN** o estado de sessão exposto transiciona para autenticado sem exigir
  que o administrador faça login novamente

### Requirement: Usuário de primeiro acesso pré-provisionado
O sistema DEVE autenticar o usuário administrador de primeiro acesso
pré-provisionado (criado fora do app, ex.: console Firebase) da mesma forma que
qualquer outro administrador, sem exigir um fluxo de cadastro dentro do app.

#### Scenario: Primeiro login com o usuário pré-provisionado
- **WHEN** o usuário de primeiro acesso, já criado fora do app, envia suas
  credenciais pela primeira vez
- **THEN** o sistema autentica normalmente, sem apresentar nenhuma etapa de
  onboarding ou cadastro adicional
