variable "project_name" {
  description = "Nome do projeto para identificação e tags"
  type        = string
}

variable "environment" {
  description = "Ambiente de implantação (dev, stg, prod)"
  type        = string
}

variable "vpc_id" {
  description = "ID da VPC onde o cluster será implantado"
  type        = string
}

variable "subnet_ids" {
  description = "Lista de IDs de subnets privadas onde o cluster e os nós residirão"
  type        = list(string)
}

variable "k8s_version" {
  description = "Versão do Kubernetes do cluster EKS"
  type        = string
  default     = "1.30"
}

variable "instance_types" {
  description = "Tipos de instâncias EC2 para o Node Group gerenciado"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "desired_capacity" {
  description = "Quantidade desejada de nós no Node Group"
  type        = number
  default     = 2
}

variable "min_capacity" {
  description = "Quantidade mínima de nós no Node Group"
  type        = number
  default     = 2
}

variable "max_capacity" {
  description = "Quantidade máxima de nós no Node Group"
  type        = number
  default     = 4
}
