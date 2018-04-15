#!/bin/bash

CONSUL_HOST=${1:-localhost}
CURL_OPT=${2:-curl}
CURL_COMMAND="curl"

if [ $CURL_OPT == "docker" ]; then
  CURL_COMMAND="docker exec -t registry curl"
fi

$CURL_COMMAND -X PUT -d 'cache--6379----2--5--0' http://$CONSUL_HOST:8500/v1/kv/ivan/RedisConnectionString
$CURL_COMMAND -X PUT -d 'neo4j://graph-db:7687' http://$CONSUL_HOST:8500/v1/kv/ivan/DB_ConnectionString
$CURL_COMMAND -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/ivan/StampTransactionId
$CURL_COMMAND -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/ivan/AtomicTransactions
$CURL_COMMAND -X PUT -d 'Json' http://$CONSUL_HOST:8500/v1/kv/ivan/Data_Format_Type
$CURL_COMMAND -X PUT -d 'mongodb://document-db:27017/' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_ConnectionString
$CURL_COMMAND -X PUT -d 'mydb' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_DbName
$CURL_COMMAND -X PUT -d 'test' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_DbCollection
$CURL_COMMAND -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/clyman/StampTransactionId
$CURL_COMMAND -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/clyman/ObjectLockingActive
$CURL_COMMAND -X PUT -d 'JSON' http://$CONSUL_HOST:8500/v1/kv/clyman/DataFormatType
$CURL_COMMAND -X PUT -d 'queue:9092' http://$CONSUL_HOST:8500/v1/kv/clyman/KafkaBrokerAddress
$CURL_COMMAND -X PUT -d 'queue:9092' http://$CONSUL_HOST:8500/v1/kv/ivan/KafkaBrokerAddress
