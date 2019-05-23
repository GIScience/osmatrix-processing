package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;

// TODO: Auto-generated Javadoc
/**
 * The Class MaxVersionNumb.
 */
public class MaxVersionNumb extends Attribute{

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "maxVersionNumb";
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The maximum version number of all objects within the given cell.";
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
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
	public double defaultValue() {
		return Double.MIN_VALUE;
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table,long cell_id, double maxVersNumb, 
			ResultSet row) throws NumberFormatException, SQLException {
		
	
			if(row.getString("osm_version") != null){
				Double i = Double.parseDouble(row.getString("osm_version"));
				//Bin mir nicht ganz sicher ob das richtig ist da die Spalte type=TEXT ist
				if(maxVersNumb < i){
					maxVersNumb = i;
				}
			}
		
		return maxVersNumb;
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
		return "Maximum version number";
	}

}