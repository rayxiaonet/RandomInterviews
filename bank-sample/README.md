# BankApplication

## Features
Per  request, this is a mini project that include a simple web site to mimic a “Banking Portal”. Detail requirement can be found from
[here](OAuth_Backend_Mini_Project.pdf)


## Design
### Key Technology
* Java 8
* Start with Gradle
* Spring boot for the restful webservice (https://projects.spring.io/spring-boot/)
* Spring Security OAuth for Facebook OAuth integration (https://projects.spring.io/spring-security-oauth/)
* AngularJS for a very simple html frontend
* Mongodb for backend datastore
* Docker for deployment

### Architecture

skipped

## Install
### Configure Facebook Dev account

Goto https://developers.facebook.com, setup your facebook dev account, add a new app,  paste the app id/app secret to
```sh
facebook.client.clientId
facebook.client.clientSecret
```
in [src/main/resources/application.yml](src/main/resources/application.yml) respectively.

click "Add product" in fb dev portal, add the "Facebook Login" product, and set "Valid OAuth redirect URIs" to "http://localhost:8080", or your server/port you want to use for this sample project.

### Configure Mongo DB

This sample project need a running mongo db instance. Update the sample project's application configuration file at [src/main/resources/application.yml](src/main/resources/application.yml), and modify the mongo db properties:
```sh    
    product.mongo.host
    product.mongo.port
    product.mongo.database
```
accordingly.

The default setting is localhost:27017, raytestdb


### Build
Run gradlew command to build:
```sh
   $./gradlew build
```
### Test
Run gradlew command to test:
```sh
   $./gradlew test
```
Sample Unit Test can be found from [src/test/java/net/rayxiao/AccountManagerTests](src/test/java/net/rayxiao/AccountManagerTests)

Sample Integration Test can be found from [src/test/java/net/rayxiao/BankApplicationTests](src/test/java/net/rayxiao/BankApplicationTests) 

A embedded mongodb server will be started on localhost:12345 in order to complete the integration test
 
### Coverage
Run gradlew command to generate coverage report
```sh
   $./gradlew check
```
coverate report can be found from build/reports/jacoco/test/html/index.html

### Run
After you have fb account configured, mongo db running, and the application built, use following command to execute it.
```sh
   $java -jar /bank-service-0.0.1-SNAPSHOT.jar
```

If you want to customize the database configuration without modify the source code and rebuild, you can use command line parameters
```sh
   $java -Dspring.application.json='{"product":{"mongo":{"host":"$(MONGO_HOST)","port":$MONGO_PORT,"database":"$MONGO_DB_NAME"}}}' -jar /bank-service-0.0.1-SNAPSHOT.jar
```
further details cand be found from [Spring boot document](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)

### Access The UI
Once application started, access http://localhost:8080 with your browser to checkout the functionalities.



## Run with Docker 
Using Docker for build and deployment will reduce the complexity significantly and improve efficiency than traditional ways.
You still need to complete the FB dev account part documented in previous chapter first.
### Run with Image from docker hub
I already pushed the docker images to dockerhub, you can simply pull my image and move to "Run image" part, without need to build local container image
```sh
$ docker run --name some-mongo -d mongo
$ docker run --name  sample  -p 8080:8080 --link some-mongo:mongo -d rayxiaonet/banksample
```

### Run with local Container Image
#### Build
Run Gradle build to compile the source code first
```sh
$ ./gradlew build
```

Dockerfile already placed at project root, simply run following docker command to build the images 
```sh
$ docker build . --tag banksample:latest
```
in root folder to build the image and tag it as "banksample"

#### Run 
use following command to run a mongo container on the fly first:
```sh
$ docker run --name some-mongo -d mongo
```
then use following command to run your bank project with mongo db container linked:
```sh
$ docker run --name  sample  -p 8080:8080 --link some-mongo:mongo -d banksample
```

#### Access The UI
Since we already bind the 8080 port of the sampl container to your docker host's 8080 port, once the container started, you should able to access http://localhost:8080 with your browser to checkout the functionalities.


   
