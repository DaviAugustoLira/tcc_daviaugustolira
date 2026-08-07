## ADDED Requirements

### Requirement: Carousel vertical de maps com miniatura
O sistema DEVE listar, na casca administrativa, todos os maps cadastrados em um carousel
vertical rolável, exibindo para cada map o nome e uma miniatura da imagem correspondente.
Um map cujo documento não tiver uma URL de imagem válida NÃO DEVE aparecer na listagem (mesmo
tratamento hoje aplicado a um map sem `name` válido).

#### Scenario: Maps cadastrados aparecem no carousel com miniatura
- **WHEN** existem maps cadastrados com nome e imagem válidos
- **THEN** o sistema exibe cada um como um item do carousel vertical, mostrando nome e
  miniatura da imagem

#### Scenario: Map sem imagem válida é omitido da listagem
- **WHEN** um documento de map não tem uma URL de imagem válida (ausente ou vazia)
- **THEN** o sistema não exibe esse map no carousel, sem interromper a exibição dos demais

#### Scenario: Nenhum map cadastrado
- **WHEN** não há nenhum map cadastrado
- **THEN** o sistema exibe uma mensagem de estado vazio no lugar do carousel

#### Scenario: Erro ao carregar a lista de maps
- **WHEN** a leitura dos maps falha (ex.: sem conexão)
- **THEN** o sistema exibe uma mensagem de erro no lugar do carousel, sem quebrar a tela

### Requirement: Visualização de map em tela cheia com rolagem
O sistema DEVE permitir abrir a imagem de um map do carousel em uma tela dedicada, exibida
em escala ocupando toda a tela, na qual o usuário pode rolar (scroll vertical e horizontal)
pela imagem quando ela for maior que a área visível. Esta tela é uma rota administrativa e
está sujeita à mesma guarda de sessão das demais rotas admin.

#### Scenario: Tocar em um item do carousel abre a imagem em tela cheia
- **WHEN** o administrador toca em um item do carousel de maps
- **THEN** o sistema navega para uma tela cheia exibindo a imagem daquele map em escala
  ocupando toda a tela

#### Scenario: Imagem maior que a viewport permite rolagem
- **WHEN** a imagem exibida em tela cheia é maior que a área visível em alguma direção
- **THEN** o sistema permite rolar (scroll) nessa direção para revelar o restante da imagem

#### Scenario: Acesso à tela de visualização sem sessão válida
- **WHEN** um usuário sem sessão administrativa válida tenta acessar a tela de visualização
  de um map
- **THEN** o sistema redireciona para a tela de login e não renderiza a imagem
