FROM openjdk:8
EXPOSE 8888
ADD target/ubx-utility-docker.jar ubx-utility-docker.jar
ENTRYPOINT ["java","-jar","/ubx-utility-docker.jar"]