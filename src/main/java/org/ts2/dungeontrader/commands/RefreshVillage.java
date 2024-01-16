package org.ts2.dungeontrader.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class RefreshVillage {
    public static void init() {
        new CommandAPICommand("dungeontraderrefreshvillage")
                .withArguments(new EntitySelectorArgument.OneEntity("entity")) // The arguments
                .withAliases("dtrv", "dungeontraderrv", "dtrefreshvillage")       // Command aliases
                .withPermission(CommandPermission.OP)               // Required permissions
                .executes((sender, args) -> {
                    Entity villaEntity = (Entity) args.get(0);
                    Villager vil = (Villager) villaEntity;

                    String villageName = vil.getPersistentDataContainer().get(NamespacedKey.fromString("village_name"), PersistentDataType.STRING);
                    Location location = vil.getLocation();

                    vil.setHealth(0);
                    SummonVillage.summon(location, villageName);
                })
                .register();
    }
}
