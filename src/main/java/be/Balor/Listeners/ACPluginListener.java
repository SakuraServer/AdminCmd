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
package be.Balor.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;

import ru.tehkode.permissions.bukkit.PermissionsEx;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class ACPluginListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(final PluginDisableEvent event) {
		ACPluginManager.unRegisterACPlugin(event.getPlugin());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(final PluginEnableEvent event) {
		if (!PermissionManager.isPermissionsExSet()) {
			final Plugin Permissions = ACPluginManager.getServer()
					.getPluginManager().getPlugin("PermissionsEx");
			if (Permissions != null) {
				if (Permissions.isEnabled()) {
					PermissionManager.setPEX(PermissionsEx
							.getPermissionManager());
				}
			}
		}
		// Removed GroupManager, Permissions, bPermissions, PermissionsBukkit, OddItem, mChatSuite, SignExtensions
		if (InvisibleWorker.dynmapAPI == null) {
			final Plugin plugin = ACPluginManager.getServer()
					.getPluginManager().getPlugin("dynmap");
			if (plugin != null && plugin.isEnabled()) {
				InvisibleWorker.dynmapAPI = (DynmapAPI) plugin;
				ACLogger.info("Successfully linked with Dynmap");
			}
		}
		if (ConfigEnum.H_ALLPLUGIN.getBoolean()) {
			for (final Plugin plugin : event.getPlugin().getServer()
					.getPluginManager().getPlugins()) {
				HelpLister.getInstance().addPlugin(plugin);
			}
		}
	}
}