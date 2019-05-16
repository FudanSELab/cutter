FROM java:8-jre

ADD ./target/cutter-0.0.1-SNAPSHOT.jar /app/
ADD data/kieker-20190514-075832442-UTC-001.dat /app/
CMD ["java", "-Xmx400m", "-jar", "/app/cutter-0.0.1-SNAPSHOT.jar"]

EXPOSE 16318