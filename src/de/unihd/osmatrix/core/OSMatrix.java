package de.unihd.osmatrix.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.*;
import org.postgresql.ds.PGPoolingDataSource;

/*
 import org.reflections.Reflections;
 import org.reflections.scanners.ResourcesScanner;
 import org.reflections.scanners.SubTypesScanner;
 import org.reflections.scanners.TypeAnnotationsScanner;
 import org.reflections.util.ClasspathHelper;
 import org.reflections.util.ConfigurationBuilder;
 import org.reflections.util.FilterBuilder;
 */

import de.unihd.osmatrix.attribute.*;
import javax.sql.DataSource;

// TODO: Auto-generated Javadoc
/**
 * The Class OSMatrix.
 */
public class OSMatrix {

	/** The Constant logger. */
	private static final Logger logger = Logger.getRootLogger(); //apache log4J

	/**
	 * The Enum TABLE.
	 */
	public enum TABLE { //could be one of those three
		/** The polygon. */
 POLYGON, 
 /** The line. */
 LINE, 
 /** The point. */
 POINT
	}

	/** The Constant NTHREADS. */
	public static final int NTHREADS = 10;
	
	/** The Constant FETCH_SIZE. */
	public static final int FETCH_SIZE = 20; //auf einmal von der db holt 
	
	/** The Constant CHUNK_SIZE. */
	public static final int CHUNK_SIZE = 1000;// jeder chunk für einen thread, damit er nicht alles prozessiert

	/** The executor. */
	private ExecutorService executor = Executors.newFixedThreadPool(NTHREADS); //service zum threads starten
	
	/** The osmatrix. */
	private DataSource osmatrix; //das man einen connection pool erstellen kann, anzahl threads zur db
	
	//welcher attributetyp brauch welche tabelle
	/** The map table type dep. */
	private Map<TABLE, List<String>> mapTableTypeDep = new HashMap<TABLE, List<String>>();
	
	//damit ich attribute object wiederfinden kann
	/** The map type attribute. */
	private Map<String, Attribute> mapTypeAttribute = new HashMap<String, Attribute>();
	
	//id in attribute_type + name
	/** The map typ id. */
	private Map<String, Integer> mapTypId = new HashMap<String, Integer>();

	/** The planet_osm. */
	private Connection planet_osm = null;

	/** The valid. */
	private int valid = -1;

	/** The fetch_size. */
	private int fetch_size = FETCH_SIZE;
	
	/** The chunk_size. */
	private int chunk_size = CHUNK_SIZE;
	
	/** The threads. */
	private int threads = NTHREADS;
	
	/** The commit_interval. */
	private int commit_interval = -1; //wie of er zurück zur db schreiben soll

	/** The planet_osm_props. */
	private Properties planet_osm_props = new Properties(); //username db host etc
	
	/** The osmatrix_props. */
	private Properties osmatrix_props = new Properties();

	/** The initialize. */
	private boolean initialize = false;
	
	public static String timeStampP;
	
	/**
	 * Start.
	 *
	 * @param valid the valid
	 */
	public void start(int valid) { // für welches Datum
		this.valid = valid; 
		if (!init())
			return;
		try {
			doTheWork();
		} finally {
			cleanup();
		}
	}

