FROM openjdk:19-jdk-alpine
COPY . /app/source
WORKDIR /app/source
RUN /bin/sh -c "./mvnw clean package -DskipTests"
WORKDIR /app
RUN cp source/target/*.jar app.jar
RUN #cp source/wait-for-it.sh .
RUN rm -rf ./source
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]