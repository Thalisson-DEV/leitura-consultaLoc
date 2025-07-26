// --- FUNÇÃO GLOBAL DE NOTIFICAÇÃO ---
// Deixamos esta função fora do 'DOMContentLoaded' para que ela fique disponível
// globalmente para os scripts de cada página assim que o arquivo for carregado.
function showNotification(message, type = 'info') {
    // Remove qualquer notificação antiga para não empilhar
    const existingToast = document.querySelector('.notification-toast');
    if (existingToast) {
        existingToast.remove();
    }

    const notification = document.createElement('div');
    notification.className = `notification-toast ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    // A notificação some após 4 segundos
    setTimeout(() => {
        notification.remove();
    }, 4000);
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
// Espera todo o HTML ser carregado antes de executar
document.addEventListener('DOMContentLoaded', () => {

    const sidebar = document.getElementById('sidebar');
    const menuToggle = document.getElementById('menu-toggle');
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;

    // --- LÓGICA PARA O TEMA CLARO/ESCURO ---
    if (themeToggle) {
        const sunIcon = themeToggle.querySelector('[data-lucide="sun"]');
        const moonIcon = themeToggle.querySelector('[data-lucide="moon"]');

        const applyTheme = (theme) => {
            if (theme === 'dark') {
                body.classList.add('dark-mode');
                if (sunIcon) sunIcon.style.display = 'none';
                if (moonIcon) moonIcon.style.display = 'block';
            } else {
                body.classList.remove('dark-mode');
                if (sunIcon) sunIcon.style.display = 'block';
                if (moonIcon) moonIcon.style.display = 'none';
            }
        };

        themeToggle.addEventListener('click', () => {
            const newTheme = body.classList.contains('dark-mode') ? 'light' : 'dark';
            localStorage.setItem('theme', newTheme);
            applyTheme(newTheme);
        });

        // Aplica o tema salvo ao carregar a página
        const savedTheme = localStorage.getItem('theme') || 'light';
        applyTheme(savedTheme);
    }


    // --- LÓGICA PARA RECOLHER/EXPANDIR O MENU ---
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            body.classList.toggle('nav-collapsed');
        });
    }


    // --- LÓGICA PARA MARCAR O LINK ATIVO NO MENU ---
    const currentPath = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.sidebar-nav a');

    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === currentPath || (currentPath === '' && linkPath === 'index.html')) {
            link.classList.add('active');
        }
    });


    // --- INICIALIZA OS ÍCONES LUCIDE ---
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});
