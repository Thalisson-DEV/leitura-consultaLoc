/**
 * Script para garantir o funcionamento do menu móvel
 */
window.addEventListener('load', function() {
  // Garantir que o overlay para o menu móvel existe
  let mobileOverlay = document.querySelector('.mobile-nav-overlay');
  if (!mobileOverlay) {
    mobileOverlay = document.createElement('div');
    mobileOverlay.className = 'mobile-nav-overlay';
    document.body.appendChild(mobileOverlay);
    console.log('Overlay do menu móvel criado');
  }

  // Configurar botão de menu
  const menuButton = document.getElementById('menu-toggle');
  const sidebar = document.getElementById('sidebar');

  if (menuButton && sidebar) {
    // Garantir que o botão está visível
    menuButton.style.display = 'flex';
    menuButton.style.position = 'fixed';
    menuButton.style.bottom = '20px';
    menuButton.style.right = '20px';
    menuButton.style.zIndex = '9999';
    menuButton.style.opacity = '1';
    menuButton.style.cursor = 'pointer';
    menuButton.style.backgroundColor = '#102a83';
    menuButton.style.color = 'white';

    // Adicionar manipulador de evento direto
    menuButton.onclick = function(e) {
      e.preventDefault();
      e.stopPropagation();

      // Alternar classe no sidebar
      sidebar.classList.toggle('mobile-open');

      // Alternar overlay
      mobileOverlay.classList.toggle('active');
    };

    // Fechar menu quando clicar no overlay
    mobileOverlay.onclick = function() {
      sidebar.classList.remove('mobile-open');
      mobileOverlay.classList.remove('active');
    };
  }
});
