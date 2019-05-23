package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;
import de.unihd.osmatrix.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class AverageNumberOfContributionsPerUser.
 */
public class AverageNumberOfContributionsPerUser extends Attribute {
	// Set<Integer> test = new HashSet<Integer>();
	/** The cell users. */
	Map<Long, Set<String>> cellUsers = new HashMap<Long, Set<String>>();
	
	/** The cell num contributions. */
	Map<Long, Long> cellNumContributions = new HashMap<Long, Long>();

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "averageNumberOfContributionsPerUser";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The average number of objects that have been modified by a single user.";
		// wie viele Objekte wurden in einer Zelle im Durchschnitt pro user
		// modifiziert, erstell etc.
		// Anzahl der Objekte geteilt durch Anzahl der User
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
	protected double doUpdate(TABLE table, long cell_id, double old_value,
			ResultSet row) throws SQLException {
		
		String user = Utils.getUserId(row);

		Long numbOfContributions = cellNumContributions.get(cell_id);
		if (numbOfContributions == null) {
			numbOfContributions = new Long(0);
			// System.out.println("AVERAGE: NULL Contribuions ");
		}

		Set<String> users = cellUsers.get(cell_id);
		if (users == null) {
			users = new HashSet<String>();
		}

		

		users.add(user);
			
		numbOfContributions = numbOfContributions + 1;

		cellUsers.put(cell_id, users);

		cellNumContributions.put(cell_id, numbOfContributions);

		return old_value;
				
	}
		
		/* (non-Javadoc)
		 * @see de.unihd.osmatrix.core.Attribute#beforSend()
		 */
		@Override
		protected void beforSend() {
		
		Set<Long> cells = cellNumContributions.keySet();
		Long numContributions;
		Set<String>numUsers;
		for (Long cell : cells) {
			numContributions = cellNumContributions.get(cell);
			numUsers = cellUsers.get(cell);
			
			double average = numContributions.doubleValue()/ numUsers.size();
			values.put(cell, average);
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
		return "Average number of contributions per user";
	}

}