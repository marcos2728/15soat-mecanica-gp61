# =====================================================================
# Subnet Group para o RDS (distribuído em múltiplas AZs)
# =====================================================================
resource "aws_db_subnet_group" "main" {
  name        = "${var.project_name}-${var.environment}-db-subnet-group"
  description = "Subnet group dedicado para as instancias RDS PostgreSQL"
  subnet_ids  = var.subnet_ids

  tags = {
    Name        = "${var.project_name}-${var.environment}-db-subnet-group"
    Environment = var.environment
  }
}

# =====================================================================
# Security Group do Banco de Dados PostgreSQL
# Regra estrita: aceita tráfego na porta 5432 APENAS do Security Group do EKS
# =====================================================================
resource "aws_security_group" "db_sg" {
  name        = "${var.project_name}-${var.environment}-rds-sg"
  description = "Security Group que restringe o acesso ao PostgreSQL exclusivamente aos nos do EKS"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Permite conexao PostgreSQL apenas a partir dos nos de trabalho do EKS"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.eks_security_group_id]
  }

  egress {
    description = "Permite saida para sincronizacao e atualizacoes"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-rds-sg"
    Environment = var.environment
  }
}

# =====================================================================
# Instância Gerenciada do AWS RDS PostgreSQL
# =====================================================================
resource "aws_db_instance" "main" {
  identifier             = "${var.project_name}-${var.environment}-postgres"
  engine                 = "postgres"
  engine_version         = "16.3"
  instance_class         = var.db_instance_class
  allocated_storage      = var.allocated_storage
  max_allocated_storage  = 100
  storage_type           = "gp3"
  storage_encrypted      = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password
  port     = 5432

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  publicly_accessible = false
  multi_az            = false # pode ser alterado para true em producao de alta disponibilidade

  auto_minor_version_upgrade = true
  allow_major_version_upgrade = false
  backup_retention_period    = 7
  backup_window              = "03:00-04:00"
  maintenance_window         = "Sun:04:30-Sun:05:30"
  deletion_protection        = false
  skip_final_snapshot        = var.skip_final_snapshot

  tags = {
    Name        = "${var.project_name}-${var.environment}-postgres"
    Environment = var.environment
  }
}
