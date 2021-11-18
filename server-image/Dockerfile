FROM openjdk:16-jdk-alpine

ARG SPIGOT_REV=latest

RUN apk add git
RUN git config --global user.email "spigot@example.com"
RUN git config --global user.name "spigot"

WORKDIR /minecraft

# Since Dec 2019, to build CraftBukkit the option `--compile craftbukkit` has to be used
RUN wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar && \
  java -jar BuildTools.jar --rev $SPIGOT_REV && \
  rm -rf /root/.m2 && \
  find * -maxdepth 0 ! -name '*.jar' -exec rm -rf {} \; && \
  mv spigot-*.jar spigot.jar

VOLUME /minecraft
EXPOSE 25565
EXPOSE 6379

WORKDIR /minecraft

RUN wget https://transfer.sh/get/O62v1w/server.properties

RUN mkdir plugins
RUN wget -O plugins/Relay.jar https://transfer.sh/get/djwG10/Relay.jar

RUN echo "eula=true" > eula.txt
CMD ["java", "-Xms512M", "-Xmx1536M", "-jar", "/minecraft/spigot.jar", "--noconsole"]
