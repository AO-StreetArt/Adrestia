#!/bin/bash

CONSUL_HOST=${1:-localhost}

curl -X PUT -d 'cache--6379----2--5--0' http://$CONSUL_HOST:8500/v1/kv/ivan/RedisConnectionString
curl -X PUT -d 'neo4j://graph-db:7687' http://$CONSUL_HOST:8500/v1/kv/ivan/DB_ConnectionString
curl -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/ivan/StampTransactionId
curl -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/ivan/AtomicTransactions
curl -X PUT -d 'Json' http://$CONSUL_HOST:8500/v1/kv/ivan/Data_Format_Type
curl -X PUT -d 'cache--6379----2--5--0' http://$CONSUL_HOST:8500/v1/kv/clyman/RedisConnectionString
curl -X PUT -d 'mongodb://document-db:27017/' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_ConnectionString
curl -X PUT -d 'mydb' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_DbName
curl -X PUT -d 'test' http://$CONSUL_HOST:8500/v1/kv/clyman/Mongo_DbCollection
curl -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/clyman/StampTransactionId
curl -X PUT -d 'False' http://$CONSUL_HOST:8500/v1/kv/clyman/AtomicTransactions
curl -X PUT -d 'True' http://$CONSUL_HOST:8500/v1/kv/clyman/ObjectLockingActive
curl -X PUT -d 'JSON' http://$CONSUL_HOST:8500/v1/kv/clyman/DataFormatType
curl -X PUT -d 'queue:9092' http://$CONSUL_HOST:8500/v1/kv/clyman/KafkaBrokerAddress
