FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN mkdir -p unpacked && cd unpacked && jar -xf ../app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/unpacked/BOOT-INF/lib /app/lib
COPY --from=build /app/unpacked/BOOT-INF/classes /app
COPY --from=build /app/unpacked/META-INF /app/META-INF
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app:app/lib/*",
            "com.nksoft.entrance_examinmation.EntranceExaminationApplication"]
