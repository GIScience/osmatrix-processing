package de.unihd.osmatrix.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeSubmitRunnable.
 */
public class TypeSubmitRunnable implements Runnable {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(TypeSubmitRunnable.class);
	
	/** The osmatrix. */
	private DataSource osmatrix;
	
	/** The attribute. */
	private Attribute attribute;
	
	/** The type. */
	private int type;
	
	/** The valid. */
	private int valid;

	/**
	 * Instantiates a new type submit runnable.
	 *
	 * @param osmatrix the osmatrix
	 * @param attribute the attribute
	 * @param type the type
	 * @param valid the valid
	 */
	public TypeSubmitRunnable(DataSource osmatrix, Attribute attribute, int type, int valid) {
		this.osmatrix = osmatrix;
		this.attribute = attribute;
		this.type = type;
		this.valid = valid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(attribute == null || attribute.getValues() == null)
			return;
			
		StringBuilder insert = new StringBuilder("insert into ");
		insert.append("attribute_");
		if(type < 100){
			insert.append("0");
			if(type < 10)
				insert.append("0");
		}
		insert.append(type);
		insert.append(" (cell_id,attribute_type_id,value,valid)");
		insert.append(" values(?,").append(type).append(",?,").append(valid).append(")");
		
		
		Connection conn = null;
		try {
		    conn = osmatrix.getConnection();
		    conn.setAutoCommit(false);
		    
		    PreparedStatement prepStmt = conn.prepareStatement(insert.toString());
		    
		    Map<Long,Double> values = attribute.getValues();
		    
		    Set<Long> cells = values.keySet();
		    int counter = 0;
		    for (Long cell : cells) {
		    	prepStmt.setLong(1,cell);                         
			    prepStmt.setDouble(2,values.get(cell));
			    prepStmt.addBatch();
			    counter++;
			    if(counter == 10000){
			    	counter = 0;
			    	int [] numUpdates=prepStmt.executeBatch(); 
			    	logger.info("Sumited type:"+type+" -> "+numUpdates.length+" rows");
			    	conn.commit();
			    	prepStmt.close();
			    	prepStmt = conn.prepareStatement(insert.toString());
			    }
			}		    

		    int [] numUpdates=prepStmt.executeBatch(); 
		    //TODO check numUpdates for errors!
		    logger.info("Sumited type:"+type+" -> "+numUpdates.length+" rows");
		    conn.commit();
		    prepStmt.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			logger.error(e.getNextException());
		} finally {
		    if (conn != null) {
		        try { conn.close(); } catch (SQLException e) {}
		    }
		}

	}

}
