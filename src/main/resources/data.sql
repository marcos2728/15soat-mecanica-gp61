INSERT INTO usuario (login, senha, role)
SELECT 'gerente', '$2b$12$giBVF31L7HnbK0cCG/Qwt.yGEk/bGoe4Vqi11uHhXZdJBgFDC7nDW', 'GERENTE'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'gerente');

INSERT INTO usuario (login, senha, role)
SELECT 'mecanico', '$2b$12$Hx7jD8s01LhysxwJQA.PBORSsOrkj8M5OyXoc9Tly5rs8aRR8DvsK', 'MECANICO'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'mecanico');

-- =========================================================
-- Massa de dados inicial - Sistema Mecânica
-- 5 clientes (+ veículos), 10 peças (estoque), 10 serviços
-- Idempotente: seguro rodar em todo boot da aplicação (ON CONFLICT DO NOTHING)
-- =========================================================

-- ---------------------------------------------------------
-- VEÍCULOS (inseridos antes dos clientes por causa da FK)
-- ---------------------------------------------------------
INSERT INTO veiculos (ano_fabricacao, marca, modelo, placa) VALUES
(2021, 'Chevrolet', 'Onix', 'ABC1D23'),
(2019, 'Toyota', 'Corolla', 'XYZ9K88'),
(2020, 'Volkswagen', 'Gol', 'DEF4E56'),
(2022, 'Fiat', 'Argo', 'GHI7F89'),
(2018, 'Honda', 'Civic', 'JKL2G34')
ON CONFLICT (placa) DO NOTHING;

-- ---------------------------------------------------------
-- CLIENTES (vinculados 1:1 aos veículos acima)
-- ---------------------------------------------------------
INSERT INTO clientes (cpf_cnpj, data_de_nascimento, email, nome, telefone) VALUES
('7338170002', '1990-05-14', 'joao.silva@email.com', 'João da Silva', '11987654321'),
('17619551018', '1985-11-02', 'maria.oliveira@email.com', 'Maria Oliveira', '11976543210'),
('58027665060', '1978-03-22', 'carlos.souza@email.com', 'Carlos Souza', '11965432109'),
('32670884024', '1995-07-30', 'ana.pereira@email.com', 'Ana Pereira', '11954321098'),
('18068756086', '2000-01-18', 'lucas.costa@email.com', 'Lucas Costa', '11943210987')
ON CONFLICT (cpf_cnpj) DO NOTHING;

-- Associa cada veículo ao respectivo cliente via FK cliente_id
UPDATE veiculos SET cliente_id = (SELECT id FROM clientes WHERE cpf_cnpj = '7338170002') WHERE placa = 'ABC1D23' AND cliente_id IS NULL;
UPDATE veiculos SET cliente_id = (SELECT id FROM clientes WHERE cpf_cnpj = '17619551018') WHERE placa = 'XYZ9K88' AND cliente_id IS NULL;
UPDATE veiculos SET cliente_id = (SELECT id FROM clientes WHERE cpf_cnpj = '58027665060') WHERE placa = 'DEF4E56' AND cliente_id IS NULL;
UPDATE veiculos SET cliente_id = (SELECT id FROM clientes WHERE cpf_cnpj = '32670884024') WHERE placa = 'GHI7F89' AND cliente_id IS NULL;
UPDATE veiculos SET cliente_id = (SELECT id FROM clientes WHERE cpf_cnpj = '18068756086') WHERE placa = 'JKL2G34' AND cliente_id IS NULL;


-- ---------------------------------------------------------
-- ESTOQUE (10 peças)
-- Sem constraint UNIQUE em nome_item, então protegemos via NOT EXISTS
-- ---------------------------------------------------------
INSERT INTO estoque (nome_item, quantidade_item, valor_item)
SELECT * FROM (VALUES
    ('Filtro de óleo', 40, 35.90),
    ('Filtro de ar', 30, 42.50),
    ('Pastilha de freio (par)', 25, 89.90),
    ('Disco de freio', 15, 210.00),
    ('Correia dentada', 20, 145.00),
    ('Amortecedor dianteiro', 10, 320.00),
    ('Vela de ignição', 60, 22.90),
    ('Bateria automotiva 60Ah', 12, 450.00),
    ('Óleo de motor 5W30 (litro)', 100, 48.90),
    ('Bomba de combustível', 8, 380.00)
) AS novos(nome_item, quantidade_item, valor_item)
WHERE NOT EXISTS (SELECT 1 FROM estoque WHERE estoque.nome_item = novos.nome_item);

