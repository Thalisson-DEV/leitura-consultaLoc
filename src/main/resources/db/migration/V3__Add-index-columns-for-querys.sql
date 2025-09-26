-- Cria um índice na coluna 'conta_contrato' para acelerar as buscas por este campo.
-- Corresponde ao método: buscarPorContaContrato()
CREATE INDEX IF NOT EXISTS idx_clientes_conta_contrato ON clientes(conta_contrato);

-- Cria um índice na coluna 'numero_serie'
-- Corresponde ao método: buscarPorNumeroSerie()
CREATE INDEX IF NOT EXISTS idx_clientes_numero_serie ON clientes(numero_serie);

-- Cria um índice na coluna 'numero_poste'
-- Corresponde ao método: buscarPorNumeroPoste()
CREATE INDEX IF NOT EXISTS idx_clientes_numero_poste ON clientes(numero_poste);

-- Cria um índice na coluna 'nome_cliente'.
-- Usamos 'LOWER' para otimizar buscas case-insensitive, que são comuns para nomes.
-- Corresponde ao método: buscarPorNomeCliente()
CREATE INDEX IF NOT EXISTS idx_clientes_nome_cliente_lower ON clientes (LOWER(nome_cliente));

