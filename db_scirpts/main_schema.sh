#!/bin/bash
echo "/*";
echo "* This script produces SQL commands for OSMATRIX_SCHEMA.";
echo "* execute this script, copy text and paste it in sql editor"; 
echo "*/";
echo ""
sh ./create_extensions.sh
echo ""
sh ./attribute_insert.sh
echo ""
sh ./times.sh
echo ""
sh ./cells.sh
echo ""
sh ./cell_types.sh
echo ""
sh ./attribute_types.sh
echo ""
sh ./attributes.sh
echo ""
# Trigger
echo "/*";
echo "* AUTO GENERATED TRIGGER FUNCTION";
echo "*";
echo "* Trigger for 'attributes' main table which redirects inserts into the";
echo "* appropriate 'attribute_xxx' sub tables";
echo "*/";
echo "";

echo "CREATE OR REPLACE FUNCTION attributes_insert_trigger()"
echo "RETURNS TRIGGER AS \$\$"
echo "BEGIN"

for type in $(eval echo {1..$1});
do

if [ $type -lt 10 ]; then
 this="00$type";
elif [ $type -lt 100 ]; then
 this="0$type";
else
 this="$type";

fi
if [ $type -eq 1 ] ; then
echo "    IF ( NEW.attribute_type_id = 1) THEN"
echo "         INSERT INTO attribute_$this VALUES (NEW.*);"
else
echo "    ELSIF ( NEW.attribute_type_id = $type ) THEN"
echo "         INSERT INTO attribute_$this VALUES (NEW.*);"
fi
done
echo "    ELSE"
echo "        RAISE EXCEPTION 'Attribute Type_ID out of range.  Fix the attributes_insert_trigger() function!';"
echo "    END IF;"
echo "    RETURN NULL;"
echo "END;"
echo "\$\$"
echo "LANGUAGE plpgsql;"
echo ""
echo "DROP TRIGGER IF EXISTS insert_attributes_trigger ON attributes;"
echo "CREATE TRIGGER insert_attributes_trigger"
echo "    BEFORE INSERT ON attributes"
echo "    FOR EACH ROW EXECUTE PROCEDURE attributes_insert_trigger();"
echo ""

echo "/*";
echo "* AUTO GENERATED CHILD TABLES OF 'ATTRIBUTES'";
echo "*/";
echo "";

# Partitions

for type in $(eval echo {1..$1});
do
if [ $type -lt 10 ]; then
 this="00$type";
elif [ $type -lt 100 ]; then
 this="0$type";
else
 this="$type";
fi

echo "DROP TABLE IF EXISTS attribute_$this;"
echo "CREATE TABLE attribute_$this ("
echo "CONSTRAINT pk_attribute_$this PRIMARY KEY (id),"
echo 'UNIQUE (attribute_type_id, cell_id, expired, valid),'
echo "CHECK  (attribute_type_id = $type)"
echo ')INHERITS(attributes)'
echo "TABLESPACE osmatrix_data;"
echo "CREATE INDEX id_index_attribute_$this ON attribute_$this (id)"
echo "TABLESPACE osmatrix_index;"
echo "DROP TRIGGER IF EXISTS attribute_insert_trigger ON attribute_$this;"
echo 'CREATE TRIGGER attribute_insert_trigger'
echo '  BEFORE INSERT'
echo "  ON attribute_$this FOR EACH ROW"
echo '  EXECUTE PROCEDURE attribute_insert();'
echo ""
done


#*******************************************************