output "db_instance_endpoint" {
  description = "Endpoint completo de conexao do banco (host:porta)"
  value       = aws_db_instance.main.endpoint
}

output "db_instance_address" {
  description = "Hostname/Endereco de rede da instancia RDS"
  value       = aws_db_instance.main.address
}

output "db_instance_port" {
  description = "Porta de conexao do PostgreSQL"
  value       = aws_db_instance.main.port
}

output "db_name" {
  description = "Nome do banco de dados relacional criado"
  value       = aws_db_instance.main.db_name
}
