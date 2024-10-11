//Created by Maurice H. at 09.10.2024
package eu.lotusgaming.bot.misc;

public class MCServer {
	
	String servername, displayname, version, requiredJoinlevel, minigameType, minigameMapName, tps;
	int serverId, currentPlayers, currentStaffs, maxPlayers, playerCapacity, ram_usage, ram_alloc, minigameMaxSlots, maxHomes, mapPort;
	boolean isStaffOnly, isOnline, isMonitored, isLocked, isHybrid, isHiddenAPI, isHiddenGame, isMinigame, allowInvSync, hasDynmap, hasJobs;
	long onlineSince, lastUpdated;
	
	public MCServer(String servername, String displayname, String version, String requiredJoinLevel, String mg_type, String mg_MapName, String tps, int serverId, int currentPlayers, int currentStaffs, int maxPlayers, int playerCapacity, int ram_usage, int ram_alloc, int mg_slots, int maxHomes, int mapPort, boolean isStaffOnly, boolean isOnline, boolean isMonitored, boolean isLocked, boolean isHybrid, boolean isHiddenAPI, boolean isHiddenGame, boolean isMG, boolean allowInvSync, boolean hasDynmap, boolean hasJobs, long onlineSince, long lastUpdated) {
		this.servername = servername;
		this.displayname = displayname;
		this.version = version;
		this.requiredJoinlevel = requiredJoinLevel;
		this.minigameType = mg_type;
		this.minigameMapName = mg_MapName;
		this.tps = tps;
		
		this.serverId = serverId;
		this.currentPlayers = currentPlayers;
		this.currentStaffs = currentStaffs;
		this.maxPlayers = maxPlayers;
		this.playerCapacity = playerCapacity;
		this.ram_usage = ram_usage;
		this.ram_alloc = ram_alloc;
		this.minigameMaxSlots = mg_slots;
		this.maxHomes = maxHomes;
		this.mapPort = mapPort;
		
		this.isStaffOnly = isStaffOnly;
		this.isOnline = isOnline;
		this.isMonitored = isMonitored;
		this.isLocked = isLocked;
		this.isHybrid = isHybrid;
		this.isHiddenAPI = isHiddenAPI;
		this.isHiddenGame = isHiddenGame;
		this.isMinigame = isMG;
		this.allowInvSync = allowInvSync;
		this.hasDynmap = hasDynmap;
		this.hasJobs = hasJobs;
		
		this.onlineSince = onlineSince;
		this.lastUpdated = lastUpdated;
	}
	
	public String getServername() {
		return servername;
	}
	public String getDisplayname() {
		return displayname;
	}
	public String getVersion() {
		return version;
	}
	public String getRequiredJoinlevel() {
		return requiredJoinlevel;
	}
	public String getMinigameType() {
		return minigameType;
	}
	public String getMinigameMapName() {
		return minigameMapName;
	}
	public String getTps() {
		return tps;
	}
	public int getServerId() {
		return serverId;
	}
	public int getCurrentPlayers() {
		return currentPlayers;
	}
	public int getCurrentStaffs() {
		return currentStaffs;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public int getPlayerCapacity() {
		return playerCapacity;
	}
	public int getRam_usage() {
		return ram_usage;
	}
	public int getRam_alloc() {
		return ram_alloc;
	}
	public int getMinigameMaxSlots() {
		return minigameMaxSlots;
	}
	public int getMaxHomes() {
		return maxHomes;
	}
	public int getMapPort() {
		return mapPort;
	}
	
	
	public boolean isStaffOnly() {
		return isStaffOnly;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public boolean isMonitored() {
		return isMonitored;
	}
	public boolean isLocked() {
		return isLocked;
	}
	public boolean isHybrid() {
		return isHybrid;
	}
	public boolean isHiddenAPI() {
		return isHiddenAPI;
	}
	public boolean isHiddenGame() {
		return isHiddenGame;
	}
	public boolean isMinigame() {
		return isMinigame;
	}
	public boolean isAllowInvSync() {
		return allowInvSync;
	}
	public boolean isHasDynmap() {
		return hasDynmap;
	}
	public boolean isHasJobs() {
		return hasJobs;
	}

}

