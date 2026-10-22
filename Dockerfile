# =====================================================================
# Estágio 1: Build da aplicação utilizando Maven e Eclipse Temurin 21 (LTS)
# =====================================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Otimização de cache de camadas: baixa dependências antes de copiar código-fonte
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código-fonte e compila gerando o artefato final
COPY src ./src
RUN mvn clean package -DskipTests -B

# =====================================================================
# Estágio 2: Imagem final de execução (Runtime) leve e segura
# =====================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Criação de usuário e grupo não-root para conformidade com CIS Benchmarks
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copia apenas o artefato JAR gerado no estágio de build
COPY --from=builder /build/target/*.jar app.jar

# Ajusta permissões para o usuário de serviço
RUN chown -R appuser:appgroup /app

# Troca para o usuário não privilegiado
USER appuser:appgroup

# Porta exposta pela aplicação Spring Boot
EXPOSE 8080

# Configurações otimizadas da JVM para containers Kubernetes/Docker
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Inicialização da aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]