FROM openjdk:8-jdk-alpine

#ARG JAR_FILE=target/*.jar
#COPY ./target/demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java","-jar","demo-0.0.1-SNAPSHOT.jar"]
WORKDIR ./
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run