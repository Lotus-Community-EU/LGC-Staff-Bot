//Created by Chris Wille at 11.02.2024
package eu.lotusgaming.bot.misc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class MiscUtils {
	
	public static long public_guild = 1153419306789507125l;
	public static long staff_guild = 1066812641768640542l;
	
	public static String retDate(OffsetDateTime odt, String pattern) {
		return odt.format(DateTimeFormatter.ofPattern(pattern));
	}

}

