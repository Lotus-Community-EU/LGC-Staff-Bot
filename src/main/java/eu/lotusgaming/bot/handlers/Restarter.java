//Created by Chris Wille at 15.02.2024
package eu.lotusgaming.bot.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class Restarter extends TimerTask{

	@Override
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date());
		if(time.equals("03:00:00")) {
			System.exit(1);
		}
	}
}