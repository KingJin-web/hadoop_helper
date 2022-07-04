FROM java:8
COPY *.jar /hdfs-helper.jar
CMD ["--server.port=9090"]
EXPOSE 9090
ENTRYPOINT ["java","-jar","/hdfs-helper.jar"]