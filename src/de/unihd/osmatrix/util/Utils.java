package de.unihd.osmatrix.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {
	
	
	/** The Constant USER_ID. */
	public static final String USER_ID = "osm_uid";
	
	/** The Constant TIMESTAMP. */
	public static final String TIMESTAMP = "osm_timestamp";

	
	/**
	 * Number of attributes.
	 *
	 * @param row the row
	 * @return the int
	 * @throws SQLException the sQL exception
	 */
	public static int numberOfPredefinedAttributes(ResultSet row) throws SQLException{
		int num = 0 ;
		
		String content;
		
		if((content = row.getString("access")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("addr:housename")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("addr:housenumber")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("addr:interpolation")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("admin_level")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("aerialway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("aeroway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("amenity")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("area")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("barrier")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("bicycle")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("brand")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("bridge")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("boundary")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("building")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("construction")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("covered")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("culvert")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("cutting")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("denomination")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("disused")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("embankment")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("foot")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("generator:source")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("harbour")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("highway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("historic")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("horse")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("intermittent")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("junction")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("landuse")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("layer")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("leisure")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("lock")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("man_made")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("military")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("motorcar")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("name")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("natural")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("oneway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("operator")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("population")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("power")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("power_source")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("place")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("railway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("ref")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("religion")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("route")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("service")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("shop")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("sport")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("surface")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("toll")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("tourism")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("tower:type")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("tracktype")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("tunnel")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("water")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("waterway")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("wetland")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("width")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("wood")) != null && !content.trim().isEmpty())
			num += 1;			
		if((content = row.getString("z_order")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("way_area")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("osm_user")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("osm_uid")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("osm_version")) != null && !content.trim().isEmpty())
			num += 1;
		if((content = row.getString("osm_timestamp")) != null && !content.trim().isEmpty())
			num += 1;
		
		
		return num;
	}
	
	public static int numberOfAllAttributes(ResultSet row) throws SQLException{
		int num = 0 ;
		
		String content;
		if((content = row.getString("tags")) != null && !content.trim().isEmpty()){
			
			for (int i = 0; i < content.length(); i++) {
				if(content.charAt(i) == '=' && content.charAt(i+1) == '>'){
					num +=1;
				}
			
			}				
			num = num -4;
		}
		
		return num;
	}
	
//	public static String getUserId(Map<String, Object> row){
//		String user = (String) row.get(USER_ID);
//		return (String)user;		
//	}
	/**
 * Gets the user id.
 *
 * @param row the row
 * @return the user id
 * @throws SQLException the sQL exception
 */
public static String getUserId(ResultSet row) throws SQLException{
		String user = row.getString(USER_ID);
		return user;		
	}
	
	/**
	 * Gets the time stamp.
	 *
	 * @param row the row
	 * @return the time stamp
	 * @throws SQLException the sQL exception
	 */
	public static Long getTimeStamp(ResultSet row) throws SQLException{
		String text = row.getString(TIMESTAMP);
		if(text == null)
			return null;
		Calendar time = null;
		try{
			time = ISO8601.parse(text);
		}catch(Exception e){
			
		}
		if(time == null)
			return null;

		return time.getTime().getTime();
	}
}
