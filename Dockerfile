# 基础镜像（使用轻量版 JDK 21）
FROM eclipse-temurin:21-jdk-alpine

# 工作目录
WORKDIR /app

# 拷贝 Spring Boot 打包后的 jar 文件
COPY target/pos-backend-0.0.1-SNAPSHOT.jar app.jar

# 拷贝 Docker 环境配置文件
COPY application-docker.yml /app/application-docker.yml

# 暴露端口
EXPOSE 8080

# 启动命令（使用 Docker 配置文件）
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
