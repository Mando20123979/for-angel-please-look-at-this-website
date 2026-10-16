FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY App.java index.html ./
RUN javac App.java

# Render provides PORT at runtime; this is just the local default
ENV PORT=10000
EXPOSE 10000

CMD ["java", "App"]
