FROM openjdk:8-jdk-alpine
COPY ./target/rentmicroservice-0.0.1.jar ./
CMD ["java","-jar","rentmicroservice-0.0.1.jar"]
