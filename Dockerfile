FROM maven:3.8.4 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY libs ./libs

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

EXPOSE 8081

 # Copie o arquivo JAR do seu projeto para dentro do contêiner
COPY --from=builder /app/target/*.jar /app/mscliente.jar

# Comando para executar o projeto quando o contêiner for iniciado
CMD ["java", "-jar", "/app/mscliente.jar"]