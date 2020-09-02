FROM openjdk:14-alpine
COPY build/libs/large-file-upload-*-all.jar large-file-upload.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "large-file-upload.jar"]