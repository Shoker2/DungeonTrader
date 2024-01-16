package org.ts2.dungeontrader.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Location;
import org.ts2.dungeontrader.DungeonTrader;

public class Reload {
    public static void init() {
        new CommandAPICommand("dungeontraderreload")
                .withAliases("dtreload")       // Command aliases
                .withPermission(CommandPermission.OP)               // Required permissions
                .executes((sender, args) -> {
                    DungeonTrader.instance.reloadConfig();
                })
                .register();
    }
}
