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

for type in $(seq 1 25);
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
