package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;
import de.unihd.osmatrix.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class MaxNumberOfModifiedObjectsPerUser.
 */
public class MaxNumberOfModifiedObjectsPerUser extends Attribute {

	/** The cell user contributions. */
	Map<Long, Map<Integer, Long>> cellUserContributions = new HashMap<Long, Map<Integer, Long>>();

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "maxNumberOfModifiedObjectsPerUser";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The maximum number of objects that have been modified by a single user.";

		// die maximale Anzahl an Objekten, die von einen einzelnen User
		// modfiziert wurden - in einer Zelle.
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
	public double defaultValue() {
		return Double.MIN_VALUE;
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
	protected double doUpdate(TABLE table, long cell_id, double old_value,
			ResultSet row) throws SQLException {

				Map<Integer, Long> userContributions = cellUserContributions
				.get(cell_id); // user_id + number of Contributions of that user
		if (userContributions == null) {
			userContributions = new HashMap<Integer, Long>();
		}

//		System.out.println("-----------------------");

		String user = Utils.getUserId(row); // user_id of that row
		Long contribution = null;
		if (user != null) {
			int user_id = Integer.parseInt(user);

			contribution = userContributions.get(user_id);
			contribution = (contribution != null) ? contribution + 1 : 1L;
			userContributions.put(user_id, contribution);

//			System.out.println("MAX: user: " + user_id);
//			System.out.println("MAX: Contributions: " + contribution);
		}

//		System.out.println("MAX: oldvalue: " + old_value);
//		System.out.println("-----------------------");

		cellUserContributions.put(cell_id, userContributions);

		return old_value;
	
	}
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#beforSend()
	 */
	@Override
	protected void beforSend() {

		for (Entry<Long, Map<Integer, Long>> entry : cellUserContributions
				.entrySet()) { // Itrate through hashmap

			Map<Integer, Long> usercon = cellUserContributions.get(entry.getKey());

			if (usercon == null) {
				values.put(entry.getKey(), 0.0);
			} 
			else 
			{
//				long minValueInMap = (Collections.min(usercon.values()));
				Long minValue = Long.MIN_VALUE;
				for (Entry<Integer, Long> users : usercon.entrySet()) {
					if (users.getValue() > minValue) {
						minValue= users.getValue();
						values.put(entry.getKey(), users.getValue()
								.doubleValue());
					}
					else{
						values.put(entry.getKey(), minValue.doubleValue());
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		return Utils.USER_ID + " is not null";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Maximum number of modified objects per user";
	}

}