-- ---------------------------------------------------------
-- SERVIÇOS (10 serviços)
-- Sem constraint UNIQUE em nome_servico, então protegemos via NOT EXISTS
-- ---------------------------------------------------------
INSERT INTO servicos (nome_servico, valor_servico)
SELECT * FROM (VALUES
    ('Troca de óleo e filtro', 150.00),
    ('Alinhamento e balanceamento', 120.00),
    ('Troca de pastilhas de freio', 180.00),
    ('Troca de disco de freio', 250.00),
    ('Troca de correia dentada', 320.00),
    ('Suspensão - troca de amortecedores', 400.00),
    ('Revisão elétrica completa', 220.00),
    ('Troca de bateria', 60.00),
    ('Diagnóstico eletrônico', 100.00),
    ('Higienização de ar-condicionado', 90.00)
) AS novos(nome_servico, valor_servico)
WHERE NOT EXISTS (SELECT 1 FROM servicos WHERE servicos.nome_servico = novos.nome_servico);

-- =========================================================
-- Massa de dados - Ordens de Serviço
-- Sem blocos DO/PLpgSQL (o parser de script do Spring não suporta $$),
-- usando INSERT ... WITH ... RETURNING encadeados.
-- Idempotente via tabela temporária "seed_flag": só semeia se
-- ordem_servicos estiver vazia no momento do boot.
-- =========================================================

CREATE TEMP TABLE IF NOT EXISTS seed_flag AS
SELECT (SELECT COUNT(*) FROM ordem_servicos) = 0 AS should_seed;

-- =====================================================================
-- OS 1 - João da Silva (ABC1D23): fluxo completo, 1 dia em cada fase
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '10 days', now() - interval '1 days', 'ENTREGUE', 185.90, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'ABC1D23' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',                       now() - interval '10 days', now() - interval '9 days'),
        ('EM_DIAGNOSTICO',               now() - interval '9 days',  now() - interval '8 days'),
        ('DIAGNOSTICO_CONCLUIDO',        now() - interval '8 days',  now() - interval '7 days'),
        ('PENDENTE_NOTIFICACAO_CLIENTE', now() - interval '7 days',  now() - interval '6 days'),
        ('PENDENTE_APROVACAO_CLIENTE',   now() - interval '6 days',  now() - interval '5 days'),
        ('APROVADA',                     now() - interval '5 days',  now() - interval '4 days'),
        ('EM_EXECUCAO',                  now() - interval '4 days',  now() - interval '3 days'),
        ('FINALIZADA',                   now() - interval '3 days',  now() - interval '2 days'),
        ('PENDENTE_ENTREGA',             now() - interval '2 days',  now() - interval '1 days'),
        ('ENTREGUE',                     now() - interval '1 days',  NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
),
ins_estoque AS (
    INSERT INTO ordem_servico_estoque (ordem_servico_id, peca_id, quantidade, valor_unitario, disponivel)
    SELECT ins_os.id, e.id, 1, 35.90, true
    FROM ins_os, estoque e WHERE e.nome_item = 'Filtro de óleo'
    RETURNING id
),
ins_servico AS (
    INSERT INTO ordem_servico_servicos (ordem_servico_id, servico_id, valor_aplicado)
    SELECT ins_os.id, s.id, 150.00
    FROM ins_os, servicos s WHERE s.nome_servico = 'Troca de óleo e filtro'
    RETURNING id
)
SELECT 1;

