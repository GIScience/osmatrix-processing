package de.unihd.osmatrix.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unihd.osmatrix.core.Attribute;
import de.unihd.osmatrix.core.OSMatrix.TABLE;
import de.unihd.osmatrix.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class AverageNumberOfAttributes.
 */
public class AverageNumberOfAttributes extends Attribute {

	/** The Constant logger. */
	private static final Logger logger = Logger
			.getLogger(AverageNumberOfAttributes.class);

	/** The cell feature. */
	Map<Long, Long> cellFeature = new HashMap<Long, Long>();
	
	/** The cell num attributes. */
	Map<Long, Long> cellNumAttributes = new HashMap<Long, Long>();

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getName()
	 */
	@Override
	public String getName() {
		return "AverageNumbAttr";
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDescription()
	 */
	@Override
	public String getDescription() {
		return "The average number of attributes attached to any object within the given cell.";
		// hier soll die durchschnittliche Anzahl von Attributen die mit den
		// nodes und ways in einer Zelle verbunden sind
		// berechnet werden. Also alle Attribute die nicht null sind.
		// anzahl der Attribute in einer Zelle geteilt durch die Anzahl der
		// nodes und ways.
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#getDependencies()
	 */
	@Override
	public List<TABLE> getDependencies() {
		return Arrays.asList(TABLE.POINT, TABLE.LINE, TABLE.POLYGON);
		// return Arrays.asList(TABLE.LINE);
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#doUpdate(de.unihd.osmatrix.core.OSMatrix.TABLE, long, double, java.sql.ResultSet)
	 */
	@Override
	protected double doUpdate(TABLE table, long cell_id, double numb,
			ResultSet row) throws SQLException {
		Long numFeat = cellFeature.get(cell_id);
		if (numFeat == null)
			numFeat = new Long(0);
		else {
			numFeat += 1;
		}
		cellFeature.put(cell_id, numFeat);

		Long numAttr = cellNumAttributes.get(cell_id);
		if (numAttr == null) {
			numAttr = new Long(0);
		}
		numAttr += Utils.numberOfAttributes(row);
		cellNumAttributes.put(cell_id, numAttr);

		return numb;
	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#beforSend()
	 */
	@Override
	protected void beforSend() {

		Set<Long> cells = cellFeature.keySet();
		Long numFeat, numAttr;
		for (Long cell : cells) {

			numFeat = cellFeature.get(cell);
			if (numFeat == null) {
				numFeat = new Long(0);
			}

			numAttr = cellNumAttributes.get(cell);
			if (numAttr == null || numAttr == 0) {
				numAttr = new Long(-1);
			}

			values.put(cell, numFeat.doubleValue()/numAttr.doubleValue());
		}

	}

	/* (non-Javadoc)
	 * @see de.unihd.osmatrix.core.Attribute#where(de.unihd.osmatrix.core.OSMatrix.TABLE)
	 */
	@Override
	protected String where(TABLE table) {
		return "1=1"; // alle Zeilen
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
		return "Average number of attributes";
	}
}