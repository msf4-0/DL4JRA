FROM openjdk:8 as build

COPY .m2 /root/.m2

# Set the current working directory inside the image
WORKDIR /app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Copy the project source
COPY src src

COPY odmodels odmodels

# Build all the dependencies in preparation to go offline. 
# This is a separate step so the dependencies will be cached unless 
# the pom.xml file has changed.
# RUN ./mvnw -o install -DskipTests
# RUN ./mvnw dependency:go-offline -B


EXPOSE 8081
EXPOSE 3000
EXPOSE 9000
ENTRYPOINT ["java","-jar","com.dl4jra.server.ServerApplication"]

# ENTRYPOINT ["java", "com.dl4jra.server.ServerApplication"]

# # Package the application
# RUN ./mvnw -o package -DskipTests
# RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# #### Stage 2: A minimal docker image with command to run the app 
# FROM openjdk:8

# ARG DEPENDENCY=/app/target/dependency

# # Copy project dependencies from the build stage
# COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
# COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
# COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# EXPOSE 8081
# EXPOSE 3000
# EXPOSE 9000

# ENTRYPOINT ["java","-cp","app:app/lib/*","com.dl4jra.server.ServerApplication"]