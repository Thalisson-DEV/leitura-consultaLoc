document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const menuToggle = document.getElementById('menu-toggle');
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;

    // Verificação de elementos críticos
    if (!sidebar) console.error('Sidebar não encontrada!');
    if (!menuToggle) console.error('Botão de menu não encontrado!');

    // Log para debug
    console.log('Inicializando sistema de menu e tema');

    // Criação do overlay para o menu móvel
    const mobileOverlay = document.createElement('div');
    mobileOverlay.className = 'mobile-nav-overlay';
    document.body.appendChild(mobileOverlay);

    // --- LÓGICA PARA RECOLHER O MENU E RESPONSIVIDADE ---
    const handleMenuToggle = () => {
        console.log('Botão de menu clicado');

        // Em dispositivos móveis, abre/fecha completamente o menu
        if (window.innerWidth <= 768) {
            if (sidebar) {
                sidebar.classList.toggle('mobile-open');
                const isOpen = sidebar.classList.contains('mobile-open');
                console.log('Menu mobile está ' + (isOpen ? 'aberto' : 'fechado'));

                // Ativar/desativar overlay
                mobileOverlay.classList.toggle('active');

                // Verificar visibilidade
                setTimeout(() => {
                    console.log('Posição do sidebar:', getComputedStyle(sidebar).left);
                }, 100);
            }
        } else {
            // Em desktops, apenas recolhe/expande o menu
            if (sidebar) sidebar.classList.toggle('collapsed');
            body.classList.toggle('nav-collapsed');
        }
    };

    // Verificar se os ícones de tema estão visíveis corretamente
    const updateThemeIcons = () => {
        const sunIcon = themeToggle.querySelector('[data-lucide="sun"]');
        const moonIcon = themeToggle.querySelector('[data-lucide="moon"]');

        if (body.classList.contains('dark-mode')) {
            sunIcon.style.display = 'none';
            moonIcon.style.display = 'block';
        } else {
            sunIcon.style.display = 'block';
            moonIcon.style.display = 'none';
        }
    };

    // Fechar o menu quando clicar no overlay (apenas mobile)
    mobileOverlay.addEventListener('click', () => {
        sidebar.classList.remove('mobile-open');
        mobileOverlay.classList.remove('active');
    });

    // Adicionar evento de clique ao botão de menu
    if (menuToggle) {
        console.log('Configurando evento de clique para o botão de menu');
        menuToggle.addEventListener('click', function(e) {
            e.preventDefault(); // Prevenir comportamento padrão
            e.stopPropagation(); // Evitar propagação
            handleMenuToggle(); // Chamar a função de toggle
            console.log('Evento de clique do menu acionado');
        });

        // Garantir que o botão seja visível
        menuToggle.style.display = 'flex';
    }

    // Fechar o menu quando um link for clicado (em dispositivos móveis)
    const navLinks = document.querySelectorAll('.sidebar-nav a');
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 768) {
                sidebar.classList.remove('mobile-open');
                mobileOverlay.classList.remove('active');
            }
        });
    });

    // --- LÓGICA PARA O TEMA CLARO/ESCURO ---
    const applyTheme = (theme) => {
        if (theme === 'dark') {
            body.classList.add('dark-mode');
            document.documentElement.setAttribute('data-theme', 'dark');
            console.log('Tema escuro aplicado');
        } else {
            body.classList.remove('dark-mode');
            document.documentElement.setAttribute('data-theme', 'light');
            console.log('Tema claro aplicado');
        }
    };

    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const isDarkMode = body.classList.contains('dark-mode');
            const newTheme = isDarkMode ? 'light' : 'dark';

            console.log('Alterando tema para:', newTheme);
            localStorage.setItem('theme', newTheme);

            // Aplicar tema
            applyTheme(newTheme);
            updateThemeIcons();

            // Forçar redraw para garantir que as cores CSS sejam atualizadas
            document.body.style.display = 'none';
            document.body.offsetHeight; // Forçar reflow
            document.body.style.display = '';
        });
    }

    // Aplica o tema salvo ao carregar a página
    const savedTheme = localStorage.getItem('theme') || 'light';
    applyTheme(savedTheme);
    updateThemeIcons(); // Atualizar ícones na inicialização


    // --- LÓGICA PARA MARCAR O LINK ATIVO NO MENU ---
    const currentPath = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.sidebar-nav a');

    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === currentPath || (currentPath === '' && linkPath === 'index.html')) {
            link.classList.add('active');
        }
    });

    // Inicializa os ícones da biblioteca Lucide
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();

        // Verificar se algum ícone não foi renderizado corretamente
        setTimeout(() => {
            const icones = document.querySelectorAll('[data-lucide]');
            let todosCarregados = true;

            icones.forEach(icone => {
                if (!icone.querySelector('svg')) {
                    todosCarregados = false;
                }
            });

            // Se algum ícone não carregou, tentar novamente
            if (!todosCarregados) {
                console.log('Tentando recarregar ícones...');
                lucide.createIcons();
            }
        }, 500);
    }
});

// --- FUNÇÃO GLOBAL DE NOTIFICAÇÃO ---
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification-toast ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 4000); // A notificação some após 4 segundos
}