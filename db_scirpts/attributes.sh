#!/bin/bash

# attributes
echo "/*";
echo "* SQL TEXT FOR ATTRIBUTES";
echo "*/";
echo "";

echo "DROP TABLE IF EXISTS attributes;"
echo "CREATE TABLE attributes ("
echo "id serial NOT NULL,"
echo "cell_id integer NOT NULL,"
echo "attribute_type_id integer NOT NULL,"
echo "value double precision NOT NULL,"
echo "valid integer NOT NULL,"
echo "expired integer,"
echo "geom geometry (Multipolygon, 900913 ),"

echo "CONSTRAINT pk_attributes PRIMARY KEY (id ),"
echo "CONSTRAINT fk_attribute_types FOREIGN KEY (attribute_type_id)" 
echo '	REFERENCES attribute_types (id) '
echo ' ON UPDATE CASCADE ON DELETE CASCADE,'

echo "CONSTRAINT fk_cells FOREIGN KEY (cell_id)"
echo '  REFERENCES cells (id) '
echo ' ON UPDATE CASCADE ON DELETE CASCADE,'

echo "CONSTRAINT fk_expired FOREIGN KEY (expired)"
echo '  REFERENCES times (id) '
echo ' ON UPDATE CASCADE ON DELETE CASCADE,'

echo "CONSTRAINT fk_valid FOREIGN KEY (valid)"
echo '  REFERENCES times (id) '
echo ' ON UPDATE CASCADE ON DELETE CASCADE'
echo ')'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX attributes_id_index ON attributes (id)"
echo "TABLESPACE osmatrix_index;"


