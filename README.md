# This repository is deprecated and replaced by [light-kafka](https://github.com/networknt/light-kafka) for Event Sourcing and CQRS with Kafka Streams and Interactive Queries. 

Transactional Messaging framework for message/event/command driven interaction style

[Stack Overflow](https://stackoverflow.com/questions/tagged/light-4j) |
[Google Group](https://groups.google.com/forum/#!forum/light-4j) |
[Gitter Chat](https://gitter.im/networknt/light-tram-4j) |
[Subreddit](https://www.reddit.com/r/lightapi/) |
[Youtube Channel](https://www.youtube.com/channel/UCHCRMWJVXw8iB7zKxF55Byw) |
[Documentation](https://doc.networknt.com/style/light-tram-4j/) |
[Contribution Guide](https://doc.networknt.com/contribute/) |

### Build

To build the project without running integration test cases. 

```
mvn clean package
```

If you have to install the jars into your local maven repository .m2, then you can

```
mvn clean install -DskipTests
```

### Test

Before running integration test cases during the build. We need to start Kafka, Zookeeper
Mysql and CDC server. 

First find out your computer IP address by issue ifconfig and find the IP address starts
with 192.XXX.XXX.XXX or 10.XXX.XXX.XXX

On my desktop it is 192.168.1.120 and you might have a different IP address. Once you found
it, please run the following command with your IP to set DOCKER_HOST_IP.  

```
export DOCKER_HOST_IP=192.168.1.120
```
On the same terminal, let's start Kafka, Zookeeper and Mysql

```
cd ~
mkdir networknt
cd networknt
git clone https://github.com/networknt/light-docker.git
cd light-docker
docker-compose -f docker-compose-eventuate.yml up
```
Above docker-compose will start Kafka, Zookeeper and Mysql. Wait for all three services
are up and running, then start Tram CDC server.

```
cd ~/networknt/light-docker
docker-compose -f docker-compose-cdcserver-for-tram.yml up
``` 

Now you can run all the integration tests. 

```
cd ~/networknt/light-tram-4j
mvn clean install
```

