## ADDED Requirements

### Requirement: Ampliação da imagem do map para cadastro de precisão
O sistema DEVE exibir a imagem do map, na tela cheia do visualizador, ajustada à largura
disponível como escala padrão (em vez do tamanho intrínseco da imagem), e DEVE permitir ampliar
(zoom por pinça) e navegar (arraste) por cima dessa escala padrão, para que o administrador
consiga apontar um local com precisão de pixel.

#### Scenario: Imagem exibida maior que o tamanho intrínseco por padrão
- **WHEN** o administrador abre a tela cheia de um map
- **THEN** o sistema exibe a imagem ajustada à largura da tela, não no seu tamanho intrínseco

#### Scenario: Ampliar a imagem por pinça
- **WHEN** o administrador realiza o gesto de pinça sobre a imagem
- **THEN** o sistema amplia a imagem dentro de um limite máximo definido, mantendo a posição
  apontada pelo gesto visível

#### Scenario: Navegar pela imagem ampliada por arraste
- **WHEN** a imagem está ampliada além do tamanho da tela e o administrador arrasta o dedo sobre
  ela
- **THEN** o sistema desloca a área visível da imagem de acordo com o arraste, sem perder o nível
  de zoom atual

### Requirement: Modo de cadastro de destino
O sistema DEVE fornecer, na tela cheia do map, um controle para ativar e desativar um modo de
cadastro de destino. Toques sobre a imagem só iniciam o registro de um novo destino quando esse
modo está ativo.

#### Scenario: Ativar o modo de cadastro
- **WHEN** o administrador aciona o controle de cadastro de destino
- **THEN** o sistema entra em modo de cadastro, habilitando a captura de toque sobre a imagem
  para registrar um novo destino

#### Scenario: Toque fora do modo de cadastro não inicia registro
- **WHEN** o administrador toca na imagem do map sem o modo de cadastro ativo
- **THEN** o sistema não abre o formulário de novo destino

#### Scenario: Permanecer em modo de cadastro após confirmar um destino
- **WHEN** o administrador confirma o cadastro de um destino enquanto o modo de cadastro está
  ativo
- **THEN** o sistema mantém o modo de cadastro ativo, permitindo tocar novamente para registrar
  outro destino

#### Scenario: Encerrar o modo de cadastro
- **WHEN** o administrador aciona novamente o controle de cadastro de destino estando o modo já
  ativo
- **THEN** o sistema desativa o modo de cadastro e nenhum toque adicional na imagem inicia um
  novo registro

### Requirement: Captura da coordenada de pixel do destino
Ao tocar na imagem do map durante o modo de cadastro, o sistema DEVE converter a posição do toque
para a coordenada de pixel correspondente na imagem original do map, independentemente do nível
de zoom ou da posição de rolagem/arraste no momento do toque, e DEVE apresentar um destino
pendente de confirmação nessa coordenada.

#### Scenario: Toque em qualquer nível de zoom resulta na mesma coordenada de pixel
- **WHEN** o administrador toca o mesmo local visual do destino em dois níveis de zoom diferentes
- **THEN** o sistema calcula a mesma coordenada de pixel da imagem original nos dois casos

#### Scenario: Marcador temporário no local tocado
- **WHEN** o administrador toca a imagem durante o modo de cadastro
- **THEN** o sistema exibe um marcador temporário no local tocado até o cadastro ser confirmado
  ou cancelado

### Requirement: Entrada manual de coordenada
O sistema DEVE oferecer uma forma de informar a coordenada de pixel do destino digitando os
valores de X e Y diretamente, como alternativa ao toque sobre a imagem, alimentando o mesmo fluxo
de confirmação de nome e descrição.

#### Scenario: Registrar destino por coordenada digitada
- **WHEN** o administrador digita valores de X e Y válidos e confirma a entrada manual
- **THEN** o sistema trata essa coordenada como um destino pendente, abrindo o mesmo formulário de
  nome e descrição usado para o toque na imagem

#### Scenario: Coordenada manual inválida
- **WHEN** o administrador tenta confirmar uma entrada manual de coordenada vazia ou não numérica
- **THEN** o sistema não cria um destino pendente e mantém os campos disponíveis para correção

### Requirement: Cadastro de nome e descrição do destino
Após um destino pendente ser definido (por toque ou coordenada manual), o sistema DEVE exigir nome
e descrição antes de permitir a confirmação do cadastro, e DEVE permitir cancelar o destino
pendente sem persistir nada.

#### Scenario: Confirmar cadastro com nome e descrição preenchidos
- **WHEN** o administrador preenche nome e descrição do destino pendente e confirma
- **THEN** o sistema persiste o destino e remove o marcador temporário, substituindo-o por um
  marcador definitivo

#### Scenario: Tentar confirmar sem nome
- **WHEN** o administrador tenta confirmar o cadastro do destino pendente sem informar um nome
- **THEN** o sistema não persiste o destino e exibe um erro indicando que o nome é obrigatório

#### Scenario: Cancelar o destino pendente
- **WHEN** o administrador cancela o destino pendente antes de confirmar
- **THEN** o sistema descarta a coordenada capturada, remove o marcador temporário e nada é
  persistido

### Requirement: Persistência do destino cadastrado
O sistema DEVE persistir cada destino confirmado como um documento na coleção `points` do
Firestore, associado ao `mapId` do map atualmente exibido, com a coordenada de pixel, nome,
descrição e identificação de quem cadastrou. Falhas de persistência (rede indisponível, sessão
expirada) DEVEM ser reportadas ao administrador sem derrubar a tela.

#### Scenario: Destino gravado com sucesso
- **WHEN** o administrador confirma um destino válido e a gravação no Firestore é bem-sucedida
- **THEN** o sistema associa o documento criado ao `mapId` do map aberto e ele passa a aparecer na
  lista de destinos já cadastrados

#### Scenario: Falha de rede ao gravar o destino
- **WHEN** o administrador confirma um destino válido sem conexão de rede disponível
- **THEN** o sistema exibe uma mensagem de erro de conectividade e mantém o destino pendente para
  nova tentativa, sem persistir um documento parcial

### Requirement: Exibição dos destinos já cadastrados
O sistema DEVE exibir, sobre a imagem do map, um marcador para cada destino já cadastrado
associado ao `mapId` exibido, independentemente do modo de cadastro estar ativo ou não.

#### Scenario: Destinos existentes aparecem ao abrir o map
- **WHEN** o administrador abre a tela cheia de um map que já tem destinos cadastrados
- **THEN** o sistema exibe um marcador na posição de cada destino existente

#### Scenario: Marcadores continuam visíveis fora do modo de cadastro
- **WHEN** o modo de cadastro de destino está desativado
- **THEN** os marcadores dos destinos já cadastrados continuam visíveis sobre a imagem

### Requirement: Controle de acesso à coleção de destinos
O sistema DEVE restringir leitura da coleção `points` a administradores autenticados e permitir
criação de documentos apenas quando o campo `createdBy` corresponder ao usuário autenticado e os
campos obrigatórios (`mapId`, `name`, `x`, `y`) forem válidos. Atualização e exclusão de destinos
NÃO DEVEM ser permitidas nesta versão.

#### Scenario: Leitura sem autenticação é negada
- **WHEN** um cliente sem sessão administrativa válida tenta ler a coleção `points`
- **THEN** o Firestore nega o acesso

#### Scenario: Criação com dados inválidos é negada
- **WHEN** uma tentativa de criação de documento em `points` omite `name` ou envia `createdBy`
  diferente do usuário autenticado
- **THEN** o Firestore rejeita a escrita
