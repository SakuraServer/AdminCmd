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
package be.Balor.Manager.Commands.Server;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.CoreCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ListValues extends CoreCommand {

	/**
	 * 
	 */
	public ListValues() {
		permNode = "admincmd.server.list";
		cmdName = "bal_list";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		if(args.length==0)
		{
			sender.sendMessage(ChatColor.DARK_AQUA+"Possibles Types :");
			sender.sendMessage(Arrays.toString(Type.values()));
			return;
		}
		String arg = "";
		for (String str : args)
			arg += str + " ";
		arg = arg.trim();
		Set<String> list = ACHelper.getInstance().getUserList(arg);
		if (list != null) {
			sender.sendMessage(ChatColor.AQUA + Type.matchType(arg).display() + ChatColor.WHITE
					+ " (" + list.size() + ") " + ChatColor.AQUA + ":");
			String buffer = "";
			for (String value : list)
				buffer += value + ", ";
			if (!buffer.equals("")) {
				if (buffer.endsWith(", "))
					buffer = buffer.substring(0, buffer.lastIndexOf(","));
				sender.sendMessage(buffer);
			}
		} else
			Utils.sI18n(sender, "emptyList");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
