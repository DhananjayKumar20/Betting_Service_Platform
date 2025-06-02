FROM openjdk:17

WORKDIR /Betting_Service_Platforms

COPY target/Betting_Service_Platforms-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Betting_Service_Platforms-0.0.1-SNAPSHOT.jar"]
