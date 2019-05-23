package de.unihd.osmatrix.core;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unihd.osmatrix.core.OSMatrix.TABLE;

// TODO: Auto-generated Javadoc
/**
 * The Class Attribute.
 */
public abstract class Attribute {
	
	/** The type_id. */
	private int type_id;
	
	/** The type. */
	private String type;
	
	/** The values. */
	protected volatile Map<Long,Double> values = new HashMap<Long,Double>(); //long cellenid 
	//maps in 
	//
	
		
	/**
	 * Sets the attribute type id.
	 *
	 * @param type_id the new attribute type id
	 */
	public void setAttributeTypeId(int type_id){
		this.type_id = type_id;
	}
	
	/**
	 * Gets the attribute type id.
	 *
	 * @return the attribute type id
	 */
	public int getAttributeTypeId(){
		return type_id;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
		
	}
	
	/**
	 * Default value.
	 *
	 * @return the double
	 */
	public double defaultValue(){
		return 0.0;
	}
	
	/**
	 * Gets the value.
	 *
	 * @param cell_id the cell_id
	 * @return the value
	 */
	protected double getValue(long cell_id){
		if(values.get(cell_id) == null){
			return defaultValue();
		}
		return values.get(cell_id);
	}
	
	/**
	 * Put value.
	 *
	 * @param cell_id the cell_id
	 * @param value the value
	 */
	protected void putValue(long cell_id, double value){
		values.put(cell_id, value);
	}
	
	
	/**
	 * Need area.
	 *
	 * @param table the table
	 * @return true, if successful
	 */
	protected boolean needArea(OSMatrix.TABLE table){
		return false;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public abstract String getDescription();
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public abstract String getTitle();
	
	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	public abstract List<TABLE> getDependencies();
		
	
	/**
	 * Do update.
	 *
	 * @param table the table
	 * @param cell_id the cell_id
	 * @param old_value the old_value
	 * @param row the row
	 * @return the double
	 * @throws Exception the exception
	 */
	protected abstract double doUpdate(OSMatrix.TABLE table,long cell_id, double old_value,ResultSet row) throws Exception;
	
	/**
	 * Where.
	 *
	 * @param table the table
	 * @return the string
	 */
	protected abstract String where(OSMatrix.TABLE table);
	
	/**
	 * Update.
	 *
	 * @param table the table
	 * @param cell_id the cell_id
	 * @param row the row
	 */
	public void update(OSMatrix.TABLE table,long cell_id, ResultSet row){
		Double last_value = values.get(cell_id);
		if(last_value == null)
			last_value = defaultValue();
			

		try {
			Double new_value = doUpdate(table,cell_id, last_value, row);
			if(last_value.compareTo(new_value) != 0){
				values.put(cell_id, new_value);
			}
		} catch (Exception e) {
			
		}
		
	}
	
	/**
	 * Befor send.
	 */
	protected void beforSend(){
		
	}
	
	
	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public  Map<Long,Double> getValues(){
		beforSend();
		return values;
	}

	
	

}
