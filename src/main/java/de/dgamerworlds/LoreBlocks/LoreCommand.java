package de.dgamerworlds.LoreBlocks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class LoreCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "LoreBlockSaver | Only Players can use this Command."));
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("lore") && !player.hasPermission("lbs.lore")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermissions &f| &cYou do not have permission to."));
            return true;
        }
        if (command.getName().equalsIgnoreCase("rename") && !player.hasPermission("lbs.rename")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermissions &f| &cYou do not have permission to."));
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5LoreBlockSaver &f| &cThis ist not a Item."));
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (command.getName().equalsIgnoreCase("lore")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/lore add/remove/set/clear ..."));
                return true;
            }
            String sub = args[0].toLowerCase();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            switch (sub) {
                case "add":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input!"));
                        return true;
                    }
                    String addText = ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    lore.add(addText);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &aLore added."));
                    break;
                case "remove":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input!"));
                        return true;
                    }
                    try {
                        int line = Integer.parseInt(args[1]) - 1;
                        if (line < 0 || line >= lore.size()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input."));
                            return true;
                        }
                        lore.remove(line);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &aLore removed."));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input."));
                    }
                    break;
                case "set":
                    if (args.length < 3) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input!"));
                        return true;
                    }
                    try {
                        int line = Integer.parseInt(args[1]) - 1;
                        if (line < 0 || line >= lore.size()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &cInvalid Input!"));
                            return true;
                        }
                        String setText = ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                        lore.set(line, setText);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &aLore changed."));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&&aLore &f| &cInvalid Input!"));
                    }
                    break;
                case "clear":
                    meta.setLore(new ArrayList<>());
                    item.setItemMeta(meta);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &aLore removed."));
                    break;
                default:
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/lore add/remove/set/clear ..."));
                    break;
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("rename")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/rename TEXT"));
                return true;
            }
            String name = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLore &f| &aUnnamed."));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("lore")) {
            if (args.length == 1) {
                return Arrays.asList("add", "remove", "set", "clear");
            }
        }
        return new ArrayList<>();
    }
}
