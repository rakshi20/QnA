## syntax=docker/dockerfile:1
#
## Comments are provided throughout this file to help you get started.
## If you need more help, visit the Dockerfile reference guide at
## https://docs.docker.com/go/dockerfile-reference/
#
## Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7
#
#################################################################################
## FOR MAVEN
#
#FROM alpine as build
#
#ARG USER_HOME_DIR="/root"
#
#
## Install Java.
#RUN apk --update --no-cache add openjdk7 curl
#
#RUN <<EOF
#
#mkdir /opt/maven
#
#maven_version=$(curl -fsSL https://repo1.maven.org/maven2/org/apache/maven/apache-maven/maven-metadata.xml  \
#      | grep -Ev "alpha|beta" \
#      | grep -oP '(?<=version>).*(?=</version)'  \
#      | tail -n1)
#
#maven_download_url="https://repo1.maven.org/maven2/org/apache/maven/apache-maven/$maven_version/apache-maven-${maven_version}-bin.tar.gz"
#
#echo "Downloading [$maven_download_url]..."
#
#curl -fL $maven_download_url | tar zxv -C /opt/maven --strip-components=1
#
#EOF
#
#ENV MAVEN_HOME /opt/maven
#ENV M2_HOME /opt/maven
#ENV PATH="/opt/maven/bin:${PATH}"
#
#ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
#
## Define working directory.
#WORKDIR /data
#
## Define commonly used JAVA_HOME variable
#ENV JAVA_HOME /usr/lib/jvm/default-jvm/
#
## Define default command.
#CMD ["mvn", "--version"]
#
## Create a stage for resolving and downloading dependencies.
#FROM eclipse-temurin:20-jdk-jammy as deps
#
#WORKDIR /build
#
## Copy the mvnw wrapper with executable permissions.
#COPY --chmod=0755 mvnw mvnw
#COPY .mvn/ .mvn/
#
## Download dependencies as a separate step to take advantage of Docker's caching.
## Leverage a cache mount to /root/.m2 so that subsequent builds don't have to
## re-download packages.
#RUN --mount=type=bind,source=pom.xml,target=pom.xml \
#    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests
#
#################################################################################
#
## Create a stage for building the application based on the stage with downloaded dependencies.
## This Dockerfile is optimized for Java applications that output an uber jar, which includes
## all the dependencies needed to run your app inside a JVM. If your app doesn't output an uber
## jar and instead relies on an application server like Apache Tomcat, you'll need to update this
## stage with the correct filename of your package and update the base image of the "final" stage
## use the relevant app server, e.g., using tomcat (https://hub.docker.com/_/tomcat/) as a base image.
#FROM deps as package
#
#WORKDIR /build
#
#COPY ./src src/
#RUN --mount=type=bind,source=pom.xml,target=pom.xml \
#    --mount=type=cache,target=/root/.m2 \
#    ./mvnw package -DskipTests && \
#    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar
#
#
#################################################################################
#
## Create a new stage for running the application that contains the minimal
## runtime dependencies for the application. This often uses a different base
## image from the install or build stage where the necessary files are copied
## from the install stage.
##
## The example below uses eclipse-turmin's JRE image as the foundation for running the app.
## By specifying the "20-jre-jammy" tag, it will also use whatever happens to be the
## most recent version of that tag when you build your Dockerfile.
## If reproducability is important, consider using a specific digest SHA, like
## eclipse-temurin@sha256:99cede493dfd88720b610eb8077c8688d3cca50003d76d1d539b0efc8cca72b4.
#FROM eclipse-temurin:20-jre-jammy AS final
#
## Create a non-privileged user that the app will run under.
## See https://docs.docker.com/go/dockerfile-user-best-practices/
#ARG UID=10001
#RUN adduser \
#    --disabled-password \
#    --gecos "" \
#    --home "/nonexistent" \
#    --shell "/sbin/nologin" \
#    --no-create-home \
#    --uid "${UID}" \
#    appuser
#USER appuser
#
## Copy the executable from the "package" stage.
#COPY --from=package build/target/app.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT [ "java", "-jar", "app.jar" ]
#
#

#FROM openjdk:8-jdk-alpine
#
## Install Maven
#RUN apk add --no-cache maven
#
## Set environment variables
#ENV MAVEN_HOME /usr/share/maven
#ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
#
## Copy your project files into the image
#COPY . /app
#
## Build your project with Maven
#RUN mvn clean install
FROM maven:3.8.5-jdk-11 as maven_builder

# Copy the project files into the container
COPY . /app
# Set work dir same as the location where project files are placed
WORKDIR /app
# Install the project's dependencies
RUN mvn clean install

# Expose port 8080 for your application
EXPOSE 8080

# Start your application
#CMD ["mvn", "spring-boot:run"]

#CMD ["chmod", "755", "target/QnA.jar"]
#
#CMD ["java", "-jar", "target/QnA.jar"]
FROM tomcat

COPY --from=maven_builder app/target/QnA.war /usr/local/tomcat_jar/

WORKDIR /usr/local

COPY ./tomcatWarSetup.sh ./

#RUN chmod 777 tomcatWarSetup.sh

#CMD ["./tomcatWarSetup.sh"]
#CMD ["cp", "tomcat_jar/QnA.war", "tomcat/webapps/"]

#/usr/local/tomcatWarSetup.sh

#tomcatWarSetup.sh content
##!/bin/bash
#
#cp tomcat_jar/QnA.war tomcat/webapps2/
#
#/bin/bash
