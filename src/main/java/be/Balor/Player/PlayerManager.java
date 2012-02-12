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
package be.Balor.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.DebugLog;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerManager {
	private ConcurrentMap<String, ACPlayer> players = new MapMaker().concurrencyLevel(8)
			.weakValues().makeMap();
	private ConcurrentMap<ACPlayer, Boolean> onlinePlayers = new MapMaker().concurrencyLevel(8)
			.makeMap();
	private final static PlayerManager instance = new PlayerManager();
	private IPlayerFactory playerFactory;

	/**
	 * 
	 */
	private PlayerManager() {
		EmptyPlayer console = new EmptyPlayer("serverConsole");
		onlinePlayers.put(console, true);
		addPlayer(console);
	}

	/**
	 * @return the instance
	 */
	public static PlayerManager getInstance() {
		return instance;
	}

	/**
	 * @param playerFactory
	 *            the playerFactory to set
	 */
	public void setPlayerFactory(IPlayerFactory playerFactory) {
		if (this.playerFactory == null)
			this.playerFactory = playerFactory;
	}

	/**
	 * Convert the ACPlayer
	 * 
	 * @param playerFactory
	 */
	public void convertFactory(IPlayerFactory factory) {
		for (String name : this.playerFactory.getExistingPlayers()) {
			ACPlayer oldPlayer = playerFactory.createPlayer(name);
			ACPlayer newPlayer = factory.createPlayer(name);
			newPlayer.setLastLocation(oldPlayer.getLastLocation());
			newPlayer.setPresentation(oldPlayer.getPresentation());

			for (String home : oldPlayer.getHomeList())
				newPlayer.setHome(home, oldPlayer.getHome(home));
			for (Entry<String, String> entry : oldPlayer.getPowers().entrySet()) {
				Type power = Type.matchType(entry.getKey());
				if (power != null)
					newPlayer.setPower(power, oldPlayer.getPower(power));
				else
					newPlayer.setCustomPower(entry.getKey(),
							oldPlayer.getCustomPower(entry.getKey()));
			}
			for (String info : oldPlayer.getInformationsList())
				newPlayer.setInformation(info, oldPlayer.getInformation(info).getObj());
			for (String kit : oldPlayer.getKitUseList())
				newPlayer.setLastKitUse(kit, oldPlayer.getLastKitUse(kit));
			newPlayer.forceSave();
		}
		this.playerFactory = factory;
	}

	/**
	 * Add a new player
	 * 
	 * @param player
	 */
	private synchronized boolean addPlayer(ACPlayer player) {
		final String name = player.getName();
		if (name == null) {
			throw new NullPointerException();
		}

		ACPlayer ref = players.get(name);
		if (ref != null)
			return false;
		players.put(name, player);
		return true;
	}

	/**
	 * Return online AC players
	 * 
	 * @return
	 */
	public List<ACPlayer> getOnlineACPlayers() {
		return new ArrayList<ACPlayer>(onlinePlayers.keySet());
	}

	/**
	 * Get Online Bukkit Player
	 * 
	 * @return
	 */
	public List<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<Player>(onlinePlayers.size());
		for (ACPlayer p : onlinePlayers.keySet()) {
			Player handler = p.getHandler();
			if (handler != null)
				list.add(handler);
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted custom power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(String power) {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (ACPlayer p : getExistingPlayers()) {
			if (p.hasCustomPower(power))
				list.add(p);
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(Type power) {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (ACPlayer p : getExistingPlayers()) {
			if (p.hasPower(power))
				list.add(p);
		}
		return list;
	}

	private List<ACPlayer> getExistingPlayers() {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (String name : playerFactory.getExistingPlayers()) {
			ACPlayer player = demandACPlayer(name);
			if (!(player instanceof EmptyPlayer))
				list.add(player);
		}
		return list;
	}

	/**
	 * Get the wanted player
	 * 
	 * @param name
	 *            name of the player
	 * @return the ACPlayer if found, else null
	 */
	private synchronized ACPlayer getPlayer(String name) {
		ACPlayer result = players.get(name);
		if (result != null)
			result.reloadHandler();
		return result;
	}

	/**
	 * Set Offline an online player. The player will lost his strong reference,
	 * when the gc will be called, the reference will be deleted.
	 * 
	 * @param player
	 *            player to setOffline
	 * @return
	 */
	public boolean setOffline(ACPlayer player) {
		player.updatePlayedTime();
		player.forceSave();
		player.setOnline(false);
		return onlinePlayers.remove(player) != null;
	}

	public ACPlayer setOnline(Player player) {
		playerFactory.addExistingPlayer(player.getName());
		ACPlayer acPlayer = demandACPlayer(player);
		onlinePlayers.put(acPlayer, true);
		DebugLog.INSTANCE.info(player.getName() + " is put online.");
		return acPlayer;
	}

	ACPlayer demandACPlayer(String name) {
		if (name == null)
			return getPlayer("serverConsole");
		ACPlayer result = getPlayer(name);
		if (result == null) {
			result = playerFactory.createPlayer(name);
			addPlayer(result);
			result = getPlayer(name);
		} else if (result instanceof EmptyPlayer) {
			ACPlayer tmp = playerFactory.createPlayer(name);
			if (tmp instanceof EmptyPlayer)
				return result;
			players.remove(name);
			onlinePlayers.remove(result);
			result = tmp;
			addPlayer(result);
			result = getPlayer(name);
		}
		return result;
	}

	ACPlayer demandACPlayer(Player player) {
		if (player == null)
			return getPlayer("serverConsole");
		String playerName = player.getName();
		ACPlayer result = getPlayer(playerName);
		if (result == null) {
			result = playerFactory.createPlayer(player);
			addPlayer(result);
			result = getPlayer(playerName);
		} else if (result instanceof EmptyPlayer) {
			ACPlayer tmp = playerFactory.createPlayer(playerName);
			if (tmp instanceof EmptyPlayer)
				return result;
			players.remove(playerName);
			onlinePlayers.remove(result);
			result = tmp;
			addPlayer(result);
			result = getPlayer(playerName);
		}
		return result;
	}

}
