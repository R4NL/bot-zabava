./gradlew  clean bootJar
chmod +x /home/thealeshka/IdeaProjects/bot-zabava/build/libs/bot-zabava-0.0.1-SNAPSHOT.jar
docker build -t theleshka/zabava_bot .
docker login -u theleshka -p IgraymnogO3 docker.io
docker push theleshka/zabava_bot