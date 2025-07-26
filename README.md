# Sistema de Busca Sipel - Documentação de Interface

## Visão Geral

Esta documentação descreve a interface do usuário do Sistema de Busca Sipel, que foi refatorada para oferecer responsividade completa e uma experiência moderna em todos os dispositivos.

## Novos Recursos

### Dashboard Responsivo
- A página inicial foi transformada em um dashboard dinâmico
- Exibe KPIs importantes como total de clientes, buscas recentes e postes mapeados
- Inclui um gráfico de buscas por tipo para análise visual dos dados
- Layout responsivo que se adapta a smartphones, tablets e desktops

### Menu de Navegação Aprimorado
- Menu hambúrguer em dispositivos móveis
- Navegação lateral recolhível em telas maiores
- Suporte a tema claro/escuro em todos os dispositivos
- Ícones visuais para cada opção de menu

### Layout Responsivo
- Interfaces adaptáveis a qualquer tamanho de tela
- Design mobile-first para garantir compatibilidade universal
- Componentes otimizados para toque em dispositivos móveis

## Tecnologias Utilizadas

- HTML5 e CSS3 para estrutura e estilo
- JavaScript para interatividade
- Chart.js para visualização de dados
- Lucide para ícones vetoriais
- Media queries para responsividade

## Instruções de Manutenção

Para adicionar novas páginas ao sistema, siga estas diretrizes:

1. Inclua os arquivos CSS necessários:
   ```html
   <link rel="stylesheet" href="assets/style.css">
   <link rel="stylesheet" href="assets/responsive.css">
   ```

2. Use a estrutura de navegação padrão:
   ```html
   <nav class="sidebar" id="sidebar">...</nav>
   <div class="page-wrapper">...</div>
   ```

3. Inclua os scripts necessários:
   ```html
   <script src="https://unpkg.com/lucide@latest"></script>
   <script src="assets/layout.js"></script>
   ```

4. Mantenha a consistência com o design existente usando as classes CSS e elementos visuais padronizados.
