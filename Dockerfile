# AS <NAME> to name this stage as maven
FROM maven:3.8.6 AS maven

# Tambahkan perintah USER untuk menentukan pengguna non-root
USER $USER

WORKDIR /usr/src/app
COPY . /usr/src/app
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17-bullseye

ARG JAR_FILE=zapphireye-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar","zapphireye-0.0.1-SNAPSHOT.jar"]
HEALTHCHECK --interval=5s --timeout=3s \
CMD curl -f http://localhost:8080/ || exit 1
