FROM eclipse-temurin:17-jre
RUN useradd --system app
COPY build/install/server/lib/ /app/lib/
USER app
ENTRYPOINT ["java", "-cp", "/app/lib/*", \
"com.extracreditstudios.thelastsemester.server.Server"]
