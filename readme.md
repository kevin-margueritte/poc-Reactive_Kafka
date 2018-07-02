# Kafka client with Reactive Kafka

## Run

 - Launch cluster `docker-compose up --force-recreate`
 - Download the sample [here](https://s3-eu-west-1.amazonaws.com/static.tabmo.io/jobs/dataeng/tweets.json)
 - Fill the topic `cat tweets.json  | kafka-console-producer --broker-list PLAINTEXT://0.0.0.0:9092 --timeout 1000 --topic tweets `
 - Launch consumer `sbt run`
