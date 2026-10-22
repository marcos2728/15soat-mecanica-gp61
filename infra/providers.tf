terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.30"
    }
  }

  # Descomente e configure caso utilize S3/DynamoDB como backend remoto
  # backend "s3" {
  #   bucket         = "mecanica-terraform-state"
  #   key            = "dev/terraform.tfstate"
  #   region         = "us-east-1"
  #   dynamodb_table = "mecanica-terraform-locks"
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
      Course      = "FIAP-SOAT"
    }
  }
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.k8s_cluster.cluster_name
}

provider "kubernetes" {
  host                   = module.k8s_cluster.cluster_endpoint
  cluster_ca_certificate = base64decode(module.k8s_cluster.cluster_certificate_authority_data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}
