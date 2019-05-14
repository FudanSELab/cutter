FROM java:8-jre

ADD ./target/cutter-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "-Xmx400m", "-jar", "/app/cutter-0.0.1-SNAPSHOT.jar"]

EXPOSE 16318