# Guia de Teste de Escalabilidade (HPA) em Ambiente Local

Este diretório contém os manifestos preparados especificamente para rodar **testes de escalabilidade horizontal (HPA)** no seu notebook **sem estourar o consumo de CPU e Memória RAM**.

---

## 💡 Como funciona a economia de recursos?

| Parâmetro | Manifesto Oficial (`k8s/`) | Manifesto Local (`k8s/local/`) | Vantagem Local |
| :--- | :--- | :--- | :--- |
| **Réplicas** | Min 2 / Max 5 | **Min 1 / Max 3** | Gasta ~70% menos memória |
| **Request de CPU** | 200m | **50m** | Apenas 0.05 core de CPU |
| **Gatilho HPA** | 70% (~140m CPU) | **30% (~15m CPU)** | Escala com apenas 5 a 10 requisições/s |
| **Janela de Scale-down** | 300 segundos (5 min) | **60 segundos** | Vê o pod sumir rápido na demonstração |
| **JVM do Spring Boot** | Padrão | **`UseSerialGC` + 256MB** | Pod consome menos de 200MB de RAM real |

---

## 🚀 Passo a Passo para Executar

### 1. Aplicar a infraestrutura básica e a aplicação leve
Se os componentes básicos (banco, config e service) já estiverem rodando no cluster, basta atualizar o app e o HPA leve:

```bash
# Aplica o app e HPA econômicos
kubectl apply -f k8s/local/03-app-deployment.yaml
kubectl apply -f k8s/local/05-app-hpa.yaml
```

*(Se estiver subindo o cluster do zero, aplique antes os arquivos `01-configmap-secret.yaml`, `02-postgres-deployment.yaml`, `04-app-service.yaml` e `06-mailpit-deployment.yaml` da pasta raiz `k8s/`)*.

---

### 2. Abrir o monitoramento em tempo real
Abra dois terminais para acompanhar:

**Terminal 1 (Acompanhar HPA):**
```bash
kubectl get hpa mecanica-app-hpa -n mecanica -w
```

**Terminal 2 (Acompanhar os pods):**
```bash
kubectl get pods -n mecanica -l app=mecanica-app -w
```

---

### 3. Disparar a carga (Início do Scale-Up)
Para iniciar o estresse (sem usar ferramentas pesadas):

```bash
kubectl apply -f k8s/local/07-load-generator.yaml
```

- Em **15 a 30 segundos**, você verá a CPU do pod ultrapassar os 30%.
- O HPA aumentará as réplicas de **1** para **2** e depois para **3 pods**.

---

### 4. Parar a carga (Início do Scale-Down)
Para finalizar o teste e demonstrar a redução automática:

```bash
kubectl delete -f k8s/local/07-load-generator.yaml
```

- A CPU voltará para perto de 0%.
- Após 60 segundos, o Kubernetes removerá os 2 pods excedentes, voltando para **1 réplica**.
