#!/bin/bash
set -e

bin=/data/elasticsearch-jdbc-2.3.4.1/bin
lib=/data/elasticsearch-jdbc-2.3.4.1/lib
echo '{
"type" : "jdbc",
"jdbc" : {
"url" : "jdbc:mysql://127.0.0.1:3306/gank",
"user" : "root",
"password" : "root",
"sql" : "select * from article where created > date_sub(now(),interval 1 minute);",
"index": "gank",
"type": "article",
"schedule" : "0 0-59 0-23 ? * *"
}
}' | java \
-cp "${lib}/*" \
-Dlog4j.configurationFile=${bin}/log4j2.xml \
org.xbib.tools.Runner \
org.xbib.tools.JDBCImporter
