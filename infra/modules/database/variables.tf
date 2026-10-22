variable "project_name" {
  description = "Nome do projeto para identificação e tags"
  type        = string
}

variable "environment" {
  description = "Ambiente de implantação (dev, stg, prod)"
  type        = string
}

variable "vpc_id" {
  description = "ID da VPC onde o banco será provisionado"
  type        = string
}

variable "subnet_ids" {
  description = "Lista de subnets privadas dedicadas ao banco de dados"
  type        = list(string)
}

variable "eks_security_group_id" {
  description = "Security Group dos nós do EKS que terão permissão de acesso ao RDS"
  type        = string
}

variable "db_name" {
  description = "Nome do banco de dados relacional inicial"
  type        = string
  default     = "mecanica"
}

variable "db_username" {
  description = "Nome do usuário administrador do banco de dados"
  type        = string
  default     = "mecanica_admin"
}

variable "db_password" {
  description = "Senha do usuário administrador do banco de dados"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Classe de instância computacional do RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  description = "Tamanho do volume em Gigabytes (GiB)"
  type        = number
  default     = 20
}

variable "skip_final_snapshot" {
  description = "Indica se deve ignorar snapshot final ao destruir (true para dev)"
  type        = bool
  default     = true
}
