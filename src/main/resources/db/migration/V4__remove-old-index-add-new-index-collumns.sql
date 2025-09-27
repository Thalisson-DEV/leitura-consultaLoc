-- V2__Otimizacao_Indices.sql
--
-- Otimiza os índices de busca para suportar consultas case-insensitive (ignorando caixa)
-- de forma eficiente, reduzindo o custo de compute usage no Neon Database.

--------------------------------------------------------------------------------
-- 1. Remoção de Índices Antigos (Se existirem)
-- O Flyway não permite CREATE OR REPLACE INDEX, então usamos DROP IF EXISTS.
--------------------------------------------------------------------------------

-- Remove o índice simples na conta_contrato (não utilizado por buscas 'IgnoreCase')
DROP INDEX IF EXISTS idx_clientes_conta_contrato;

-- Remove o índice simples no numero_serie (não utilizado por buscas 'IgnoreCase')
DROP INDEX IF EXISTS idx_clientes_numero_serie;


--------------------------------------------------------------------------------
-- 2. Criação de Índices Funcionais Otimizados
-- Esses índices usam a função LOWER() para corresponder exatamente às consultas
-- geradas pelo Spring Data JPA (.findBy...IgnoreCase), garantindo o uso do índice.
--------------------------------------------------------------------------------

-- Cria um índice funcional para 'conta_contrato' (Método: findByContaContratoIgnoreCase)
CREATE INDEX IF NOT EXISTS idx_clientes_conta_contrato_lower
    ON clientes (LOWER(conta_contrato));

-- Cria um índice funcional para 'numero_serie' (Método: findByNumeroSerieIgnoreCase)
CREATE INDEX IF NOT EXISTS idx_clientes_numero_serie_lower
    ON clientes (LOWER(numero_serie));


--------------------------------------------------------------------------------
-- 3. Confirmação (Índices que já estavam corretos ou mantidos)
--------------------------------------------------------------------------------

-- O índice para 'numero_poste' (busca case-sensitive) está correto. Mantido.
CREATE INDEX IF NOT EXISTS idx_clientes_numero_poste
    ON clientes (numero_poste);

-- O índice para 'nome_cliente' (busca 'StartingWithIgnoreCase') já estava correto. Mantido.
CREATE INDEX IF NOT EXISTS idx_clientes_nome_cliente_lower
    ON clientes (LOWER(nome_cliente));