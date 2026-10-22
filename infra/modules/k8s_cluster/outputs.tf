output "cluster_id" {
  description = "ID do Cluster EKS criado"
  value       = aws_eks_cluster.main.id
}

output "cluster_name" {
  description = "Nome do Cluster EKS criado"
  value       = aws_eks_cluster.main.name
}

output "cluster_endpoint" {
  description = "Endpoint do API Server do Kubernetes no EKS"
  value       = aws_eks_cluster.main.endpoint
}

output "cluster_certificate_authority_data" {
  description = "Certificado de autoridade da CA do cluster em Base64"
  value       = aws_eks_cluster.main.certificate_authority[0].data
}

output "node_security_group_id" {
  description = "ID do Security Group aplicado aos nós de trabalho (workers)"
  value       = aws_security_group.node_sg.id
}
