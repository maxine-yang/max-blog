# 第一階段：編譯代碼
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# 第二階段：運行代碼 (改用 Amazon Corretto，這是目前最穩定的 Java 17 鏡像)
FROM amazoncorretto:17-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
