# OSMatrix processing 

## This Repository is no longer maintained!

## Steps will be done once:

### Scripts to Create OSMatrix Schema 

+ Excecute main_schema.sh, mention number of attribute tables you want
    * Such as: 
        * ./main_schema 25

### Processing Data for OSMatrix

#### 1. Downloading

+ Download the planet file from Planet.osm.org you want to process
    + wget http://planet.openstreetmap.org/pbf/full-history/history-latest.osm.pbf

#### 2. Extracting Region/Cutting/Bounding Box

+ Download OSMconvert tool for Operating System you want
    +  Available from http://wiki.openstreetmap.org/wiki/Osmconvert#Download
+ Extracting region (Europe) using OSMConvert command
    * osmconvert planet-file -b=rectangle coordinates (left, bottom, right, top) -o=outputfile
    * osmconvert /data/OSMatrix/2016-09-01_full_history/history-latest.osm.pbf -b=-10.53538,34.66683,38.00957,61.07390 -o=/data/OSMatrix/extracted_regions/europe.pbf

#### 3. Extracting Timestamps after cutting out Region from a OSM history file

+ Download Osmium command line tool and install it from:
    * http://osmcode.org/osmium-tool/
+ Command to filter the timestamps
    + Option 1 (osm2pgsql version 0.9 and above)
        *Extract timestamps as pbf format, because osm2pgsql version 0.9 and above supports the importing of pbf files and provide all extra attributes
    + Option 2 (osm2pgsql versions below 0.9)
        * Importing pbf files does not work with --extra attributes and lacks information such as osm_timestamps etc. 
        * osm.bz2 format does work and provide all extra attributes while importing data, which can be accomplished directly in osmium time-filter command by specifying output file format as osm.bz2 (this way you can avoid converting step for all timestamps)
            * osmium time-filter input-file timestamp YY-MM-DDTHH:MM:SSZ-o output-file (osm, pbf, osm.bz2)
            * osmium-tool-1.3.1/build/osmium time-filter extracted_regions/europe.pbf 2006-01-01T00:00:00Z -o europe_ts/2006-01-01.pbf

#### Note:
##### (Convert files in case if you already have osm or osm.bz2 file formats or want to work with them instead of pbf) 

+ All timestamps need to convert from .pbf to .osm or .osm.bz2 format if extracted as pbf files using OSMConvert tool. 
    * osmconvert input-file.pbf -o=output-file.osm
        * osmconvert /data/OSMatrix/europe_ts/2006-01-01.pbf -o=/data/OSMatrix/europe_ts/2006-01-01.osm
    or 
        * osmconvert /data/OSMatrix/Europe_ts/2006-01-01.pbf | bzip2 > 2006-01-01.osm.bz2

+ Dumping cells table
    * cells_osmatrix_europe.sql file is available in the repository (cells_dump directory) which is created by a pg_dump utility
    * To dump a single table 
        * pg_dump -Fp --data-only -t tablename -d databasename > dumpfile.sql
    * To import/restore
        * psql databasename < dumpfile.sql
        * psql osmatrix3000 < cells_osmatrix_europe.sql


## Steps will be repeated for each timestamp:

#### 4. Importing Data to Database

+ Using osm2pgsql tool
    * osm2pgsql -d databasename -U username -W --hstore-all --style --extra-attributes -C --number-processes input-file
    * osm2pgsql -d osmatrix-DB -U osmatrix -W --hstore-all --style /usr/share/osm2pgsql/default.style --extra-attributes -C 36000 --number-processes 11 /data/OSMatrix/Europe_ts/2006-01-01.pbf

#### 5. Processing

+ Use an executable jar file build from this project (https://gitlab.com/giscience/osmatrix_processing/) with the neccessary attributes
    * java -server -Xms -jar [.jar-file] -oh [host-name] -od [database-name] -ou [username] -op [‘password’] -t -c -f -v [version number, the number to put here is the number of available timestamps + 1] -ts "YYYY-MM-DD HH:MM:SS"
    * java -server -Xms32000m -jar osmatrix_processing_2016-09-16.jar -oh server.name.something.de:5432 -od osmatrix-DB -ou osmatrix -op '' -t 10 -c 1000 -f 1000 -v 1 -ts "2006-01-01 00:00:00"
