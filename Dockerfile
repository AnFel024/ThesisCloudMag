FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=build/libs/app.jar
ENV PROFILE="test"
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]