-- =====================================================================
-- OS 2 - João da Silva (ABC1D23): 15 dias em PENDENTE_ESTOQUE, 1 dia nas demais
-- Após reposição, volta a DIAGNOSTICO_CONCLUIDO e segue até ENTREGUE
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '26 days', now() - interval '1 days', 'ENTREGUE', 359.80, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'ABC1D23' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',                       now() - interval '26 days', now() - interval '25 days'),
        ('EM_DIAGNOSTICO',               now() - interval '25 days', now() - interval '24 days'),
        ('DIAGNOSTICO_CONCLUIDO',        now() - interval '24 days', now() - interval '23 days'),
        ('PENDENTE_ESTOQUE',             now() - interval '23 days', now() - interval '8 days'),
        ('DIAGNOSTICO_CONCLUIDO',        now() - interval '8 days',  now() - interval '7 days'),
        ('PENDENTE_NOTIFICACAO_CLIENTE', now() - interval '7 days',  now() - interval '6 days'),
        ('PENDENTE_APROVACAO_CLIENTE',   now() - interval '6 days',  now() - interval '5 days'),
        ('APROVADA',                     now() - interval '5 days',  now() - interval '4 days'),
        ('EM_EXECUCAO',                  now() - interval '4 days',  now() - interval '3 days'),
        ('FINALIZADA',                   now() - interval '3 days',  now() - interval '2 days'),
        ('PENDENTE_ENTREGA',             now() - interval '2 days',  now() - interval '1 days'),
        ('ENTREGUE',                     now() - interval '1 days',  NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
),
ins_estoque AS (
    INSERT INTO ordem_servico_estoque (ordem_servico_id, peca_id, quantidade, valor_unitario, disponivel)
    SELECT ins_os.id, e.id, 2, 89.90, true
    FROM ins_os, estoque e WHERE e.nome_item = 'Pastilha de freio (par)'
    RETURNING id
),
ins_servico AS (
    INSERT INTO ordem_servico_servicos (ordem_servico_id, servico_id, valor_aplicado)
    SELECT ins_os.id, s.id, 180.00
    FROM ins_os, servicos s WHERE s.nome_servico = 'Troca de pastilhas de freio'
    RETURNING id
)
SELECT 1;

-- =====================================================================
-- OS 3 - Maria Oliveira (XYZ9K88): em andamento (EM_EXECUCAO)
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '7 days', now() - interval '18 hours', 'EM_EXECUCAO', 465.00, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'XYZ9K88' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',                       now() - interval '7 days', now() - interval '6 days'),
        ('EM_DIAGNOSTICO',               now() - interval '6 days', now() - interval '4 days'),
        ('DIAGNOSTICO_CONCLUIDO',        now() - interval '4 days', now() - interval '3 days'),
        ('PENDENTE_NOTIFICACAO_CLIENTE', now() - interval '3 days', now() - interval '2 days'),
        ('PENDENTE_APROVACAO_CLIENTE',   now() - interval '2 days', now() - interval '1 days'),
        ('APROVADA',                     now() - interval '1 days', now() - interval '18 hours'),
        ('EM_EXECUCAO',                  now() - interval '18 hours', NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
),
ins_estoque AS (
    INSERT INTO ordem_servico_estoque (ordem_servico_id, peca_id, quantidade, valor_unitario, disponivel)
    SELECT ins_os.id, e.id, 1, 145.00, true
    FROM ins_os, estoque e WHERE e.nome_item = 'Correia dentada'
    RETURNING id
),
ins_servico AS (
    INSERT INTO ordem_servico_servicos (ordem_servico_id, servico_id, valor_aplicado)
    SELECT ins_os.id, s.id, 320.00
    FROM ins_os, servicos s WHERE s.nome_servico = 'Troca de correia dentada'
    RETURNING id
)
SELECT 1;

