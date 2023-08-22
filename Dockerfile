FROM maven:3-amazoncorretto-8 AS endless-field-build
WORKDIR /app
COPY endless-field/pom.xml ./pom.xml
RUN mvn dependency:go-offline -P war

COPY endless-field/src ./src
RUN mvn clean install -P jar
RUN mvn clean install -P war


FROM maven:3-amazoncorretto-8 AS build

COPY --from=endless-field-build /root/.m2/repository/com/zhukovsd/endless-field /root/.m2/repository/com/zhukovsd/endless-field

WORKDIR /app
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline

RUN ls -la /root/.m2/repository

COPY src ./src
RUN mvn package


FROM tomcat:9-jre8
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war