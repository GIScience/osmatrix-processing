package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;
import de.unihd.osmatrix.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class DateOfLatestEdit.
 */
public class DateOfLatestEdit extends Attribute{
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(DateOfLatestEdit.class);

	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
	public double defaultValue() {
		return Double.MIN_VALUE;
	};
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "DateOfLatestEdit";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The date of the latest edit to any object within the given cell.";
		//Datum des ersten editierten Objekts in einer Zelle.
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
	 */
	@Override
	public List<TABLE> getDependencies() {
		return Arrays.asList(TABLE.POINT, TABLE.LINE, TABLE.POLYGON);
//		return Arrays.asList(TABLE.LINE);
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table, long cell_id, double latest,
			ResultSet row) throws SQLException {

		
			Long time = Utils.getTimeStamp(row);
			if(time == null){
				logger.info("Timestamp is null: "+row.toString());
				return latest;
			}
			if(time > latest)
				latest = time;
		

		return latest;
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		return  Utils.TIMESTAMP+" IS NOT NULL";
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
		return "Date of latest edit";
	}

}