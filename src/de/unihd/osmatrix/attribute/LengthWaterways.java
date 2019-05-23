package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;


// TODO: Auto-generated Javadoc
/**
 * The Class LanduseIndustrial.
 */
public class LengthWaterways extends Attribute{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LanduseIndustrial.class);
	
	/** The values. */
	Set<String> values = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "length_waterways";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "The length of waterways given in meters.";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
	 */
	@Override
	public List<TABLE> getDependencies() {
		// TODO Auto-generated method stub
		return Arrays.asList(TABLE.LINE);
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table, long cell_id, double area,
			ResultSet row) throws SQLException {
		String waterway = row.getString("waterway");
		
		if(waterway != null) area += row.getDouble("cell_length");
		
                return area;

	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		if(table == TABLE.POLYGON)
			return "waterway IS NOT null";
		return null;
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
		return "Length of waterways";
	}

}