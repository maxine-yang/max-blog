# 第一階段：編譯代碼 (使用 Maven)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 第二階段：運行代碼 (改用 Amazon Corretto，這是目前 Java 17 最穩定的版本)
FROM amazoncorretto:17-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