	/**
	 * Cleanup.
	 */
	@SuppressWarnings("static-access")
	private void cleanup() {
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		logger.info("Finished all threads");

		if (planet_osm != null)
			try {
				planet_osm.close();
			} catch (SQLException e) {
			}
	}
	//db connection
	/**
	 * Inits the.
	 *
	 * @return true, if successful
	 */
	private boolean init() {
		logger.info("Start init");
		try {
			Class.forName("org.postgresql.Driver");
			logger.info("Driver successfully loaded");

			if (!checkInitDB())
				return false;

			PGPoolingDataSource source = new PGPoolingDataSource();
			source.setDataSourceName("OSMatrix Source");
			source.setServerName(osmatrix_props.getProperty("host",
					"localhost:5432"));
			source.setDatabaseName(osmatrix_props.getProperty("database",
					"osmatrix"));
			source.setUser(osmatrix_props.getProperty("user", "test"));
			source.setPassword(osmatrix_props.getProperty("password", "test"));
			source.setMaxConnections(threads); //connection pro thread
			osmatrix = source;

			init_maps();

			StringBuilder sb;
			{
				Set<String> types = mapTypeAttribute.keySet();
				sb = new StringBuilder("\nRegistered Types:");
				for (String type : types) {
					sb.append("\n  ");
					sb.append(type).append(":\t\t\t");
					sb.append(mapTypId.get(type)).append(" - ");
					sb.append(mapTypeAttribute.get(type).getClass().getName());
				}
				logger.debug(sb.toString());
			}
			{
				sb = new StringBuilder("\nTable Dependencies:\n");
				Set<TABLE> tables = mapTableTypeDep.keySet();
				for (TABLE table : tables) {
					sb.append("  ");
					sb.append(table).append(": ");
					List<String> types = mapTableTypeDep.get(table);
					for (String type : types) {
						sb.append(type).append(", ");
					}
					sb.delete(sb.lastIndexOf(","), sb.length());
					sb.append("\n");
				}
				sb.delete(sb.lastIndexOf("\n"), sb.length());
				logger.debug(sb.toString());
			}
			return true;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Init_maps.
	 *
	 * @throws SQLException the sQL exception
	 */
	private void init_maps() throws SQLException {
		Set<String> types = mapTypeAttribute.keySet();

		// IMPORTANT! there should be a rule or trigger an attribute_types
		// which prevents duplicate inserts based on 'attribute'!
		Connection con = osmatrix.getConnection();
		con.setAutoCommit(false);
		PreparedStatement insert = con
				.prepareStatement("insert into attribute_types (attribute,description,title,validfrom) values (?,?,?,?)"); //neue attribute sofern tabellen vorhanden

		for (String type : types) {
			Attribute attr = mapTypeAttribute.get(type);
			if (attr == null) {
				logger.error("attribute[" + type + "] is null!");
				continue;
			}
			insert.setString(1, type);
			insert.setString(2, attr.getDescription());
			insert.setString(3, type);
			insert.setTimestamp(4, Timestamp.valueOf(timeStampP));
			insert.addBatch();
		}
		
		
		
		@SuppressWarnings("unused")
		int[] res = insert.executeBatch();
		insert.close();
		con.commit();

		Statement select = con.createStatement();

		ResultSet rst = select
				.executeQuery("select attribute, id from attribute_types");
		while (rst.next()) {
			mapTypId.put(rst.getString(1), rst.getInt(2));
		}
		rst.close();
		con.close();

	}
	
	/**
	 * Check init db.
	 *
	 * @return true, if successful
	 */
	private boolean checkInitDB() {
		StringBuilder url = new StringBuilder("jdbc:postgresql://");
		url.append(planet_osm_props.getProperty("host", "localhost:5432"));
		url.append("/").append(planet_osm_props.getProperty("database", "osm"));
		Properties props = new Properties();
		props.setProperty("user", planet_osm_props.getProperty("user", "test"));
		props.setProperty("password",
				planet_osm_props.getProperty("password", "test"));

		try {
			System.out.println(url.toString());
			planet_osm = DriverManager.getConnection(url.toString(), props);
			planet_osm.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		}

		return true;
	}

	/**
	 * Do the work.
	 */
	public void doTheWork() {

		if (mapTypeAttribute.size() == 0) {
			logger.info("No registered types!");
			return;
		}

		try {

			Statement st = planet_osm.createStatement();

			st.setFetchSize(fetch_size); // Turn use of the cursor on.

			StringBuilder sql;

			{
				sql = new StringBuilder("select cells.id as cell_id");
				sql.append(" ,osm.*");
				
				
		//		sql.append(" ,st_area(st_intersection(cells.the_geom,osm.way)) as cell_area");
				sql.append(" ,case when (1=2");{
					List<String> attr_types = mapTableTypeDep.get(TABLE.POLYGON);
					if (attr_types != null) {
						for (int i = 0; i < attr_types.size(); ++i) {
							Attribute attr = mapTypeAttribute.get(attr_types
									.get(i));
							if(!attr.needArea(TABLE.POLYGON))
								continue;
							String where = attr.where(TABLE.POLYGON);
							sql.append(" or (").append(where).append(")");
						}
					}
				}
		//		sql.append(") then st_area(st_intersection(cells.geom,osm.way)) else -1.0 end as cell_area ");
				sql.append(") then st_area(st_transform(st_intersection(cells.geom,osm.way),4326)::geography) else -1.0 end as cell_area ");
								
				sql.append(" from planet_osm_polygon osm");
				sql.append(" left join cells on (st_intersects(cells.geom,osm.way))");
				sql.append(" where 1=1");
				sql.append(" and cells.id is not null");
				sql.append(" and st_isvalid(osm.way)");

				sql.append(" and ( 1=2 ");
				{
					List<String> attr_types = mapTableTypeDep.get(TABLE.POLYGON);

					if (attr_types != null) {
						for (int i = 0; i < attr_types.size(); ++i) {
							Attribute attr = mapTypeAttribute.get(attr_types
									.get(i));
							String where = attr.where(TABLE.POLYGON);
							if (where != null && where.trim().length() > 0) {
								sql.append(" or (").append(where).append(")");
							}

						}
					}
				}
				sql.append(")");
				// and ( attr_where or attr2_where)

				// TODO entferne limit
				 //sql.append(" limit 1000000");
				//System.out.println(sql.toString());

				processTable(TABLE.POLYGON, st, sql.toString());

			}

			{
				// TODO LINES
				sql = new StringBuilder("select cells.id as cell_id");
				sql.append(" ,osm.*");
//				sql.append(" ,st_length(st_intersection(cells.geom,osm.way)) as cell_length");
				sql.append(" ,st_length(st_transform(st_intersection(cells.geom,osm.way),4326)::geography) as cell_length");
				sql.append(" from planet_osm_line osm");
				sql.append(" left join cells on (st_intersects(cells.geom,osm.way))");
				sql.append(" where 1=1");
				sql.append(" and cells.id is not null");
				sql.append(" and st_isvalid(osm.way)");

				// sql.append(" limit 10000");

				processTable(TABLE.LINE, st, sql.toString());
			}

			{
				sql = new StringBuilder("select cells.id as cell_id");
				sql.append(" ,osm.*");

				sql.append(" from planet_osm_point osm");
				sql.append(" left join cells on (st_intersects(cells.geom,osm.way))");
				sql.append(" where 1=1");
				sql.append(" and cells.id is not null");
				sql.append(" and st_isvalid(osm.way)");

				// // TODO entferne limit
				// sql.append(" limit 10000");
				//
				processTable(TABLE.POINT, st, sql.toString());
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Process table.
	 *
	 * @param table the table
	 * @param stmt the stmt
	 * @param sql the sql
	 * @throws SQLException the sQL exception
	 */
	private void processTable(TABLE table, Statement stmt, String sql)
			throws SQLException {
		long count = 0;
		//Map<Long, List<Map<String, Object>>> cellRows = null; //Map<String, Object> eine Zeile aus der db String 
		//column name object zb 5 da nicht klar was es ist object
		//Long Wert cellenid mit der Liste für die Zeilen 
		//pro celle pro attribute ein double wert

		//List<Map<String, Object>> list; //um 

		//Map<String, Object> row = null;

		List<Attribute> attributes = new ArrayList<Attribute>();
		List<String> attr_types = mapTableTypeDep.get(table);
		if (attr_types == null) {
			logger.info("no attributes for this table");
			return;
		}
		for (String type : attr_types)
			attributes.add(mapTypeAttribute.get(type)); //alle attribute die interessant sind

		logger.debug(sql);
		ResultSet rs = stmt.executeQuery(sql);
		//ResultSetMetaData meta = rs.getMetaData(); //spaltennamen 

		while (rs.next()) { //hole zeilen  
			
			long cell_id = rs.getInt("cell_id");  
		    for (Attribute attr : attributes) {
		                attr.update(table, cell_id, rs);
		        }
		    count += 1;
		    if(count % chunk_size == 0){ //modulo
		    	System.out.print("\r" + table + ": processed " + count + " rows");
		    }
		    /*
			// Map for storing Cell_id to Database_Rows
			cellRows = new HashMap<Long, List<Map<String, Object>>>();
			for (int i = 0; i < chunk_size; ++i) {
				count++;
				row = new HashMap<String, Object>(meta.getColumnCount());
				for (int j = 1; j <= meta.getColumnCount(); ++j) {
					if (rs.getObject(j) != null
							&& (rs.getObject(j).toString()).trim().length() > 0)
						row.put(meta.getColumnLabel(j), rs.getObject(j));
				}

				long id = (Integer) row.get("cell_id");
				list = cellRows.get(id);
				if (list == null) {
					list = new ArrayList<Map<String, Object>>();
					cellRows.put(id, list);
				}
				list.add(row);
				list = null;
				row = null;
				if (!rs.next())
					break;
			}

			// logger.debug("processing ");
			System.out.print("\r" + table + ": processed " + count + " rows");

			Set<Long> cell_ids = cellRows.keySet();
			for (Long cell_id : cell_ids) {
				List<Map<String, Object>> rows = cellRows.get(cell_id);
				for (Attribute attr : attributes) { //für jede Zelle alle attribute
					attr.update(table, cell_id, rows);
				}
			}
			cellRows.clear();
			*/
		}

		rs.close();
		checkDependenceAndSubmitAttribute(table);

	}

	/**
	 * Check dependence and submit attribute.
	 *
	 * @param table the table
	 */
	private void checkDependenceAndSubmitAttribute(TABLE table) {
		List<String> types = mapTableTypeDep.get(table);
		if (types == null)
			return;
		mapTableTypeDep.remove(table);

		Set<TABLE> dependencies = mapTableTypeDep.keySet();
		List<String> typesToSubmit = new ArrayList<String>(types.size());

		boolean hasAnotherDep = false;
		for (String type : types) {
			hasAnotherDep = false;
			for (TABLE dep_table : dependencies) {
				if (mapTableTypeDep.get(dep_table).contains(type)) {
					hasAnotherDep = true;
					break;
				}
			}
			if (!hasAnotherDep) {
				typesToSubmit.add(type);
			}
		}

		for (String type : typesToSubmit) {
			System.out.println("SubmitType: " + type);
			Attribute attribute = mapTypeAttribute.get(type);
			mapTypeAttribute.remove(type); // not longer required
			Runnable worker = new TypeSubmitRunnable(osmatrix, attribute,
					mapTypId.get(type), valid);
			executor.execute(worker);
		}
	}

	/**
	 * Sets the inits the.
	 *
	 * @param init the new inits the
	 */
	private void setInit(boolean init) {
		this.initialize = init;

	}

	/**
	 * Adds the attribute.
	 *
	 * @param clazz the clazz
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private void addAttribute(Class<? extends Attribute> clazz) //erwarted klassendefiniion die von attribute abgeleitet ist
			throws InstantiationException, IllegalAccessException {

		Attribute attr = clazz.newInstance();
		if (attr != null) {
			String type = attr.getName(); // Attribute class name
			type = type.trim().replace(" ", "_");
			attr.setType(type);

			List<TABLE> dependencies = attr.getDependencies();
			if (type.length() > 0 && !mapTypeAttribute.containsKey(type)) {
				mapTypeAttribute.put(type, attr);
				for (TABLE table : dependencies) {
					List<String> types = mapTableTypeDep.get(table);
					if (types == null) {
						types = new ArrayList<String>();
						mapTableTypeDep.put(table, types);
					}
					types.add(type);
				}
			}
		}
	}

	/**
	 * Sets the fetch size.
	 *
	 * @param fetch_size the new fetch size
	 */
	private void setFetchSize(int fetch_size) {
		this.fetch_size = fetch_size;

	}

	/**
	 * Sets the chunk size.
	 *
	 * @param chunk_size the new chunk size
	 */
	private void setChunkSize(int chunk_size) {
		this.chunk_size = chunk_size;

	}
	
	/**
	 * Sets the number of threads.
	 *
	 * @param threads the new number of threads
	 */
	private void setNumberOfThreads(int threads) {
		this.threads = threads;

	}
	//sets the connection to the OSMatrix database
	/**
	 * Sets the os matrix connection info.
	 *
	 * @param host the host
	 * @param database the database
	 * @param user the user
	 * @param passwd the passwd
	 */
	private void setOSMatrixConnectionInfo(String host, String database,
			String user, String passwd) {
		osmatrix_props.put("host", host);
		osmatrix_props.put("database", database);
		osmatrix_props.put("user", user);
		osmatrix_props.put("password", passwd);

	}
	//sets the connection to the database with the planet_osm
	/**
	 * Sets the osm planet connection info.
	 *
	 * @param host the host
	 * @param database the database
	 * @param user the user
	 * @param passwd the passwd
	 */
	private void setOSMPlanetConnectionInfo(String host, String database,
			String user, String passwd) {
		planet_osm_props.put("host", host);
		planet_osm_props.put("database", database);
		planet_osm_props.put("user", user);
		planet_osm_props.put("password", passwd);

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	@SuppressWarnings("static-access") 
	public static void main(String[] args) { //options start---------------------------------------
		Option help = OptionBuilder.withLongOpt("help")
				.withDescription("Print this help.").create('?');
		Option osm_host = OptionBuilder.hasArg().withArgName("host:port")
				.withDescription("OSM Database host:port")
				.withLongOpt("osm_host").isRequired().create("oh");
		Option osm_db = OptionBuilder.hasArg().withArgName("dbname")
				.withDescription("OSM Database name").withLongOpt("osm_db")
				.isRequired().create("od");
		Option osm_user = OptionBuilder.hasArg().withArgName("user")
				.withDescription("OSM Database username")
				.withLongOpt("osm_user").isRequired().create("ou");
		Option osm_password = OptionBuilder.hasArg().withArgName("password")
				.withDescription("OSM Database password")
				.withLongOpt("osm_passwd").isRequired().create("op");		
		Option timeStampOpt = OptionBuilder.hasArg().withArgName("timeStampOpt")
				.withDescription("the timestamp to be processed")
				.withLongOpt("timestamp").isRequired().create("ts");
		

		Option matrix_host = OptionBuilder.hasArg().withArgName("host:port")
				.withDescription("OSMatrix Database host:port")
				.withLongOpt("matrix_host").create("mh");
		Option matrix_db = OptionBuilder.hasArg().withArgName("dbname")
				.withDescription("OSMatrix Database name")
				.withLongOpt("matrix_db").create("md");
		Option matrix_user = OptionBuilder.hasArg().withArgName("user")
				.withDescription("OSMatrix Database username")
				.withLongOpt("matrix_user").create("mu");
		Option matrix_password = OptionBuilder.hasArg().withArgName("password")
				.withDescription("OSMatrix Database password")
				.withLongOpt("matrix_passwd").create("mp");

		Option threads = OptionBuilder.hasArg().withArgName("num_threads")
				.withDescription("Number of threads. DEFAULT 2")
				.withLongOpt("threads").create('t');
		Option chunk_size = OptionBuilder.hasArg().withArgName("chunk_size")
				.withDescription("Chunk size. DEFAULT[10000]")
				.withLongOpt("chunk_size").create("c");
		Option fetch_size = OptionBuilder.hasArg().withArgName("fetch_size")
				.withDescription("Fetch size. DEFAULT[10000]")
				.withLongOpt("fetch_size").create("f");
		Option init = OptionBuilder.withDescription("Initalization")
				.withLongOpt("init").create('i');
		Option valid = OptionBuilder.hasArg().withArgName("valid_id")
				.withLongOpt("valid_id").isRequired().create("v");
		Option commit = OptionBuilder.hasArg().withArgName("row_count")
				.withLongOpt("commit").create("co");		
		
		
		
		Options options = new Options();
		options.addOption(osm_host);
		options.addOption(osm_db);
		options.addOption(osm_user);
		options.addOption(osm_password);
		options.addOption(matrix_host);
		options.addOption(matrix_db);
		options.addOption(matrix_user);
		options.addOption(matrix_password);

		options.addOption(threads);
		options.addOption(chunk_size);
		options.addOption(fetch_size);
		options.addOption(init);

		options.addOption(valid);
		options.addOption(commit);
		
		options.addOption(timeStampOpt);

		options.addOption(help);

		HelpFormatter formatter = new HelpFormatter();

		OSMatrix osmatrix = new OSMatrix();
		
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			String host = line.getOptionValue(osm_host.getOpt());
			String db = line.getOptionValue(osm_db.getOpt());
			String user = line.getOptionValue(osm_user.getOpt());
			String passwd = line.getOptionValue(osm_password.getOpt());
			
			String time = line.getOptionValue(timeStampOpt.getOpt());
			timeStampP=time;
			
			osmatrix.setOSMPlanetConnectionInfo(host, db, user, passwd);

			host = line.getOptionValue(matrix_host.getOpt(), host);
			db = line.getOptionValue(matrix_db.getOpt(), db);
			user = line.getOptionValue(matrix_user.getOpt(), user);
			passwd = line.getOptionValue(matrix_password.getOpt(), passwd);
												
			osmatrix.setOSMatrixConnectionInfo(host, db, user, passwd);

			osmatrix.setNumberOfThreads(Integer.parseInt(line.getOptionValue(
					threads.getOpt(), "4")));
			osmatrix.setChunkSize(Integer.parseInt(line.getOptionValue(
					chunk_size.getOpt(), "10000"))); //set chunk size, default is 10000
			osmatrix.setFetchSize(Integer.parseInt(line.getOptionValue(
					fetch_size.getOpt(), "10000"))); //set fetch size, default is 10000
			osmatrix.setInit(line.hasOption(init.getOpt()));

			osmatrix.setCommitIntervas(Integer.parseInt(line.getOptionValue(
					commit.getOpt(), "-1")));
//options end ---------------------------------------------------------------------------
			try {
                            /*
                             * Attributes used for OSM Redaction Analysis
                             */
                                osmatrix.addAttribute(AreaLanduse.class);
                                osmatrix.addAttribute(AreaBuildings.class);
                                osmatrix.addAttribute(AreaAmenity.class);
                                osmatrix.addAttribute(AreaLeisure.class);
                                osmatrix.addAttribute(AreaNatural.class);
                                osmatrix.addAttribute(LengthHighwaysMajor.class);
                                osmatrix.addAttribute(LengthHighwaysMinor.class);
                                osmatrix.addAttribute(LengthWaterways.class);
                                osmatrix.addAttribute(LengthRailways.class);
                                osmatrix.addAttribute(LengthBoundary.class);
                                
                                
                            /*
                             * Attribute used for OSMatrix
                             */
				osmatrix.addAttribute(AverageNumberOfAttributes.class);
				osmatrix.addAttribute(AverageNumberOfContributionsPerUser.class);
				osmatrix.addAttribute(AverageNumberOfVersionNumber.class);
				
				osmatrix.addAttribute(DateOfEldestEdit.class);
				osmatrix.addAttribute(DateOfLatestEdit.class);				
				osmatrix.addAttribute(LanduseAllotments.class);
				osmatrix.addAttribute(LanduseCommercial.class);
				
				osmatrix.addAttribute(LanduseFarmland.class);
				osmatrix.addAttribute(LanduseIndustrial.class);
				
				osmatrix.addAttribute(LanduseResidential.class);
				osmatrix.addAttribute(LanduseVineyards.class);
				
				osmatrix.addAttribute(MaxNumberOfAttributes.class);
				osmatrix.addAttribute(MaxNumberOfModifiedObjectsPerUser.class);				
				
				osmatrix.addAttribute(MaxOSMIDAttribute.class);
				osmatrix.addAttribute(MaxVersionNumb.class);
								
				osmatrix.addAttribute(MinNumberOfAttributes.class);
				osmatrix.addAttribute(MinNumberOfModifiedObjectsPerUser.class);
				osmatrix.addAttribute(MinVersionNumb.class);
				
				osmatrix.addAttribute(SumOfAllAttributes.class);
				osmatrix.addAttribute(TotalNumberOfBuildings.class);
				
				osmatrix.addAttribute(TotalNumberOfUsers.class);
				osmatrix.addAttribute(TotalNumbOfFeatures.class);
				
				osmatrix.addAttribute(TotalNumbOfHouseNumb.class);
				osmatrix.addAttribute(TotalNumbOfPOIs.class);
				
				osmatrix.addAttribute(TotalNumberOfSidewalkIncline.class);
				osmatrix.addAttribute(TotalNumberOfSidewalkInformation.class);
				osmatrix.addAttribute(TotalNumberOfSidewalkSmoothness.class);
				osmatrix.addAttribute(TotalNumberOfSidewalkSurface.class);
				osmatrix.addAttribute(TotalNumberOfSidewalkWidth.class);
//				
				// osmatrix.addAttribute(MaxOSMIDAttribute.class);
				// osmatrix.addAttribute(CountRowsAttribute.class);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			osmatrix.start(Integer.parseInt(line.getOptionValue(valid.getOpt(),
					"-1")));

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
			formatter.printHelp("osmatrix [options]", options);
		}

		/*
		 * Reflections reflections = new Reflections(new ConfigurationBuilder()
		 * .filterInputsBy( new FilterBuilder.Include(FilterBuilder
		 * .prefix("de.unihd.osmatrix.attribute")))
		 * .setUrls(ClasspathHelper.forJavaClassPath()) .setScanners(new
		 * SubTypesScanner(), new TypeAnnotationsScanner(), new
		 * ResourcesScanner()));
		 * 
		 * Set<Class<? extends Attribute>> subTypes = reflections
		 * .getSubTypesOf(Attribute.class);
		 * 
		 * for (Class<? extends Attribute> clazz : subTypes) { try {
		 * osmatrix.addAttribute(clreturn old_value;azz); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */


	}

	/**
	 * Sets the commit intervas.
	 *
	 * @param i the new commit intervas
	 */
	private void setCommitIntervas(int i) {
		this.commit_interval = i;
	}

}
