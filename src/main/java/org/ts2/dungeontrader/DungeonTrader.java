package org.ts2.dungeontrader;

import com.ssomar.score.SCore;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ts2.dungeontrader.commands.RefreshVillage;
import org.ts2.dungeontrader.commands.Reload;
import org.ts2.dungeontrader.commands.SummonVillage;

import java.util.*;

import static javax.swing.Action.NAME;

public final class DungeonTrader extends JavaPlugin {
    public static Plugin instance;
    public static boolean hasExecutableItems = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.instance = this;

        Plugin executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
        if(executableItems != null && executableItems.isEnabled()) {
            System.out.println("[ExecutableItems] ExecutableItems hooked !");
            hasExecutableItems = true;
        }

        SummonVillage.init();
        Reload.init();
        RefreshVillage.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ItemStack getExecutableItem(String executableItemId, int amount){
        ItemStack item = null;
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(executableItemId);
        if(eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(amount, Optional.empty());
        }

        return item;
    }
}
