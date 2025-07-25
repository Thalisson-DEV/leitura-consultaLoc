/**
 * Funções para obter dados reais do banco de dados para o dashboard
 */

// Obter estatísticas de clientes para o dashboard
async function obterEstatisticasDashboard() {
  try {
    console.log('Buscando estatísticas do servidor...');
    const response = await fetch('/api/v1/clientes/estatisticas');
    if (!response.ok) {
      throw new Error('Erro ao buscar estatísticas');
    }
    const dados = await response.json();
    console.log('Estatísticas recebidas:', dados);
    return dados;
  } catch (error) {
    console.error('Erro ao carregar estatísticas:', error);
    return null;
  }
}

// Inicializar o dashboard com dados reais ou simplificados
async function inicializarDashboard() {
  // Tenta carregar dados reais primeiro
  const estatisticas = await obterEstatisticasDashboard();
  console.log('Estatísticas recebidas do backend:', estatisticas);

  // Elemento do total de clientes
  const totalClientesElement = document.getElementById('total-clientes');

  // Se temos dados estatísticos, usamos eles
  if (estatisticas && totalClientesElement && typeof estatisticas.totalClientes === 'number') {
    // Atualiza o total de clientes usando o valor retornado pelo método obterEstatisticas
    const totalClientes = estatisticas.totalClientes;
    console.log('Total de clientes:', totalClientes);

    // Inicialmente mostra o valor final para evitar problemas de exibição
    totalClientesElement.textContent = totalClientes.toLocaleString('pt-BR');

    // Adiciona um efeito de contagem
    animarContador(totalClientesElement, 0, totalClientes);
  } else {
    // Se não temos dados estatísticos
    if (totalClientesElement) {
      totalClientesElement.textContent = '—';
    }

    // Adiciona mensagem de que não há dados disponíveis
    const container = document.querySelector('.container');
    if (container && !document.querySelector('.sem-dados-msg')) {
      const mensagem = document.createElement('p');
      mensagem.className = 'sem-dados-msg';
      mensagem.textContent = 'Não há dados estatísticos disponíveis no momento.';
      mensagem.style.textAlign = 'center';
      mensagem.style.marginTop = '30px';
      mensagem.style.fontStyle = 'italic';
      container.appendChild(mensagem);
    }
  }
}

// Função para animar o contador
function animarContador(elemento, inicio, fim) {
  if (!elemento) return;

  // Duração da animação em milissegundos
  const duracao = 1500;
  const incremento = Math.ceil(fim / 100);
  let atual = inicio;

  // Função de animação
  const animar = () => {
    atual += incremento;
    if (atual > fim) atual = fim;

    elemento.textContent = atual.toLocaleString('pt-BR');

    if (atual < fim) {
      requestAnimationFrame(animar);
    }
  };

  requestAnimationFrame(animar);
}
