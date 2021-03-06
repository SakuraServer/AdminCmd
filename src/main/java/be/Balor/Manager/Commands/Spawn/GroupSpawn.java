/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Spawn;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.Tools.Threads.TeleportTask;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class GroupSpawn extends SpawnCommand {

	/**
	 *
	 */
	public GroupSpawn() {
		permNode = "admincmd.spawn.gtp";
		cmdName = "bal_groupspawn";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws PlayerNotFound {
		if (Utils.isPlayer(sender)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			final Player p = (Player) sender;
			if (args.hasFlag('l')) {
				p.sendMessage(ChatColor.GREEN + "Possible Group Names are:");
				String msg = "";
				for (final String gName : ACHelper.getInstance().getGroupList()) {
					msg += gName + ", ";
					if (msg.length() >= ACMinecraftFontWidthCalculator.chatwidth) {
						sender.sendMessage(ChatColor.AQUA + msg);
						msg = "";
					}
				}
				if (!msg.equals("")) {
					if (msg.endsWith(", ")) {
						msg = msg.substring(0, msg.lastIndexOf(","));
					}
					sender.sendMessage(ChatColor.AQUA + msg);
				}
				return;
			}
			if (args.length < 1) {
				final ACWorld w = ACWorld.getWorld(p.getWorld());
				if (w == null) {
					return;
				}
				Location loc = null;
				for (final String gName : ACHelper.getInstance().getGroupList()) {
					final String lcGname = gName.toLowerCase();
					if (PermissionManager.hasPerm(p, "admincmd.respawn."
							+ lcGname)) {
						loc = w.getGroupSpawn(lcGname);
						replace.put("groupName", lcGname);
						break;
					}
				}
				if (loc != null) {
					ACPluginManager.scheduleSyncTask(new TeleportTask(p, loc));
					LocaleHelper.GROUP_SPAWN.sendLocale(p, replace);
					return;
				} else {
					LocaleHelper.NO_GROUP_SPAWN.sendLocale(p, replace);
					return;
				}
			} else if (args.length == 1) {
				final String gName = args.getString(0).toLowerCase();
				if (!ACHelper.getInstance().getGroupList().contains(gName)) {
					replace.put("groupName", gName);
					LocaleHelper.NO_SUCH_GROUP.sendLocale(p, replace);
					return;
				}
				final ACWorld w = ACWorld.getWorld(p.getWorld().getName());
				if (w == null) {
					return;
				}
				Location loc = null;
				if (PermissionManager.hasPerm(p, "admincmd.respawn." + gName,
						true)
						|| PermissionManager.hasPerm(p,
								"admincmd.respawn.admin")) {
					loc = w.getGroupSpawn(gName);
					replace.put("groupName", gName);
					if (loc == null) {
						LocaleHelper.NO_GROUP_SPAWN.sendLocale(p, replace);
						return;
					}
					ACPluginManager.scheduleSyncTask(new TeleportTask(p, loc));
					LocaleHelper.GROUP_SPAWN.sendLocale(p, replace);
				}
			} else if (args.length >= 2) {
				final String gName = args.getString(0).toLowerCase();
				final ACWorld w = ACWorld.getWorld(args.getString(1));
				if (!ACHelper.getInstance().getGroupList().contains(gName)) {
					replace.put("groupName", gName);
					LocaleHelper.NO_SUCH_GROUP.sendLocale(p, replace);
					return;
				}
				if (w == null) {
					return;
				}
				Location loc = null;
				if (PermissionManager.hasPerm(p, "admincmd.respawn." + gName,
						true)
						|| PermissionManager.hasPerm(p,
								"admincmd.respawn.admin")) {
					loc = w.getGroupSpawn(gName);
					replace.put("groupName", gName);
					if (loc == null) {
						LocaleHelper.NO_GROUP_SPAWN.sendLocale(p, replace);
						return;
					}
					ACPluginManager.scheduleSyncTask(new TeleportTask(p, loc));
					LocaleHelper.GROUP_SPAWN.sendLocale(p, replace);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
