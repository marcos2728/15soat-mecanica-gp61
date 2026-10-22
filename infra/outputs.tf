# =====================================================================
# Outputs do Cluster EKS
# =====================================================================
output "cluster_name" {
  description = "Nome do Cluster EKS criado"
  value       = module.k8s_cluster.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint da API do cluster Kubernetes"
  value       = module.k8s_cluster.cluster_endpoint
}

output "kubeconfig_command" {
  description = "Comando AWS CLI para atualizar as credenciais do kubeconfig local"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${module.k8s_cluster.cluster_name}"
}

# =====================================================================
# Outputs do Banco de Dados PostgreSQL (RDS)
# =====================================================================
output "database_endpoint" {
  description = "Endpoint completo de conexao ao RDS (host:porta)"
  value       = module.database.db_instance_endpoint
}

output "database_address" {
  description = "Hostname de rede da instancia RDS PostgreSQL"
  value       = module.database.db_instance_address
}

output "database_port" {
  description = "Porta padrao de conexao com o PostgreSQL"
  value       = module.database.db_instance_port
}

output "database_name" {
  description = "Nome do banco de dados relacional criado"
  value       = module.database.db_name
}
