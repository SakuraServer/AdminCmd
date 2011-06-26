/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package com.Balor.files.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Utils {
	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public static Material checkMaterial(String mat) {
		Material m = null;
		try {
			int id = Integer.parseInt(mat);
			m = Material.getMaterial(id);
		} catch (NumberFormatException e) {
			m = Material.matchMaterial(mat);
		}
		return m;

	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @param toParse
	 * @return
	 */
	public static String colorParser(String toParse, String delimiter) {
		String ResultString = null;
		try {
			Pattern regex = Pattern.compile(delimiter + "[0-9]+");
			Matcher regexMatcher = regex.matcher(toParse);
			String result = null;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				result = regexMatcher.replaceFirst(ChatColor.getByCode(
						Integer.parseInt(ResultString.substring(1))).toString());
				regexMatcher = regex.matcher(result);
			}
			return result;
		} catch (Exception ex) {
			return null;
		}

	}

	public static String colorParser(String toParse) {
		return colorParser(toParse, "&");
	}
}