#!/bin/bash

echo "/*"
echo "* TABLE SQL CELL_TYPES"
echo "*/"
echo ""
echo "DROP TABLE IF EXISTS cell_types;"
echo "CREATE TABLE cell_types ("
echo "id serial NOT NULL,"
echo "cell_size double precision NOT NULL,"
echo "bounds geometry,"
echo "description text,"
echo "CONSTRAINT pk_cell_types PRIMARY KEY (id )"
echo ')'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX cell_types_id_index ON cell_types (id)"
echo "TABLESPACE osmatrix_index;"
