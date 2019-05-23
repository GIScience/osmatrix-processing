#!/bin/bash

# Times
echo "/*";
echo "* TIMES TABLE SQL TEXT";
echo "*/";
echo "";

echo "DROP TABLE IF EXISTS times;"
echo "CREATE TABLE times ("
echo "id serial NOT NULL,"
echo '"time"' "timestamp without time zone NOT NULL,"
echo "CONSTRAINT pk_times PRIMARY KEY (id ),"
echo "CONSTRAINT times_time_key" 'UNIQUE ("time" )'
echo ')'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX times_id_index ON times (id)"
echo "TABLESPACE osmatrix_index;"