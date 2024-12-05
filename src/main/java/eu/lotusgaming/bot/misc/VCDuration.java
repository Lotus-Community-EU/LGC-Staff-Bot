//Created by Maurice H. at 17.10.2024
package eu.lotusgaming.bot.misc;

public class VCDuration {
	
	long timeJoin, channelId, memberId, guildId;
	
	public VCDuration(long guildId, long memberId, long channelId, long timeJoin) {
		this.timeJoin = timeJoin;
		this.channelId = channelId;
		this.memberId = memberId;
		this.guildId = guildId;
	}

	public long getTimeJoin() {
		return timeJoin;
	}

	public long getChannelId() {
		return channelId;
	}

	public long getMemberId() {
		return memberId;
	}

	public long getGuildId() {
		return guildId;
	}

}

