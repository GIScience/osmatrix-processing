#!/bin/bash

echo "/*"
echo "* TABLE SQL CELLS"
echo "*/"
echo "DROP TABLE IF EXISTS cells;"
echo "CREATE TABLE cells ("
echo "id double precision NOT NULL,"
echo "geom geometry(MultiPolygon,900913),"
echo "type double precision,"
echo "CONSTRAINT cells2_pkey PRIMARY KEY (id )"
echo ')'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX cells_id_index ON cells (id)"
echo "TABLESPACE osmatrix_index;"

echo "CREATE INDEX sidx_cells2_geom"
echo 'ON cells'
echo 'USING gist'
echo "(geom)"
echo "TABLESPACE osmatrix_index;"
