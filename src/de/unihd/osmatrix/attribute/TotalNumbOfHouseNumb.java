package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;

// TODO: Auto-generated Javadoc
/**
 * The Class TotalNumbOfHouseNumb.
 */
public class TotalNumbOfHouseNumb extends Attribute {
	// int totalNumberOfFeatures = 0;

		/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
		public double defaultValue() {
			return 0.0;
		}

		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#getName()
		 */
		@Override
		public String getName() {
			return "totalNumbOfHouseNumb";
		}

		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The number of house numbers within the given cell.";
		}

		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
		 */
		@Override
		public List<TABLE> getDependencies() {
			return Arrays.asList(TABLE.POLYGON, TABLE.LINE, TABLE.POINT);
		}

		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
		 */
		@Override
		protected double doUpdate(TABLE table, long cell_id, double old_value,
				ResultSet row) throws SQLException {
			String houseNumber = row.getString("addr:housenumber");
			if(houseNumber != null)
				
				return old_value += 1.0;
			
			else			
			return old_value;
			
		}

		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
		 */
		@Override
		protected String where(TABLE table) {
			return "1=1"; // wir interessieren uns fuer alle eintraege
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
			return "Number of house numbers";
		}

}
