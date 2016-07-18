FROM java:8

WORKDIR /var/app

ADD excord-*.jar /var/app/excord.jar
ADD application.properties /var/app/application.properties

ENTRYPOINT [ "java", "-jar", "/var/app/excord.jar" ]

EXPOSE 9090
