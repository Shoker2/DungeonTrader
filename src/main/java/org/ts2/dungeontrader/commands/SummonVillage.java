package org.ts2.dungeontrader.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ts2.dungeontrader.DungeonTrader;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SummonVillage {
    public static void init() {
        new CommandAPICommand("dungeontradersummonvillage")
                .withArguments(new GreedyStringArgument("name")) // The arguments
                .withAliases("dtsv", "dungeontradersv", "dtsummonvillage")       // Command aliases
                .withPermission(CommandPermission.OP)               // Required permissions
                .executesPlayer((sender, args) -> {
                    Location location = sender.getLocation();
                    String villageName = (String) args.get(0);
                    summon(location, villageName);
                })
                .executesCommandBlock((sender, args) -> {
                    Location location = sender.getBlock().getLocation();
                    String villageName = (String) args.get(0);
                    summon(location, villageName);
                })
                .executesNative((sender, args) -> {
                    Location location = sender.getLocation();
                    String villageName = (String) args.get(0);
                    summon(location, villageName);
                })
                .register();
    }

    public static void summon(Location location, String villageName) {
        String type = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".type");
        String profession = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".profession");
        String level = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".level");

        boolean CanPickUpLoot = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".CanPickUpLoot");
        boolean Glowing = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".Glowing");
        boolean HasVisualFire = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".HasVisualFire");
        boolean Invulnerable = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".Invulnerable");
        boolean NoAI = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".NoAI");
        boolean NoGravity = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".NoGravity");
        boolean PersistenceRequired = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".PersistenceRequired");
        boolean Silent = DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".Silent");

        MemorySection recipesMemorySection = (MemorySection) DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes");
        Set recipesNames = recipesMemorySection.getKeys(false);

        int amountRecipes = getInteger("villagers." + villageName + ".amountRecipes");
        Set selectedRecipesNames = getAmountElements(recipesNames, amountRecipes);

        List<MerchantRecipe> merchantRecipeList = new ArrayList<MerchantRecipe>();
        for (Object recipeNameObj: selectedRecipesNames) {
            String recipeName = (String) recipeNameObj;

            int maxUses = getInteger("villagers." + villageName + ".recipes." + recipeName + ".maxUses");
            String rewardExp = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".rewardExp");

            String buyA_item = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".buyA.item");
            int buyA_count = getInteger("villagers." + villageName + ".recipes." + recipeName + ".buyA.count");
            String buyA_customModelData = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".buyA.customModelData");

            String buyB_item = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".buyB.item");
            int buyB_count = getInteger("villagers." + villageName + ".recipes." + recipeName + ".buyB.count");
            String buyB_customModelData = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".buyB.customModelData");

            String sell_item = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".sell.item");
            int sell_count = getInteger("villagers." + villageName + ".recipes." + recipeName + ".sell.count");
            String sell_customModelData = DungeonTrader.instance.getConfig().getString("villagers." + villageName + ".recipes." + recipeName + ".sell.customModelData");

            ItemStack out = null;

            if (DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".recipes." + recipeName + ".sell.ei_mode") && DungeonTrader.hasExecutableItems) {
                out = DungeonTrader.getExecutableItem(sell_item, sell_count);
            } else {
                out = new ItemStack(Material.valueOf(sell_item.toUpperCase()), sell_count);
            }

            ItemMeta outMeta = out.getItemMeta();

            if (sell_customModelData != null) {
                outMeta.setCustomModelData(Integer.parseInt(sell_customModelData));
            }

            if (DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".sell.tags") != null) {
                MemorySection memorySectionTags = (MemorySection) DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".sell.tags");
                for (String s : memorySectionTags.getKeys(false)) {
                    try {
                        outMeta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.INTEGER, Integer.parseInt(memorySectionTags.get(s).toString()));
                    } catch (java.lang.NumberFormatException e) {
                        outMeta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.STRING, memorySectionTags.get(s).toString());
                    }
                }
            }

            out.setItemMeta(outMeta);

            MerchantRecipe merchantRecipe = new MerchantRecipe(out, maxUses);
            merchantRecipe.setExperienceReward(Boolean.parseBoolean(rewardExp));

            List<ItemStack> itemStackList = new ArrayList<ItemStack>();

            if (buyA_item != null) {
                ItemStack itemStack = null;

                if (DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".recipes." + recipeName + ".buyA.ei_mode") && DungeonTrader.hasExecutableItems) {
                    itemStack = DungeonTrader.getExecutableItem(buyA_item, buyA_count);
                } else {
                    itemStack = new ItemStack(Material.valueOf(buyA_item.toUpperCase()), buyA_count);
                }

                ItemMeta meta = itemStack.getItemMeta();

                if (DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".buyA.tags") != null) {
                    MemorySection memorySectionTags = (MemorySection) DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".buyA.tags");
                    for (String s : memorySectionTags.getKeys(false)) {
                        try {
                            meta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.INTEGER, Integer.parseInt(memorySectionTags.get(s).toString()));
                        } catch (java.lang.NumberFormatException e) {
                            meta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.STRING, memorySectionTags.get(s).toString());
                        }
                    }
                }

                if (buyA_customModelData != null) {
                    meta.setCustomModelData(Integer.parseInt(buyA_customModelData));
                }

                itemStack.setItemMeta(meta);
                itemStackList.add(itemStack);
            }

            if (buyB_item != null) {
                ItemStack itemStack = null;

                if (DungeonTrader.instance.getConfig().getBoolean("villagers." + villageName + ".recipes." + recipeName + ".buyB.ei_mode") && DungeonTrader.hasExecutableItems) {
                    itemStack = DungeonTrader.getExecutableItem(buyB_item, buyB_count);
                } else {
                    itemStack = new ItemStack(Material.valueOf(buyB_item.toUpperCase()), buyB_count);
                }

                ItemMeta meta = itemStack.getItemMeta();

                if (DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".buyB.tags") != null) {
                    MemorySection memorySectionTags = (MemorySection) DungeonTrader.instance.getConfig().get("villagers." + villageName + ".recipes." + recipeName + ".buyB.tags");
                    for (String s : memorySectionTags.getKeys(false)) {
                        try {
                            meta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.INTEGER, Integer.parseInt(memorySectionTags.get(s).toString()));
                        } catch (java.lang.NumberFormatException e) {
                            meta.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.STRING, memorySectionTags.get(s).toString());
                        }
                    }
                }

                if (buyB_customModelData != null) {
                    meta.setCustomModelData(Integer.parseInt(buyB_customModelData));
                }

                itemStack.setItemMeta(meta);
                itemStackList.add(itemStack);
            }


            merchantRecipe.setIngredients(itemStackList);

            merchantRecipeList.add(merchantRecipe);
        }

        Villager vil = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        MemorySection memorySectionTags = (MemorySection) DungeonTrader.instance.getConfig().get("villagers." + villageName + ".Tags");
        for (String s: memorySectionTags.getKeys(false)) {
            vil.getPersistentDataContainer().set(NamespacedKey.fromString(s), PersistentDataType.STRING, memorySectionTags.get(s).toString());
        }

        vil.getPersistentDataContainer().set(NamespacedKey.fromString("village_name"), PersistentDataType.STRING, villageName);

        vil.setProfession(Villager.Profession.valueOf(profession.toUpperCase()));
        vil.setVillagerType(Villager.Type.valueOf(type.toUpperCase()));
        vil.setVillagerLevel(Integer.parseInt(level));

        vil.setCanPickupItems(CanPickUpLoot);
        vil.setGlowing(Glowing);
        vil.setAI(!NoAI);
        vil.setInvulnerable(Invulnerable);
        vil.setVisualFire(HasVisualFire);
        vil.setSilent(Silent);
        vil.setPersistent(PersistenceRequired);
        vil.setGravity(NoGravity);

        vil.setRecipes(merchantRecipeList);
    }

    private static Set getAmountElements(Set elements, int amount) {
        if (amount >= elements.size()) {
            return elements;
        }

        HashSet<String> newElements = new HashSet<String>();
        for (int i=0; i < amount; i++) {
            String randomElement = getRandomKey(elements);
            newElements.add(randomElement);
            elements.remove(randomElement);
        }

        return newElements;
    }

    public static String getRandomKey(Set myHashSet) {
        int size = myHashSet.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;

        String toReturn = "";

        for (Object obj : myHashSet) {
            if (i == item)
                toReturn = obj.toString();
            i++;
        }

        return String.valueOf(toReturn);
    }

    private static int getInteger(String path) {
        int result = 0;

        if (DungeonTrader.instance.getConfig().get(path) == null) {return 0;}

        if (DungeonTrader.instance.getConfig().get(path) instanceof String) {
            result = Integer.parseInt(DungeonTrader.instance.getConfig().getString(path));
        } else if (DungeonTrader.instance.getConfig().get(path) instanceof Integer) {
            result = Integer.parseInt(DungeonTrader.instance.getConfig().getString(path));
        } else {
            MemorySection memorySection = (MemorySection) DungeonTrader.instance.getConfig().get(path);
            result = ThreadLocalRandom.current().nextInt((Integer) memorySection.get("min"), (Integer) memorySection.get("max") + 1);
        }

        return result;
    }
}
