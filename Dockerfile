FROM ubuntu:latest
MAINTAINER tomas chovanec "tomasnt4@gmail.com"
RUN apt-get update && apt-get install -y openjdk-8-jdk

ENV version=aws-db-usage
ENV dbuser=postgres
ENV dbpass=password321
ENV jdbcurl=jdbc:postgresql://pmadatabaseaws.cnh7vzmhukta.us-east-2.rds.amazonaws.com:5432/postgres

WORKDIR /usr/local/bin
ADD target/pma-app.jar .
ENTRYPOINT ["java", "-jar", "pma-app.jar"]