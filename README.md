<div align="center">
<h1>GeoRoute-C API</h1>
<p>
<strong>Uma API de geolocalização e roteamento rápida, desenvolvida em Spring Boot, projetada para otimizar o acesso às coordenadas de clientes e o uso de recursos de banco de dados (Neon DB).</strong>
</p>
<p>
<img src="https://img.shields.io/badge/Status-Estável-green" alt="Status do Projeto: Estável">
<img src="https://img.shields.io/badge/Java-17%2B-blue?logo=java&logoColor=white" alt="Java 17+">
<img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=spring-boot" alt="Spring Boot 3.x">
<img src="https://img.shields.io/badge/Banco%20de%20Dados-PostgreSQL%20(Neon)-blue?logo=postgresql" alt="PostgreSQL">
<img src="https://img.shields.io/badge/Otimização-Alto%20Desempenho-red" alt="Otimização de Querys">
</p>
</div>

---

## 📋 Índice
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Endpoint de Geolocalização](#-endpoint-de-geolocalização)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Otimização e Performance (Diferencial Técnico)](#-otimização-e-performance-diferencial-técnico)
- [Como Executar o Projeto](#-como-executar-o-projeto)
- [Contribuições](#-contribuições)
- [Contato](#-contato)

---

## 📚 Sobre o Projeto
A **GeoRoute-C API** foi desenvolvida para solucionar o problema de acesso rápido à localização de clientes (instalações) usando diversos identificadores (conta contrato, número de série, etc.).

Seu foco principal é a **eficiência e a economia de recursos**. A API é ideal para aplicações que precisam de uma resposta de redirecionamento imediata para serviços de mapas (como o Google Maps) para otimizar rotas e tempo de resposta de equipes de campo.

## ✨ Funcionalidades Principais
✅ **Redirecionamento Instantâneo:** Busca a latitude e longitude do cliente e retorna um `HTTP 302 Found` (Redirecionamento) para o Google Maps.

⚡ **Buscas por Múltiplos Identificadores:** Suporta buscas por ID da Instalação, Conta Contrato, Número de Série, Número do Poste e Nome do Cliente.

## 🗺️ Endpoint de Geolocalização
Todos os endpoints de busca e redirecionamento são prefixados com `/api/v1/clientes` e utilizam o status `302 Found` para indicar o redirecionamento bem-sucedido.

| Método | URL | Parâmetro | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/instalacao/{instalacao}/redirecionar-maps` | `Long instalacao` | Busca pela chave primária (ID da Instalação). Mais rápido. |
| `GET` | `/conta-contrato/{contaContrato}/redirecionar-maps` | `String contaContrato` | Busca exata e case-insensitive pela Conta Contrato. |
| `GET` | `/numero-serie/{numeroSerie}/redirecionar-maps` | `String numeroSerie` | Busca exata e case-insensitive pelo Número de Série. |
| `GET` | `/numero-poste/{numeroPoste}/redirecionar-maps` | `String numeroPoste` | Busca exata pelo Número do Poste (Primeiro resultado). |
| `GET` | `/nome/{nomeCliente}/redirecionar-maps` | `String nomeCliente` | Busca por prefixo (`StartingWith`) e case-insensitive pelo Nome do Cliente. |

---

## 🛠️ Tecnologias Utilizadas
A API foi construída com foco em robustez, velocidade e baixo custo operacional.

* **Linguagem:** **Java 17+**
* **Framework:** **Spring Boot 3.x**
* **Persistência:** Spring Data JPA / Hibernate
* **Banco de Dados:** **PostgreSQL** (Hospedado no **Neon DB** no plano Free)
* **Controle de Versão DB:** **Flyway** (para gerenciar e aplicar otimizações de índices)
* **Build Tool:** Maven

---

## 🚀 Otimização e Performance (Diferencial Técnico)
O design desta API foi focado na **redução do Compute Usage** do Neon Database, garantindo que o custo operacional se mantenha baixo e o desempenho alto.

1.  **Cache de Aplicação (`@Cacheable`):** Implementado em todos os métodos de busca. Requisições repetidas não chegam ao banco de dados, utilizando apenas a memória da JVM.
2.  **Projeções JPA (`CoordenadasClientes`):** As consultas ao banco de dados retornam **apenas** as colunas `latitude` e `longitude`. Isso reduz significativamente o I/O do disco e a transferência de dados.
3.  **Índices Funcionais com `LOWER()`:** Utilização de índices funcionais via Flyway (`idx_..._lower`) nas colunas `conta_contrato`, `numero_serie` e `nome_cliente`. Isso torna as buscas *case-insensitive* (`...IgnoreCase`) **extremamente rápidas** sem recorrer a *Full Table Scans* (a principal causa de alto Compute Usage).
4.  **Buscas Otimizadas:** A lógica de busca foi alterada de **lenta e custosa** `LIKE '%param%'` para buscas **exatas** (`=`) e buscas por **prefixo** (`LIKE 'param%'`), que usam eficientemente os índices criados.

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
* **Java JDK 17** ou superior
* **Maven 3.x**
* Uma conta no **Neon DB** (PostgreSQL)
* O banco de dados configurado na variável de ambiente ou no arquivo `application.properties`.

### Configuração e Execução
1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/Thalisson-DEV/leitura-consultaLoc
    cd GeoRoute-C-API
    ```

2.  **Configure o Banco de Dados:**
    Adicione suas credenciais do Neon Database ao arquivo `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://[SEU_ENDPOINT_NEON].ap-southeast-1.aws.neon.tech/nome-do-db
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    # Configurações do Flyway
    spring.flyway.enabled=true
    ```

3.  **Execute a Aplicação:**
    O Flyway aplicará automaticamente a migração de otimização de índices (V2) na inicialização.
    ```bash
    mvn spring-boot:run
    ```

A API estará rodando em `http://localhost:8080`.

---

## 🤝 Contribuições
Este projeto aceita contribuições, sugestões e feedbacks que visem a melhoria da performance, da segurança ou da manutenção do código. Sinta-se à vontade para:

* Abrir uma **Issue** para relatar bugs ou sugerir novas funcionalidades.
* Abrir um **Pull Request** com suas alterações.

## 👤 Contato
Se você tiver dúvidas, sugestões ou quiser discutir o projeto:

* **GitHub:** ThalissonDEV - https://github.com/Thalisson-DEV
* **LinkedIn:** www.linkedin.com/in/thalisson-damião-108a1732b
* **Email:** thalissondamiao1@gmail.com