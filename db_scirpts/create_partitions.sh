#!/bin/bash

echo "/*";
echo "* AUTO GENERATED CHILD TABLES OF 'ATTRIBUTES'";
echo "*/";
echo "";


## Partitions
for type in $(seq 1 25);
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
echo ')INHERITS(attributes);'
echo "DROP TRIGGER IF EXISTS attribute_insert_trigger ON attribute_$this;"
echo 'CREATE TRIGGER attribute_insert_trigger'
echo '  BEFORE INSERT'
echo "  ON attribute_$this FOR EACH ROW"
echo '  EXECUTE PROCEDURE attribute_insert();'
echo ""
done
