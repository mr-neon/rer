/*package me.xneonx.random_enchanted_rewards;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import java.util.logging.Logger;

import dev.aurelium.auraskills.api.event.skill.*;
import dev.aurelium.auraskills.api.skill.*;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.*;
import dev.aurelium.auraskills.api.user.*;

import java.util.*;

public class RandomEnchantPlugin extends JavaPlugin implements Listener {
	
	// Logger instance for the plugin
    private static Logger logger;

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        logger = getLogger();

        // Register events
        //getServer().getPluginManager().registerEvents(new RandomEnchantListener(), this);

        // Print message to console
        getLogger().info(ChatColor.GREEN + "[RandomEnchantPlugin] has been enabled!");
    }

    @EventHandler
    public void onSkillLevelUp(SkillLevelUpEvent event) {
        Player player = event.getPlayer();
        Skill skill = event.getSkill();
        int newLevel = event.getLevel();
        if (skill == Skills.FARMING) { // Check if the skill is Farming
            // Generate a random enchanted hoe based on the skill
            if (newLevel % 10 == 0) { // Every 10 levels of Farming
            	ItemStack hoe = generateRandomEnchantedHoe();
            
            // Give the hoe to the player
            if (hoe != null) {
                player.getInventory().addItem(hoe);
                player.sendMessage("Congratulations! You've received a randomly enchanted Diamond Hoe for reaching level " + newLevel + " in farming.");
            }
        }
    }
    }
        private ItemStack generateRandomEnchantedHoe() {
            ItemStack hoe = new ItemStack(Material.DIAMOND_HOE); // Start with a diamond hoe
            
            Random random = new Random();
            List<Enchantment> applicableEnchantments = getApplicableEnchantments(hoe);

            if (!applicableEnchantments.isEmpty()) {
                for (int i = 0; i < 3; i++) { // Add up to three random applicable enchantments
                    Enchantment randomEnchantment = applicableEnchantments.get(random.nextInt(applicableEnchantments.size()));
                    int randomLevel = random.nextInt(randomEnchantment.getMaxLevel()) + 1;

                    hoe.addEnchantment(randomEnchantment, randomLevel);
                }
            }

            return hoe;
        }

        // Method to get enchantments applicable to a hoe
        private List<Enchantment> getApplicableEnchantments(ItemStack item) {
            List<Enchantment> applicableEnchantments = new ArrayList<>();
            for (Enchantment enchantment : Enchantment.values()) {
                try {
                    if (enchantment.canEnchantItem(item)) {
                        applicableEnchantments.add(enchantment);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Some enchantments may throw IllegalArgumentException, ignore them
                }
            }
            return applicableEnchantments;
        }
    @Override
    public void onDisable() {
        // Print message to console
        logger.info("RandomEnchantPlugin has been disabled!");
    }
}*/

package me.xneonx.random_enchanted_rewards;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import java.util.logging.Logger;

import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;

import java.util.*;

public class RandomEnchantPlugin extends JavaPlugin implements Listener {

    // Logger instance for the plugin
    private static Logger logger;

    // Map to associate each skill with a list of possible items
    private final Map<Skill, List<Material>> skillItemMap = new HashMap<>();
    private final Random random = new Random();

    // Configurable values
    private int weightIron;
    private int weightGold;
    private int weightDiamond;
    private int weightNetherite;
    private int maxEnchantments;
    private double enchantmentLevelIncreaseChance;
    private int levelInterval;

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        logger = getLogger();

        // Save default config
        saveDefaultConfig();

        // Load configuration values
        loadConfigValues();

        // Print message to console
        getLogger().info(ChatColor.GREEN + "[RandomEnchantPlugin] has been enabled!");

