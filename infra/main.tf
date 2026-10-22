# =====================================================================
# Consulta de Zonas de Disponibilidade (AZs) ativas na região
# =====================================================================
data "aws_availability_zones" "available" {
  state = "available"
}

# =====================================================================
# VPC Dedicada de Alta Disponibilidade
# =====================================================================
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name                                                          = "${var.project_name}-${var.environment}-vpc"
    "kubernetes.io/cluster/${var.project_name}-${var.environment}-eks-cluster" = "shared"
  }
}

# =====================================================================
# Internet Gateway (Acesso à Internet para subnets públicas)
# =====================================================================
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-${var.environment}-igw"
  }
}

# =====================================================================
# Subnets Públicas (para NAT Gateways e Load Balancers externos)
# =====================================================================
resource "aws_subnet" "public" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name                                                          = "${var.project_name}-${var.environment}-public-subnet-${count.index + 1}"
    "kubernetes.io/role/elb"                                      = "1"
    "kubernetes.io/cluster/${var.project_name}-${var.environment}-eks-cluster" = "shared"
  }
}

# =====================================================================
# Subnets Privadas (onde residem os nós de trabalho do EKS)
# =====================================================================
resource "aws_subnet" "private" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name                                                          = "${var.project_name}-${var.environment}-private-subnet-${count.index + 1}"
    "kubernetes.io/role/internal-elb"                             = "1"
    "kubernetes.io/cluster/${var.project_name}-${var.environment}-eks-cluster" = "shared"
  }
}

# =====================================================================
# Subnets Isoladas para Banco de Dados (RDS)
# =====================================================================
resource "aws_subnet" "database" {
  count             = length(var.database_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.database_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${var.project_name}-${var.environment}-db-subnet-${count.index + 1}"
  }
}

# =====================================================================
# NAT Gateway para permitir tráfego de saída das subnets privadas
# =====================================================================
resource "aws_eip" "nat" {
  domain = "vpc"

  tags = {
    Name = "${var.project_name}-${var.environment}-nat-eip"
  }
}

resource "aws_nat_gateway" "nat" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[0].id

  tags = {
    Name = "${var.project_name}-${var.environment}-nat-gw"
  }

  depends_on = [aws_internet_gateway.igw]
}

# =====================================================================
# Tabelas de Roteamento e Associações
# =====================================================================
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-public-rt"
  }
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat.id
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-private-rt"
  }
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table" "database" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-${var.environment}-db-rt"
  }
}

resource "aws_route_table_association" "database" {
  count          = length(aws_subnet.database)
  subnet_id      = aws_subnet.database[count.index].id
  route_table_id = aws_route_table.database.id
}

# =====================================================================
# Módulo do Cluster Kubernetes (EKS)
# =====================================================================
module "k8s_cluster" {
  source = "./modules/k8s_cluster"

  project_name     = var.project_name
  environment      = var.environment
  vpc_id           = aws_vpc.main.id
  subnet_ids       = aws_subnet.private[*].id
  k8s_version      = var.k8s_version
  instance_types   = var.node_instance_types
  desired_capacity = var.node_desired_capacity
  min_capacity     = var.node_min_capacity
  max_capacity     = var.node_max_capacity
}

# =====================================================================
# Módulo do Banco de Dados Relacional (RDS PostgreSQL)
# =====================================================================
module "database" {
  source = "./modules/database"

  project_name          = var.project_name
  environment           = var.environment
  vpc_id                = aws_vpc.main.id
  subnet_ids            = aws_subnet.database[*].id
  eks_security_group_id = module.k8s_cluster.node_security_group_id
  db_name               = var.db_name
  db_username           = var.db_username
  db_password           = var.db_password
  db_instance_class     = var.db_instance_class
  allocated_storage     = var.db_allocated_storage
  skip_final_snapshot   = var.db_skip_final_snapshot
}
