# Infraestrutura como Código (IaC) - Tech Challenge Fase 2

Este diretório contém os manifestos e módulos **Terraform** responsáveis por provisionar de forma automatizada, reproduzível e resiliente toda a infraestrutura em nuvem na **Amazon Web Services (AWS)** para a aplicação de Oficina Mecânica / Ordens de Serviço.

---

## 🏛️ Arquitetura Provisionada

A arquitetura segue o modelo corporativo de segregação de rede e princípio do menor privilégio (*Least Privilege / Zero Trust*):

```
                        VPC (10.0.0.0/16)
 ┌──────────────────────────────────────────────────────────────┐
 │  Subnets Públicas (10.0.1.0/24, 10.0.2.0/24)                 │
 │  ├── Internet Gateway (IGW)                                  │
 │  ├── NAT Gateway                                             │
 │  └── AWS Application / Network Load Balancer (ELB)           │
 └──────────────────────────────┬───────────────────────────────┘
                                │ Roteamento Seguro
 ┌──────────────────────────────▼───────────────────────────────┐
 │  Subnets Privadas (10.0.11.0/24, 10.0.12.0/24)               │
 │  └── EKS Cluster (v1.30)                                     │
 │      └── Managed Node Group (Auto Scaling: 2 a 4 nós)       │
 │          └── Pods da Aplicação Mecânica                      │
 └──────────────────────────────┬───────────────────────────────┘
                                │ Porta 5432 (Security Group EKS)
 ┌──────────────────────────────▼───────────────────────────────┐
 │  Subnets Isoladas de Dados (10.0.21.0/24, 10.0.22.0/24)      │
 │  └── AWS RDS PostgreSQL (v16.3)                              │
 │      ├── DB Subnet Group multi-AZ                            │
 │      └── Criptografia de dados em repouso (KMS gp3)          │
 └──────────────────────────────────────────────────────────────┘
```

### Destaques de Segurança e Alta Disponibilidade
- **Rede Isolada:** O banco de dados PostgreSQL RDS fica em subnets isoladas sem qualquer rota para a Internet.
- **Firewall em Camadas (Security Groups):** O banco de dados aceita conexões na porta 5432 **estritamente** originadas pelo Security Group dos nós do EKS.
- **Alta Disponibilidade:** A VPC, os nós do EKS e o DB Subnet Group estão distribuídos em múltiplas Zonas de Disponibilidade (Multi-AZ).
- **Auto Scaling:** Nós de trabalho configurados com elasticidade automática via EKS Managed Node Group.

---

## 📁 Estrutura de Diretórios

```text
infra/
├── main.tf                     # Orquestração da VPC e chamada aos módulos
├── variables.tf                # Declaração de variáveis globais e de entrada
├── outputs.tf                  # Saídas com endpoints, nomes e comandos úteis
├── providers.tf                # Configuração dos provedores AWS e Kubernetes
├── terraform.tfvars.example    # Arquivo de exemplo com valores padrão
├── modules/
│   ├── k8s_cluster/            # Módulo de provisionamento do EKS e nós gerenciados
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── database/               # Módulo do banco de dados relacional RDS PostgreSQL
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
└── README.md                   # Documentação da infraestrutura
```

---

## 🛠️ Pré-requisitos

Antes de iniciar a implantação, certifique-se de possuir instalado em sua máquina:

1. **[Terraform CLI](https://developer.hashicorp.com/terraform/downloads)** (versão `>= 1.5.0`)
2. **[AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)** configurada com credenciais válidas (`aws configure`)
3. **[kubectl](https://kubernetes.io/docs/tasks/tools/)** para interagir com o cluster Kubernetes provisionado

---

## 🚀 Passo a Passo para Implantação

### 1. Preparar o arquivo de variáveis

Navegue até a pasta `infra` e copie o exemplo de variáveis:

```bash
cd infra
cp terraform.tfvars.example terraform.tfvars
```

Edite o arquivo `terraform.tfvars` caso deseje alterar a região, tipo de instância ou senhas:

```hcl
aws_region   = "us-east-1"
project_name = "mecanica"
environment  = "dev"
db_password  = "sua_senha_segura_aqui"
```

### 2. Inicializar o Terraform

Baixa os provedores necessários (`aws`, `kubernetes`) e inicializa os módulos locais:

```bash
terraform init
```

### 3. Planejar o provisionamento

Gera e valida o plano de execução, exibindo todos os recursos que serão criados:

```bash
terraform plan
```

### 4. Aplicar a infraestrutura

Cria toda a VPC, EKS Cluster, Node Group e banco de dados RDS:

```bash
terraform apply -auto-approve
```

> **Tempo estimado:** O provisionamento do cluster EKS e do RDS leva em média entre **10 e 15 minutos**.

---

## 🔗 Conectando o `kubectl` ao Cluster Criado

Após a conclusão do `terraform apply`, execute o comando retornado no output `kubeconfig_command`:

```bash
aws eks update-kubeconfig --region us-east-1 --name mecanica-dev-eks-cluster
```

Valide se os nós do cluster estão em estado `Ready`:

```bash
kubectl get nodes
```

---

## 📦 Deploy da Aplicação no Cluster EKS

Com o cluster conectado, você pode aplicar os manifestos Kubernetes disponíveis na pasta `../k8s`:

```bash
kubectl apply -f ../k8s/
```

Para monitorar os pods:

```bash
kubectl get pods -n mecanica -w
```

---

## 🧹 Destruição dos Recursos

Para encerrar os recursos na nuvem e evitar custos adicionais ao término dos testes:

```bash
terraform destroy -auto-approve
```