        // Initialize skill-item mapping
        initializeSkillItemMap();
    }

    private void loadConfigValues() {
        weightIron = getConfig().getInt("weights.iron", 50);
        weightGold = getConfig().getInt("weights.gold", 30);
        weightDiamond = getConfig().getInt("weights.diamond", 15);
        weightNetherite = getConfig().getInt("weights.netherite", 5);
        maxEnchantments = getConfig().getInt("maxEnchantments", 3);
        enchantmentLevelIncreaseChance = getConfig().getDouble("enchantmentLevelIncreaseChance", 0.3);
        levelInterval = getConfig().getInt("levelInterval", 10);
    }

    private void initializeSkillItemMap() {
        skillItemMap.put(Skills.FARMING, Arrays.asList(
                Material.IRON_HOE, Material.GOLDEN_HOE,
                Material.DIAMOND_HOE, Material.NETHERITE_HOE));
        skillItemMap.put(Skills.MINING, Arrays.asList(
                Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE,
                Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE));
        skillItemMap.put(Skills.FISHING, Collections.singletonList(Material.FISHING_ROD)); // Fishing rods are not tiered
        skillItemMap.put(Skills.EXCAVATION, Arrays.asList(
                Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL,
                Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL));
        skillItemMap.put(Skills.FORAGING, Arrays.asList(
                Material.IRON_AXE, Material.GOLDEN_AXE,
                Material.DIAMOND_AXE, Material.NETHERITE_AXE));
        skillItemMap.put(Skills.ARCHERY, Collections.singletonList(Material.BOW)); // Bows are not tiered
        skillItemMap.put(Skills.FIGHTING, Arrays.asList(
                Material.IRON_SWORD, Material.GOLDEN_SWORD,
                Material.DIAMOND_SWORD, Material.NETHERITE_SWORD));
        // Add other skills and their corresponding items here
    }

    @EventHandler
    public void onSkillLevelUp(SkillLevelUpEvent event) {
        Player player = event.getPlayer();
        Skill skill = event.getSkill();
        int newLevel = event.getLevel();
        
        if (!isSkillEnabled(skill)) {
            return;
        }

        // Check if the level is a multiple of the level interval
        if (newLevel % levelInterval == 0) {
            List<Material> materials = skillItemMap.get(skill);
            if (materials != null) {
                // Select a material based on weighted random distribution
                Material material = selectMaterialByWeight(materials);

                // Generate a random enchanted item based on the skill
                ItemStack enchantedItem = generateRandomEnchantedItem(material);

                // Give the item to the player
                player.getInventory().addItem(enchantedItem);
                player.sendMessage("Congratulations! You've received a randomly enchanted " + material.toString().replace("_", " ").toLowerCase() + " for reaching level " + newLevel + " in " + skill.name().toLowerCase() + ".");
            }
        }
    }

    private boolean isSkillEnabled(Skill skill) {
        return getConfig().getBoolean("skills." + skill.name().toLowerCase() + ".enabled", false);
    }
    private Material selectMaterialByWeight(List<Material> materials) {
        // Define weights based on configuration
        List<Integer> weights = Arrays.asList(weightIron, weightGold, weightDiamond, weightNetherite);

        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (int i = 0; i < materials.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomValue < cumulativeWeight) {
                return materials.get(i);
            }
        }

        return materials.get(0); // Default to the first material if all else fails
    }

    private ItemStack generateRandomEnchantedItem(Material material) {
        ItemStack item = new ItemStack(material); // Start with the specified material

        List<Enchantment> applicableEnchantments = getApplicableEnchantments(item);

        if (!applicableEnchantments.isEmpty()) {
            int numberOfEnchantments = 1 + random.nextInt(maxEnchantments); // Add 1 to maxEnchantments random applicable enchantments
            for (int i = 0; i < numberOfEnchantments; i++) {
                Enchantment randomEnchantment = applicableEnchantments.get(random.nextInt(applicableEnchantments.size()));
                
                // Use chance to increase enchantment level
                int randomLevel = 1;
                while (random.nextDouble() < enchantmentLevelIncreaseChance && randomLevel < randomEnchantment.getMaxLevel()) {
                    randomLevel++;
                }

                item.addUnsafeEnchantment(randomEnchantment, randomLevel);
            }
        }

        return item;
    }

    // Method to get enchantments applicable to an item
    private List<Enchantment> getApplicableEnchantments(ItemStack item) {
        List<Enchantment> applicableEnchantments = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (enchantment.canEnchantItem(item)) {
                    applicableEnchantments.add(enchantment);
                }
            } catch (IllegalArgumentException ignored) {
                // Some enchantments may throw IllegalArgumentException, ignore them
            }
        }
        return applicableEnchantments;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rer")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player && !sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
                    return true;
                }

                reloadConfig();
                loadConfigValues();
                sender.sendMessage(ChatColor.GREEN + "[RandomEnchantPlugin] Configuration reloaded!");
                logger.info("Configuration reloaded by " + sender.getName());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onDisable() {
        // Print message to console
        logger.info("RandomEnchantPlugin has been disabled!");
    }
}
