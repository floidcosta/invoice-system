FROM maven:3.8.5-openjdk-17
# FROM maven:3.8.2-jdk-8 # for Java 8

WORKDIR /invoice-app
COPY . .
RUN mvn clean install -DskipTests -U

CMD mvn spring-boot:run