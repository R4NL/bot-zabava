FROM openjdk:17-oracle
COPY build/libs .
CMD java -jar bot-zabava-0.0.1-SNAPSHOT.jar