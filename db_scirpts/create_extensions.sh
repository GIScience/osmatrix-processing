#!/bin/bash
echo "/*"
echo "* CREATE EXTENSIONS"
echo "*/"
echo "DROP EXTENSION IF EXISTS hstore, postgis, quantile;"
echo "CREATE EXTENSION hstore;"
echo "CREATE EXTENSION postgis;"
echo "CREATE EXTENSION quantile;"
