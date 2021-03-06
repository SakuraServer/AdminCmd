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
package be.Balor.JUnit.Egg;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Tools.Egg.EggType;
import be.Balor.Tools.Egg.Exceptions.ProcessingArgsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
@SuppressWarnings("serial")
public class TestEgg extends EggType<Boolean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#onEvent(org.bukkit.event.player.
	 * PlayerEggThrowEvent)
	 */
	@Override
	public void onEvent(final PlayerEggThrowEvent event) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Tools.Egg.EggType#processArguments(be.Balor.Manager.Commands
	 * .CommandArgs)
	 */
	@Override
	protected void processArguments(final Player player, final CommandArgs args)
			throws ProcessingArgsException {
		if (args.hasFlag('t')) {
			value = true;
		} else {
			value = false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Tools.Egg.EggType#checkPermission(org.bukkit.entity.Player)
	 */
	@Override
	protected boolean checkPermission(final Player player) {
		return true;
	}

}
