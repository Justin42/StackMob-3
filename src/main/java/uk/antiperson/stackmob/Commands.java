package uk.antiperson.stackmob;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import uk.antiperson.stackmob.tools.extras.GlobalValues;

import java.util.UUID;


public class Commands implements CommandExecutor {

    private StackMob sm;

    public Commands(StackMob sm) {
        this.sm = sm;
    }


    private final String noPerm = GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
            "You do not have the permission to perform this command! If you believe this is in error, contact the server administration.";

    @Override
    // the nest of doom.
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        if(sender.hasPermission("StackMob.*") || sender.hasPermission("StackMob.Admin")) {
            if (args.length == 0) {
                sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GOLD + "Plugin commands:");
                sender.sendMessage(ChatColor.AQUA + "/sm spawnstack [size] [entity type] " + ChatColor.GREEN + "Spawns a new pre-stacked entity.");
                sender.sendMessage(ChatColor.AQUA + "/sm remove [radius] " + ChatColor.GREEN + "Removes all of the stacked entities loaded in the specified radius.");
                sender.sendMessage(ChatColor.AQUA + "/sm removeall " + ChatColor.GREEN + "Removes all of the stacked entities loaded.");
                sender.sendMessage(ChatColor.AQUA + "/sm stick " + ChatColor.GREEN + "Gives you the stick of stacking.");
                sender.sendMessage(ChatColor.AQUA + "/sm stats " + ChatColor.GREEN + "Displays entity statistics.");
                sender.sendMessage(ChatColor.AQUA + "/sm reload " + ChatColor.GREEN + "Reloads the configuration file.");
                sender.sendMessage(ChatColor.AQUA + "/sm reset " + ChatColor.GREEN + "Resets the configuration file.");
                sender.sendMessage(ChatColor.AQUA + "/sm check " + ChatColor.GREEN + "Checks for version updates.");
                sender.sendMessage(ChatColor.AQUA + "/sm update " + ChatColor.GREEN + "Downloads the latest version.");
                sender.sendMessage(ChatColor.AQUA + "/sm about " + ChatColor.GREEN + "Shows plugin information.");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("about")) {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GOLD + "StackMob v" + sm.getDescription().getVersion() + " by antiPerson and contributors.");
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.YELLOW + "Find out more at " + sm.getDescription().getWebsite());
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.YELLOW + "Find the source code at " + GlobalValues.GITHUB);
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.YELLOW + "Has this plugin helped your server? Please leave a review!");
                } else if (args[0].equalsIgnoreCase("reset")) {
                    sm.config.getF().delete();
                    sm.config.reloadCustomConfig();
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GREEN + "The configuration has been reset and reloaded.");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    sm.config.reloadCustomConfig();
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GREEN + "The configuration has been reloaded.");
                } else if (args[0].equalsIgnoreCase("removeall")) {
                    int counter = 0;
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getLivingEntities()) {
                            if (entity.hasMetadata(GlobalValues.METATAG)) {
                                counter++;
                                entity.remove();
                            }
                        }
                    }
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GREEN + "A total of " + counter + " entities were removed.");
                } else if (args[0].equalsIgnoreCase("check")) {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GOLD + sm.updater.updateString());
                } else if (args[0].equalsIgnoreCase("update")) {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GOLD + sm.updater.update());
                } else if (args[0].equalsIgnoreCase("stats")) {
                    int stackedCount = 0;
                    int stackedTotal = 0;
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getLivingEntities()) {
                            if (entity.hasMetadata(GlobalValues.METATAG)) {
                                stackedCount = stackedCount + 1;
                                stackedTotal = stackedTotal + entity.getMetadata(GlobalValues.METATAG).get(0).asInt();
                            }
                        }
                    }

                    int stackedCount1 = 0;
                    int stackedTotal1 = 0;
                    if (sender instanceof Player) {
                        for (Entity entity : ((Player) sender).getLocation().getChunk().getEntities()) {
                            if (entity.hasMetadata(GlobalValues.METATAG)) {
                                stackedCount1 = stackedCount1 + 1;
                                stackedTotal1 = stackedTotal1 + entity.getMetadata(GlobalValues.METATAG).get(0).asInt();
                            }
                        }
                    }

                    int cacheTotal = 0;
                    for (UUID uuid : sm.cache.amountCache.keySet()) {
                        if (sm.cache.amountCache.get(uuid) > 0) {
                            cacheTotal = cacheTotal + sm.cache.amountCache.get(uuid);
                        }
                    }


                    sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GOLD + "Entity stacking statistics:");
                    sender.sendMessage(ChatColor.YELLOW + "Loaded entities: " + ChatColor.GREEN + stackedCount + " (" + stackedTotal + " stacked.) "
                            + ChatColor.YELLOW + "Loaded entities (this chunk): " + ChatColor.GREEN + stackedCount1 + " (" + stackedTotal1 + " stacked.) ");
                    sender.sendMessage(ChatColor.YELLOW + "Cached entities: " + ChatColor.GREEN + sm.cache.amountCache.size() + " (" + cacheTotal + " stacked.) ");
                } else if (args[0].equalsIgnoreCase("stick")){
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        sm.stickTools.giveStackingStick(player);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                        player.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.YELLOW + "The stacking stick has been added to your inventory.");
                    } else {
                        sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                "You need to be a player to do this!");
                    }
                } else {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                            "Incorrect command parameters!");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (sender instanceof Player) {
                        try{
                            Integer numb = Integer.valueOf(args[1]);
                            int counter = 0;
                            for (Entity entity : ((Player) sender).getNearbyEntities(numb, numb, numb)) {
                                if (entity.hasMetadata(GlobalValues.METATAG)) {
                                    entity.remove();
                                    counter++;
                                }
                            }
                            sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GREEN + "A total of " + counter + " entities were removed.");
                        } catch (NumberFormatException e){
                            sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                    "Invalid number format!");
                        }
                    } else {
                        sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                "You need to be a player to do this!");
                    }

                } else {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                            "Incorrect command parameters!");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("spawnstack")) {

                    if (sender instanceof Player) {
                        Integer numb;
                        try {
                            numb = Integer.valueOf(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                    "Invalid number format!");
                            return false;
                        }
                        boolean contains = false;
                        for (EntityType type : EntityType.values()) {
                            if (args[2].equalsIgnoreCase(type.toString())) {
                                contains = true;
                            }
                        }
                        if (contains) {
                            Entity newEntity = ((Player) sender).getWorld().spawnEntity(((Player) sender).getLocation(), EntityType.valueOf(args[2].toUpperCase()));
                            newEntity.setMetadata(GlobalValues.NO_SPAWN_STACK, new FixedMetadataValue(sm, true));
                            newEntity.setMetadata(GlobalValues.METATAG, new FixedMetadataValue(sm, numb));
                            sender.sendMessage(GlobalValues.PLUGIN_TAG + ChatColor.GREEN + "Spawned a " + args[2].toUpperCase() + " with a stack size of " + numb + " at your location.");
                        } else {
                            sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                    "Invalid entity type!");
                        }
                    } else {
                        sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                                "You need to be a player to do this!");
                    }
                } else {
                    sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                            "Incorrect command parameters!");
                }
            } else {
                sender.sendMessage(GlobalValues.PLUGIN_TAG + GlobalValues.ERROR_TAG +
                        "Incorrect command parameters!");
            }
        }else{
            sender.sendMessage(noPerm);
        }
        return false;
    }
}
