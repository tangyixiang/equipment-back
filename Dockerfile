FROM java:11

ENV home /home/soft/jar

WORKDIR $home

ADD ocs-admin.jar $home/ocs-admin.jar

EXPOSE 9080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","ocs-admin.jar"]
