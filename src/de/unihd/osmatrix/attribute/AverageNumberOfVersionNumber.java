package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;
import de.unihd.osmatrix.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class AverageNumberOfVersionNumber.
 */
public class AverageNumberOfVersionNumber extends Attribute {

/** The Constant logger. */
private static final Logger logger = Logger.getLogger(AverageNumberOfAttributes.class);
	
	
	/** The cell version numb. */
	Map<Long, Double> cellVersionNumb = new HashMap<Long, Double>();
	
	/** The cell feature numb. */
	Map<Long, Long> cellFeatureNumb = new HashMap<Long,Long>();
//	Map<Long, Map<Long,Double>> cellNumbFreatureVersionNumb = new HashMap <Long, Map<Long,Double>>();

	/* (non-Javadoc)
 * @see de.unihd.osmatrix.core.Attribute#getName()
 */
@Override
	public String getName() {
		return "AverageNumberOfVersionNumber";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The average version number of all objects within the given cell.";
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
	public double defaultValue() {
		return 1.0;
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
	 */
	@Override
	public List<TABLE> getDependencies() {
		return Arrays.asList(TABLE.POINT,TABLE.LINE,TABLE.POLYGON);
//		return Arrays.asList(TABLE.LINE);
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table, long cell_id, double oldvalue,
			ResultSet row) throws SQLException {
	    
		
		Long featureNumb = cellFeatureNumb.get(cell_id);
		if (featureNumb == null){
			featureNumb = new Long(1);
		}
		
		else {
			featureNumb = featureNumb +1;
		}
		
		Double versionNumb = cellVersionNumb.get(cell_id);
		if (versionNumb == null){
			versionNumb = 1.0;
		}
		
		else {
			if(row.getString("osm_version") == null){
			versionNumb= defaultValue();
			}
			else{
				Double i = Double.parseDouble(row.getString("osm_version"));
				versionNumb = versionNumb +i; 
			}
				
		}
		
		cellFeatureNumb.put(cell_id, featureNumb);
		cellVersionNumb.put(cell_id, versionNumb);
		
	    
		return oldvalue;
		}
	

	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#beforSend()
	 */
	@Override
	protected void beforSend() {
		
		Set<Long> cells = cellFeatureNumb.keySet();
		Long numFeat;
		Double numVersion;
		for (Long cell : cells) {
			numFeat = cellFeatureNumb.get(cell);
			numVersion = cellVersionNumb.get(cell);
			values.put(cell, numVersion/numFeat.doubleValue());
		}

	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		return "osm_version IS NOT NULL"; 
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#needArea(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected boolean needArea(TABLE table) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Average version number";
	}


}
