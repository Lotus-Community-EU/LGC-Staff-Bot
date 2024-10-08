//Created by Maurice H. at 08.10.2024
package eu.lotusgaming.bot.misc;

public class MCPlayer {
	
	String uuid, clan, name, nick, language, playerGroup, countryCode, currentLastServer;
	int lgcid, playtime, sb_state, money_bank, money_pocket, money_interestLevel, killedPlayers, killedEntities, killedByPlayers, killedByEntities;
	boolean isOnline, isStaff, allowTPA, allowPMs;
	long discordSnowflake, firstJoin, lastJoin;
	
	public MCPlayer(String uuid, String clan, String name, String nick, String language, String playerGroup, String countryCode, String currentLastServer, int lgcid, int playtime, int sb_state, int money_bank, int money_pocket, int money_interestLevel, int killedPlayers, int killedEntities, int killedByPlayers, int killedByEntities, boolean isOnline, boolean isStaff, boolean allowTPA, boolean allowPMs, long discordSnowflake, long firstJoin, long lastJoin) {
		this.uuid = uuid;
		this.clan = clan;
		this.name = name;
		this.nick = nick;
		this.language = language;
		this.playerGroup = playerGroup;
		this.countryCode = countryCode;
		this.currentLastServer = currentLastServer;
		
		this.lgcid = lgcid;
		this.playtime = playtime;
		this.sb_state = sb_state;
		this.money_bank = money_bank;
		this.money_pocket = money_pocket;
		this.money_interestLevel = money_interestLevel;
		this.killedPlayers = killedPlayers;
		this.killedEntities = killedEntities;
		this.killedByPlayers = killedByPlayers;
		this.killedByEntities = killedByEntities;
		
		this.isOnline = isOnline;
		this.isStaff = isStaff;
		this.allowPMs = allowPMs;
		this.allowTPA = allowTPA;
		
		this.discordSnowflake = discordSnowflake;
		this.firstJoin = firstJoin;
		this.lastJoin = lastJoin;
	}
	
	public String getUuid() {
		return uuid;
	}
	public String getClan() {
		return clan;
	}
	public String getName() {
		return name;
	}
	public String getNick() {
		return nick;
	}
	public String getLanguage() {
		return language;
	}
	public String getPlayerGroup() {
		return playerGroup;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public String getCurrentLastServer() {
		return currentLastServer;
	}
	public int getLgcid() {
		return lgcid;
	}
	public int getPlaytime() {
		return playtime;
	}
	public int getSb_state() {
		return sb_state;
	}
	public int getMoney_bank() {
		return money_bank;
	}
	public int getMoney_pocket() {
		return money_pocket;
	}
	public int getMoney_interestLevel() {
		return money_interestLevel;
	}
	public int getKilledPlayers() {
		return killedPlayers;
	}
	public int getKilledEntities() {
		return killedEntities;
	}
	public int getKilledByPlayers() {
		return killedByPlayers;
	}
	public int getKilledByEntities() {
		return killedByEntities;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public boolean isStaff() {
		return isStaff;
	}
	public boolean isAllowTPA() {
		return allowTPA;
	}
	public boolean isAllowPMs() {
		return allowPMs;
	}
	public long getDiscordSnowflake() {
		return discordSnowflake;
	}
	public long getFirstJoin() {
		return firstJoin;
	}
	public long getLastJoin() {
		return lastJoin;
	}
}