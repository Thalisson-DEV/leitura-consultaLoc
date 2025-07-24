# Use uma imagem base oficial do Java. Use a versão do Java do seu projeto (11, 17, etc.)
FROM openjdk:24-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar do seu projeto para o container
# O nome do .jar deve ser o mesmo que o Maven ou Gradle gera. Verifique sua pasta 'target'.
COPY target/leitura-consultaLoc-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que sua aplicação Spring Boot usa (padrão 8080)
EXPOSE 8080

# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]