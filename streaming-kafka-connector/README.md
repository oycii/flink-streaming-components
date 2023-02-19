# Component for job pipeline kafka connection
Read topics and create for every message universal case class InputMsg

## Init kafka broker 
git clone https://github.com/Gorini4/kafka_scala_example
cd $HOME/projects/otus/kafka_scala_example/
docker-compose up


## Create kafka topics
docker-compose exec broker -ti bash

bin/kafka-topics.sh --bootstrap-server localhost:29092 --create --topic input --partitions 3 --replication-factor 1
bin/kafka-topics.sh --bootstrap-server localhost:29092 --create --topic output --partitions 3 --replication-factor 1

## Install
```
sbt clean
sbt package
sbt publishLocal
sbt assembly
```

## Run IDE
--appName streaming-kafka-connector
--parallelism 3
--checkpointingTimeout 10000
--kafkaConsumerBootstrapServers 127.0.0.1:29092 
--kafkaConsumerTopics input 
--kafkaConsumerGroupId KafkaConnector-15 
--kafkaConsumerClientIdPrefix ClientId-15 
--kafkaProducerBootstrapServers 127.0.0.1:29092 
--kafkaProducerTopic output 
--kafkaProducerTransactionalIdPrefix TransactionalId-15 
--kafkaConsumerOptions isolation.level=read_committed,session.timeout.ms=300000,enable.auto.commit=true,auto.commit.interval.ms=1000
--kafkaProducerOptions commit.offsets.on.checkpoint=true,enable.idempotence=true,max.in.flight.requests.per.connection=5,transaction.timeout.ms=120000,retries=1,acks=all     


## Run cluster
flink run $HOME/projects/otus/flink-streaming-components/streaming-kafka-connector/target/scala-2.12/streaming-kafka-connector-assembly-0.1.jar --appName KafkaConnector \
--parallelism 3 \
--checkpointingTimeout 10000 \
--kafkaConsumerBootstrapServers 127.0.0.1:29092 \ 
--kafkaConsumerTopics input \
--kafkaConsumerGroupId KafkaConnector-15 \
--kafkaConsumerClientIdPrefix ClientId-15 \
--kafkaProducerBootstrapServers 127.0.0.1:29092 \
--kafkaProducerTopic output \
--kafkaProducerTransactionalIdPrefix TransactionalId-15 \
--kafkaConsumerOptions isolation.level=read_committed,session.timeout.ms=300000,enable.auto.commit=true,auto.commit.interval.ms=1000 \
--kafkaProducerOptions commit.offsets.on.checkpoint=true,enable.idempotence=true,max.in.flight.requests.per.connection=5,transaction.timeout.ms=120000,retries=1,acks=all      