# light-tram-4j
Transactional Messaging framework for message/event/command driven interaction style

### Build

To build the project without running integration test cases. 

```
mvn clean package
```

If you have to install the jars into your local maven repository .m2, then you can

```
mvn clean install -DskipTests
```

Before running integration test cases during the build. We need to start Kafka, Zookeeper
Mysql and CDC server. 

