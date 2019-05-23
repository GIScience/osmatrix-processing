#!/bin/bash


# attribute_types
echo "/*";
echo "*SQL TEXT FOR ATTRIBUTE TYPES";
echo "*/";
echo "";

echo "DROP TABLE IF EXISTS attribute_types;"
echo "CREATE TABLE attribute_types ("
echo "id serial NOT NULL,"
echo "attribute text NOT NULL,"
echo "description text,"
echo "title text,"
echo "validfrom timestamp without time zone,"
echo "CONSTRAINT pk_attribute_types PRIMARY KEY (id ),"
echo "CONSTRAINT attribute_types_attribute_key" 'UNIQUE (attribute )'
echo ')'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX attribute_types_id_index ON attribute_types (id)"
echo "TABLESPACE osmatrix_index;"

echo "CREATE OR REPLACE RULE attribute_types_duplicate_ignore AS ON INSERT"
echo "TO attribute_types"
echo "WHERE (EXISTS (Select 1"
echo "	FROM attribute_types attribute_types_1"
echo "	WHERE attribute_types_1.attribute = new.attribute))"
echo "DO INSTEAD UPDATE attribute_types SET description = new.description"
echo "WHERE attribute_types.attribute = new.attribute;"
