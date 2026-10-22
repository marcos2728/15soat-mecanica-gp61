# =====================================================================
# Variáveis Gerais e de Provedor
# =====================================================================
variable "aws_region" {
  description = "Região da AWS para o provisionamento dos recursos"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Nome do projeto para identificação de recursos"
  type        = string
  default     = "mecanica"
}

variable "environment" {
  description = "Ambiente de implantação (dev, stg, prod)"
  type        = string
  default     = "dev"
}

# =====================================================================
# Variáveis de Rede (VPC)
# =====================================================================
variable "vpc_cidr" {
  description = "Bloco CIDR principal da VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Blocos CIDR para subnets públicas (expostas via Internet Gateway)"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Blocos CIDR para subnets privadas (onde residirão os nós do EKS)"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

variable "database_subnet_cidrs" {
  description = "Blocos CIDR para subnets isoladas de banco de dados (RDS)"
  type        = list(string)
  default     = ["10.0.21.0/24", "10.0.22.0/24"]
}

# =====================================================================
# Variáveis do Cluster Kubernetes (EKS)
# =====================================================================
variable "k8s_version" {
  description = "Versão do Kubernetes gerenciada pelo EKS"
  type        = string
  default     = "1.30"
}

variable "node_instance_types" {
  description = "Tipo de instância EC2 para os worker nodes do EKS"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "node_min_capacity" {
  description = "Capacidade mínima de nós no cluster"
  type        = number
  default     = 2
}

variable "node_max_capacity" {
  description = "Capacidade máxima de nós no cluster"
  type        = number
  default     = 4
}

variable "node_desired_capacity" {
  description = "Capacidade desejada de nós no cluster"
  type        = number
  default     = 2
}

# =====================================================================
# Variáveis do Banco de Dados PostgreSQL (RDS)
# =====================================================================
variable "db_name" {
  description = "Nome do banco de dados inicial"
  type        = string
  default     = "mecanica"
}

variable "db_username" {
  description = "Usuário mestre do banco PostgreSQL"
  type        = string
  default     = "mecanica_admin"
}

variable "db_password" {
  description = "Senha do usuário mestre do PostgreSQL"
  type        = string
  sensitive   = true
  default     = "mecanica_secure_password_123"
}

variable "db_instance_class" {
  description = "Classe computacional da instância RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Tamanho inicial de armazenamento (GiB) do RDS"
  type        = number
  default     = 20
}

variable "db_skip_final_snapshot" {
  description = "Pular snapshot final na destruição do banco (útil para dev)"
  type        = bool
  default     = true
}
