# --- Estágio 1: Build com Maven e Java 24 ---
# Usamos uma imagem oficial do Java 24 e instalamos o Maven nela.
FROM eclipse-temurin:24-jdk AS builder

# Instala o Maven
RUN apt-get update && \
    apt-get install -y maven

# Define o diretório de trabalho
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache do Docker
COPY pom.xml .

# Baixa as dependências (opcional, mas acelera builds futuros)
RUN mvn dependency:go-offline

# Copia o resto do código-fonte
COPY src ./src

# Executa o build, pulando os testes. Isso vai criar a pasta /app/target/ com o .jar
RUN mvn clean package -DskipTests


# --- Estágio 2: Imagem final ---
# Usamos uma imagem leve, apenas com o Java 24 para rodar a aplicação
FROM eclipse-temurin:24-jre

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio 'builder'
# O nome do .jar deve ser o mesmo que o Maven gera.
COPY --from=builder /app/target/leituraconsultaloc-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que sua aplicação Spring Boot usa (padrão 8080)
EXPOSE 8080

# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]