-- =====================================================================
-- OS 4 - Carlos Souza (DEF4E56): concluída há mais tempo, fases mais longas
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '20 days', now() - interval '8 days', 'ENTREGUE', 1040.00, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'DEF4E56' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',                       now() - interval '20 days', now() - interval '19 days'),
        ('EM_DIAGNOSTICO',               now() - interval '19 days', now() - interval '17 days'),
        ('DIAGNOSTICO_CONCLUIDO',        now() - interval '17 days', now() - interval '16 days'),
        ('PENDENTE_NOTIFICACAO_CLIENTE', now() - interval '16 days', now() - interval '15 days'),
        ('PENDENTE_APROVACAO_CLIENTE',   now() - interval '15 days', now() - interval '13 days'),
        ('APROVADA',                     now() - interval '13 days', now() - interval '12 days'),
        ('EM_EXECUCAO',                  now() - interval '12 days', now() - interval '10 days'),
        ('FINALIZADA',                   now() - interval '10 days', now() - interval '9 days'),
        ('PENDENTE_ENTREGA',             now() - interval '9 days',  now() - interval '8 days'),
        ('ENTREGUE',                     now() - interval '8 days',  NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
),
ins_estoque AS (
    INSERT INTO ordem_servico_estoque (ordem_servico_id, peca_id, quantidade, valor_unitario, disponivel)
    SELECT ins_os.id, e.id, 2, 320.00, true
    FROM ins_os, estoque e WHERE e.nome_item = 'Amortecedor dianteiro'
    RETURNING id
),
ins_servico AS (
    INSERT INTO ordem_servico_servicos (ordem_servico_id, servico_id, valor_aplicado)
    SELECT ins_os.id, s.id, 400.00
    FROM ins_os, servicos s WHERE s.nome_servico = 'Suspensão - troca de amortecedores'
    RETURNING id
)
SELECT 1;

-- =====================================================================
-- OS 5 - Ana Pereira (GHI7F89): cancelada após espera longa em PENDENTE_ESTOQUE
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '12 days', now() - interval '2 days', 'CANCELADA', 480.00, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'GHI7F89' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',                now() - interval '12 days', now() - interval '11 days'),
        ('EM_DIAGNOSTICO',        now() - interval '11 days', now() - interval '10 days'),
        ('DIAGNOSTICO_CONCLUIDO', now() - interval '10 days', now() - interval '9 days'),
        ('PENDENTE_ESTOQUE',      now() - interval '9 days',  now() - interval '2 days'),
        ('CANCELADA',             now() - interval '2 days',  NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
),
ins_estoque AS (
    INSERT INTO ordem_servico_estoque (ordem_servico_id, peca_id, quantidade, valor_unitario, disponivel)
    SELECT ins_os.id, e.id, 1, 380.00, false
    FROM ins_os, estoque e WHERE e.nome_item = 'Bomba de combustível'
    RETURNING id
),
ins_servico AS (
    INSERT INTO ordem_servico_servicos (ordem_servico_id, servico_id, valor_aplicado)
    SELECT ins_os.id, s.id, 100.00
    FROM ins_os, servicos s WHERE s.nome_servico = 'Diagnóstico eletrônico'
    RETURNING id
)
SELECT 1;

-- =====================================================================
-- OS 6 - Lucas Costa (JKL2G34): recém criada, ainda em diagnóstico
-- (sem peças/serviços vinculados, pois só são vinculados ao concluir o diagnóstico)
-- =====================================================================
WITH ins_os AS (
    INSERT INTO ordem_servicos (data_hora_criacao, data_hora_atualizacao, status, valor_total, veiculo_id)
    SELECT now() - interval '1 days', now() - interval '12 hours', 'EM_DIAGNOSTICO', NULL, v.id
    FROM veiculos v, seed_flag f
    WHERE v.placa = 'JKL2G34' AND f.should_seed
    RETURNING id
),
ins_hist AS (
    INSERT INTO ordem_servico_status_historico (ordem_servico_id, status, data_hora_inicio, data_hora_fim)
    SELECT ins_os.id, x.status, x.inicio, x.fim
    FROM ins_os, (VALUES
        ('CRIADA',         now() - interval '1 days',   now() - interval '12 hours'),
        ('EM_DIAGNOSTICO', now() - interval '12 hours', NULL::timestamp)
    ) AS x(status, inicio, fim)
    RETURNING id
)
SELECT 1;