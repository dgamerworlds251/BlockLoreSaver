package de.dgamerworlds.LoreBlocks;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoreBlockSaver extends JavaPlugin implements Listener {
    private Map<String, BlockData> blockDataMap;
    private File dataFile;
    private YamlConfiguration dataConfig;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("LoreBlockSaver Activated!");
        blockDataMap = new HashMap<>();
        dataFile = new File(getDataFolder(), "blockdata.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try { dataFile.createNewFile(); } catch (IOException ignored) {}
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadBlockData();
    }

    @Override
    public void onDisable() {
        saveBlockData();
    }

    private String getBlockKey(Block block) {
        return block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            Block block = e.getBlockPlaced();
            String key = getBlockKey(block);
            List<Component> loreComponents = meta.lore();
            List<String> lore = loreComponents != null ? loreComponents.stream().map(c -> c != null ? PlainTextComponentSerializer.plainText().serialize(c) : null).collect(Collectors.toList()) : null;
            Component displayNameComponent = meta.displayName();
            String name = displayNameComponent != null ? PlainTextComponentSerializer.plainText().serialize(displayNameComponent) : null;
            blockDataMap.put(key, new BlockData(lore, name, block.getType().name()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        String key = getBlockKey(block);
        if (blockDataMap.containsKey(key)) {
            e.setDropItems(false);
            BlockData data = blockDataMap.get(key);
            ItemStack drop = new ItemStack(block.getType());
            ItemMeta meta = drop.getItemMeta();
            if (data.lore != null) {
                meta.setLore(data.lore); // Setzt die Lore als List<String> (Legacy)
            }
            if (data.name != null) {
                meta.displayName(Component.text(data.name));
            }
            drop.setItemMeta(meta);
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
            blockDataMap.remove(key);
        }
    }

    private void loadBlockData() {
        for (String key : dataConfig.getKeys(false)) {
            String type = dataConfig.getString(key + ".type");
            List<String> lore = dataConfig.getStringList(key + ".lore");
            String name = dataConfig.getString(key + ".name");
            blockDataMap.put(key, new BlockData(lore.isEmpty() ? null : lore, name, type));
        }
    }

    private void saveBlockData() {
        for (String key : blockDataMap.keySet()) {
            BlockData data = blockDataMap.get(key);
            dataConfig.set(key + ".type", data.type);
            dataConfig.set(key + ".lore", data.lore);
            dataConfig.set(key + ".name", data.name);
        }
        try { dataConfig.save(dataFile); } catch (IOException ignored) {}
    }

    public static class BlockData {
        public List<String> lore;
        public String name;
        public String type;
        public BlockData(List<String> lore, String name, String type) {
            this.lore = lore;
            this.name = name;
            this.type = type;
        }
    }
}
