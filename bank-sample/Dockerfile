FROM openjdk:8
ADD build/libs/bank-service-0.0.1-SNAPSHOT.jar /
EXPOSE 8080

CMD ["java","-Dproduct.mongo.host=mongo","-jar", "bank-service-0.0.1-SNAPSHOT.jar"]



