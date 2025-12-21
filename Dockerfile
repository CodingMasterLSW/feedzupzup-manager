FROM eclipse-temurin:21-jre

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 jar 복사
COPY build/libs/*SNAPSHOT.jar /app/feed-zupzup-manager.jar

WORKDIR /app

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-jar", "feed-zupzup-manager.jar"]