# --- Estágio 1: Build com Maven ---
# Usamos uma imagem que já tem Maven e JDK para construir o projeto
FROM maven:3.8.5-openjdk-17 AS builder

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
# Usamos uma imagem leve, apenas com o Java para rodar a aplicação
FROM openjdk:17-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio 'builder'
# O nome do .jar deve ser o mesmo que o Maven gera.
COPY --from=builder /app/target/leitura-consultaLoc-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que sua aplicação Spring Boot usa (padrão 8080)
EXPOSE 8080

# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]