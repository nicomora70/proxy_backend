FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY src src
COPY test test
RUN mkdir -p out && javac --release 11 -encoding UTF-8 -d out $(find src test -name "*.java")

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app
COPY --from=build /app/out out
USER app
ENV PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/api/health || exit 1
ENTRYPOINT ["java", "-cp", "out", "com.bovedadocs.Main"]
