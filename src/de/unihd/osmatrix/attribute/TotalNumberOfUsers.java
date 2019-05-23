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
 * The Class TotalNumberOfUsers.
 */
public class TotalNumberOfUsers extends Attribute {
	// Map zum zwischen halten von userids Set<Long> und cell_ids Map<Long, ..
	/** The cell users. */
	Map<Long, Set<String>> cellUsers = new HashMap<Long, Set<String>>();
	
	/** The users. */
	Set<String> users = null;
	
	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "totalNumberOfUsersInACell";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The number of users that have edited at least one object within the given cell.";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#defaultValue()
	 */
	@Override
	public double defaultValue() {
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
	 */
	@Override
	public List<TABLE> getDependencies() {
		return Arrays.asList(TABLE.POLYGON, TABLE.LINE, TABLE.POINT);
//		return Arrays.asList(TABLE.LINE);
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		//return Utils.USER_ID + " is not null";
		return "osm_uid is not null";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table, long cell_id, double old_value,
			ResultSet row) throws SQLException {
		//Integer i = Integer.parseInt(cellUsers.get(cell_id));
		users = cellUsers.get(cell_id);
		if (users == null)
			users = new HashSet<String>();

		String user = null;

		user = Utils.getUserId(row);
		if (user == null)
			return old_value;

		users.add(user);
		cellUsers.put(cell_id, users);
		//System.out.println(user+ " " +row.getString("osm_uid")+" "+users.size());
		

		return users.size();
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Number of users";
	}

}