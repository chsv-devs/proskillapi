/**
 * SkillAPI
 * com.sucy.skill.data.Settings
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sucy.party.Parties;
import com.sucy.party.Party;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.CombatProtection;
import com.sucy.skill.api.DefaultCombatProtection;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.cast.IndicatorSettings;
import com.sucy.skill.data.formula.Formula;
import com.sucy.skill.data.formula.value.CustomValue;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.gui.tool.GUITool;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.log.Logger;
import lombok.Getter;
import lombok.experimental.Accessors;
import mc.promcteam.engine.mccore.config.CommentedConfig;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.mccore.config.parse.NumberParser;
import mc.promcteam.engine.mccore.util.TextFormatter;
import mc.promcteam.engine.mccore.util.VersionManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

/**
 * <p>The management class for SkillAPI's config.yml settings.</p>
 */
public class Settings {

    private static final String GUI_BASE = "GUI.";
    private static final String
                                GUI_OLD  = GUI_BASE + "old-health-bar",
            GUI_FORCE                    = GUI_BASE + "force-scaling",
            GUI_LVLBAR                   = GUI_BASE + "level-bar",
            GUI_FOOD                     = GUI_BASE + "food-bar",
            GUI_ACTION                   = GUI_BASE + "use-action-bar",
            GUI_TEXT                     = GUI_BASE + "action-bar-text",
            GUI_BOARD                    = GUI_BASE + "scoreboard-enabled",
            GUI_NAME                     = GUI_BASE + "show-class-name",
            GUI_LEVEL                    = GUI_BASE + "show-class-level",
            GUI_BINDS                    = GUI_BASE + "show-binds",
            GUI_BIND_TEXT                = GUI_BASE + "show-bind-text",
            GUI_LVLTXT                   = GUI_BASE + "class-level-text",
            GUI_TITLE                    = GUI_BASE + "title-enabled",
            GUI_DUR                      = GUI_BASE + "title-duration",
            GUI_FADEI                    = GUI_BASE + "title-fade-in",
            GUI_FADEO                    = GUI_BASE + "title-fade-out",
            GUI_LIST                     = GUI_BASE + "title-messages",
            GUI_CUSTOMMODELDATA          = GUI_BASE + "use-custommodeldata",

    DEFAULT_YIELD                  = "default",
            ACCOUNT_BASE           = "Accounts.",
            ACCOUNT_MAIN           = ACCOUNT_BASE + "main-class-group",
            ACCOUNT_EACH           = ACCOUNT_BASE + "one-per-class",
            ACCOUNT_MAX            = ACCOUNT_BASE + "max-accounts",
            ACCOUNT_PERM           = ACCOUNT_BASE + "perm-accounts",
            TARGET_BASE            = "Targeting.",
            TARGET_MONSTER         = TARGET_BASE + "monsters-enemy",
            TARGET_PASSIVE         = TARGET_BASE + "passive-ally",
            TARGET_PLAYER          = TARGET_BASE + "player-ally",
            TARGET_PARTIES         = TARGET_BASE + "parties-ally",
            TARGET_NPC             = TARGET_BASE + "affect-npcs",
            TARGET_STANDS          = TARGET_BASE + "affect-armor-stands",
            SAVE_BASE              = "Saving.",
            SAVE_AUTO              = SAVE_BASE + "auto-save",
            SAVE_MINS              = SAVE_BASE + "minutes",
            SAVE_SQL               = SAVE_BASE + "sql-database",
            SAVE_SQLD              = SAVE_BASE + "sql-details",
            CLASS_BASE             = "Classes.",
            CLASS_MODIFY           = CLASS_BASE + "modify-health",
            CLASS_HP               = CLASS_BASE + "classless-hp",
            CLASS_SHOW             = CLASS_BASE + "show-auto-skills",
            CLASS_ATTRIB           = CLASS_BASE + "attributes-enabled",
            CLASS_REFUND           = CLASS_BASE + "attributes-downgrade",
            CLASS_LEVEL            = CLASS_BASE + "level-up-skill",
            MANA_BASE              = "Mana.",
            MANA_ENABLED           = MANA_BASE + "enabled",
            MANA_FREQ              = MANA_BASE + "freq",
            SKILL_BASE             = "Skills.",
            SKILL_DOWNGRADE        = SKILL_BASE + "allow-downgrade",
            SKILL_MESSAGE          = SKILL_BASE + "show-messages",
            SKILL_RADIUS           = SKILL_BASE + "message-radius",
            SKILL_BLOCKS           = SKILL_BASE + "block-filter",
            SKILL_KNOCKBACK        = SKILL_BASE + "knockback-no-damage",
            SKILL_MODEL_DATA       = SKILL_BASE + "use-custommodeldata",
            SKILL_REFUND_ON_CHANGE = SKILL_BASE + "refund-on-change",
            ITEM_BASE              = "Items.",
            ITEM_LORE              = ITEM_BASE + "lore-requirements",
            ITEM_DROP              = ITEM_BASE + "drop-weapon",
            ITEM_SKILLS            = ITEM_BASE + "skill-requirements",
            ITEM_ATTRIBS           = ITEM_BASE + "lore-attributes",
            ITEM_CLASS             = ITEM_BASE + "lore-class-text",
            ITEM_SKILL             = ITEM_BASE + "lore-skill-text",
            ITEM_LEVEL             = ITEM_BASE + "lore-level-text",
            ITEM_EXCLUDE           = ITEM_BASE + "lore-exclude-text",
            ITEM_ATTR              = ITEM_BASE + "lore-attribute-text",
            ITEM_STATS             = ITEM_BASE + "attribute-text",
            ITEM_SLOTS             = ITEM_BASE + "slots",
            CAST_BASE              = "Casting.",
            CAST_ENABLED           = CAST_BASE + "enabled",
            CAST_BARS              = CAST_BASE + "bars",
            CAST_COMBAT            = CAST_BASE + "combat",
            CAST_INDICATOR         = CAST_BASE + "cast-indicator",
            CAST_SLOT              = CAST_BASE + "slot",
            CAST_ITEM              = CAST_BASE + "item",
            CAST_COOLDOWN          = CAST_BASE + "cooldown",
            CAST_HOVER             = CAST_BASE + "hover-item",
            CAST_INSTANT           = CAST_BASE + "instant-item",
            COMBO_BASE             = "Click Combos.",
            COMBO_ENABLED          = COMBO_BASE + "enabled",
            COMBO_CUSTOM           = COMBO_BASE + "allow-custom",
            COMBO_CLICK            = COMBO_BASE + "use-click-",
            COMBO_SIZE             = COMBO_BASE + "combo-size",
            COMBO_TIME             = COMBO_BASE + "click-time",
            COMBO_AUTO             = COMBO_BASE + "auto-assign",
            EXP_BASE               = "Experience.",
            WORLD_BASE             = "Worlds.",
            WORLD_ENABLE           = WORLD_BASE + "enable",
            WORLD_TYPE             = WORLD_BASE + "use-as-enabling",
            WORLD_LIST             = WORLD_BASE + "worlds",
            WG_SKILLS              = "disable-skills",
            WG_EXP                 = "disable-exp";

    private final         HashMap<String, Double>        yields           = new HashMap<String, Double>();
    private final         HashMap<String, GroupSettings> groups           = new HashMap<String, GroupSettings>();
    private final         SkillAPI                       plugin;
    private final         DataSection                    config;
    private final         HashMap<String, Integer>       permAccounts     = new HashMap<String, Integer>();
    private final         ArrayList<String>              monsterWorlds    = new ArrayList<String>();
    private final         ArrayList<String>              passiveWorlds    = new ArrayList<String>();
    private final         ArrayList<String>              playerWorlds     = new ArrayList<String>();
    /**
     * Retrieves the default skill bar layout
     *
     * @return default skill bar layout
     */
    @Getter private final boolean[]                      defaultBarLayout = new boolean[9];
    /**
     * Retrieves the list of locked skill bar slots
     *
     * @return list of locked skill bar slots
     */
    @Getter private final boolean[]                      lockedSlots      = new boolean[9];
    @Getter
    @Accessors(fluent = true)
    private               boolean                        useOldDurability, useBoundingBoxes;

    private         Map<String, Map<String, Double>> breakYields;
    private         Map<String, Map<String, Double>> placeYields;
    private         Map<String, Map<String, Double>> craftYields;
    @Getter private boolean                          trackBreak, yieldsEnabled;
    /**
     * Retrieves whether accounts should be initialized with
     * one file per class.
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean             onePerClass;
    /**
     * Retrieves the main class group for displaying prefixes
     * or showing account information
     *
     * @return main class group
     */
    @Getter private String              mainGroup;
    /**
     * Retrieves the max accounts allowed for most players
     *
     * @return max accounts allowed for most players
     */
    @Getter private int                 maxAccounts;
    private         boolean             monsterEnemy;
    private         boolean             passiveAlly;
    private         boolean             playerAlly;
    private         boolean             partiesAlly;
    private         boolean             affectNpcs;
    private         boolean             affectArmorStands;
    private         CombatProtection    combatProtection = new DefaultCombatProtection();
    private         boolean             auto;
    private         boolean             useSql;
    private         int                 minutes;
    private         int                 sqlDelay;
    /**
     * Retrieves the host IP for the database
     *
     * @return host IP for SQL database
     */
    @Getter private String              sqlHost;
    /**
     * Retrieves the host port for the database
     *
     * @return host port for SQL database
     */
    @Getter private String              sqlPort;
    /**
     * Retrieves the name of the SQL database
     *
     * @return SQL database name
     */
    @Getter private String              sqlDatabase;
    private         String              sqlUser;
    private         String              sqlPass;
    private         boolean             modifyHealth;
    private         int                 defaultHealth;
    private         boolean             showAutoSkills;
    private         boolean             attributesEnabled;
    /**
     * Checks whether or not attribute points can be refunded
     *
     * @return true if can refund, false otherwise
     */
    @Getter private boolean             attributesDowngrade;
    private         String              levelUpSkill;
    /**
     * Checks whether or not mana is enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean             manaEnabled;
    /**
     * Retrieves the frequency of mana gain
     *
     * @return the frequency of mana gain
     */
    @Getter private int                 gainFreq;
    /**
     * Retrieves the list of filtered blocks
     *
     * @return list of blocks
     */
    @Getter private ArrayList<Material> filteredBlocks;
    /**
     * Checks whether or not downgrades are allowed
     *
     * @return true if allowed, false otherwise
     */
    @Getter private boolean             allowDowngrade;
    /**
     * Checks whether or not skill messages are enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean             showSkillMessages;
    /**
     * @return whether or not knockback should be applied when dealing 0 damage
     */
    @Getter private boolean             knockback;
    /**
     * Gets the radius in which skill messages are sent out
     *
     * @return skill message radius
     */
    @Getter private int                 messageRadius;
    private         boolean             skillModelData;
    /**
     * Checks whether or not lore requirements are enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean             checkLore;
    /**
     * @return true if should check for attribute bonuses
     */
    @Getter private boolean             checkAttributes;
    /**
     * @return true if should check for skill requirements
     */
    @Getter private boolean             checkSkills;
    /**
     * @return checks if weapons are dropped when hovered
     */
    @Getter private boolean             dropWeapon;
    /**
     * Retrieves the text used for class requirements on items
     *
     * @return lore text for class requirements
     */
    @Getter private String              loreClassText;
    /**
     * Retrieves the text used for level requirements on items
     *
     * @return lore text for level requirements
     */
    @Getter private String              loreLevelText;
    /**
     * Retrieves the text used for excluded classes on items
     *
     * @return lore text for excluded classes
     */
    @Getter private String              loreExcludeText;
    /**
     * @return slots checked for requirements and attributes
     */
    @Getter private int[]               slots;
    private         String              skillPre, skillPost;
    private String attrReqPre, attrReqPost;
    private String attrPre, attrPost;
    private         List<String> titleMessages;
    /**
     * Checks whether or not old health bars (fixed 10 hearts) are enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      oldHealth;
    /**
     * @return true if forces the SkillAPI health scaling, false otherwise
     */
    @Getter private boolean      forceScaling;
    /**
     * Gets the setting for using the level bar
     *
     * @return level bar setting
     */
    @Getter private String       levelBar;
    /**
     * Gets the setting for using the food bar
     *
     * @return food bar setting
     */
    @Getter private String       foodBar;
    /**
     * @return boolean whether classes should be refunded their skill points on changing.
     */
    @Getter private boolean      refundOnClassChange;
    /**
     * @return text shown alongside the class level
     */
    @Getter private String       levelText;
    /**
     * Checks whether or not the action bar is being used
     *
     * @return true if used, false otherwise
     */
    @Getter private boolean      useActionBar;
    /**
     * Gets the text to display on the action bar
     *
     * @return action bar text
     */
    @Getter private String       actionText;
    /**
     * Checks whether or not the stats scoreboard is to be shown
     *
     * @return true if shown, false otherwise
     */
    @Getter private boolean      showScoreboard;
    /**
     * Checks whether or not a player's class name is to be
     * shown next to their name
     *
     * @return true if shown, false otherwise
     */
    @Getter private boolean      showClassName;
    /**
     * Checks whether or not a player's class level is to be
     * shown below their name
     *
     * @return true if shown, false otherwise
     */
    @Getter private boolean      showClassLevel;
    @Getter private boolean      showBinds;
    @Getter private String       bindText;
    private         boolean      useTitle;
    /**
     * @return duration of the title display in ticks
     */
    @Getter private int          titleDuration;
    /**
     * @return fade in time of the title display in ticks
     */
    @Getter private int          titleFadeIn;
    /**
     * @return fade out time of the title display in ticks
     */
    @Getter private int          titleFadeOut;
    /**
     * Checks whether or not CustomModelData is enabled for GUI
     *
     * @return true if enabled, false otherwise
     */
    @Getter
    @Accessors(fluent = true)
    private         boolean      useGUIModelData;
    /**
     * @return true if default casting is enabled
     */
    @Getter private boolean      castEnabled;
    private         boolean      castBars;
    private         boolean      combatEnabled;
    /**
     * @return slot the cast item is stored in
     */
    @Getter private int          castSlot;
    /**
     * @return global cooldown for casting
     */
    @Getter private long         castCooldown;
    /**
     * @return cast item to use in the slot
     */
    @Getter private ItemStack    castItem;
    @Getter private ItemStack    hoverItem;
    @Getter private ItemStack    instantItem;
    /**
     * @return enabled clicks as an array of booleans indexed by click ID
     */
    @Getter private boolean[]    enabledClicks;
    /**
     * Checks whether or not click combos are enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      combosEnabled;
    /**
     * Checks whether or not players can customize their click combos
     *
     * @return true if can customize them, false otherwise
     */
    @Getter private boolean      customCombosAllowed;
    @Getter
    @Accessors(fluent = true)
    private         boolean      shouldAutoAssignCombos;
    /**
     * Retrieves the max length of combos to be used
     *
     * @return max length of combos to be used
     */
    @Getter private int          comboSize;
    /**
     * Retrieves the amount of seconds allowed between clicks before the combo resets
     *
     * @return number of seconds before a click combo resets
     */
    @Getter private int          clickTime;
    private         ExpFormula   expFormula;
    private         Formula      expCustom;
    private         boolean      useCustomExp;
    /**
     * Checks whether or not experience is to be gained through
     * vanilla experience orbs
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      useOrbs;
    /**
     * Checks whether or not experience from mobs spawned
     * via a mob spawner is to be blocked.
     *
     * @return true if blocked, false otherwise
     */
    @Getter private boolean      blockSpawner;
    /**
     * Checks whether or not experience from mobs spawned
     * via eggs are to be blocked
     *
     * @return true if blocked, false otherwise
     */
    @Getter private boolean      blockEgg;
    /**
     * Checks whether or not players in creative mode
     * are blocked from receiving experience.
     *
     * @return true if blocked, false otherwise
     */
    @Getter private boolean      blockCreative;
    /**
     * Checks whether or not messages should
     * be displayed when a player gains experience
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      showExpMessages;
    /**
     * Checks whether or not messages should be displayed
     * when a player gains a level
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      showLevelMessages;
    /**
     * Checks whether or not messages should be displayed
     * when a player dies and loses experience
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      showLossMessages;
    private         Set<String>  expLostBlacklist;
    /**
     * Checks whether or not the skill bar is enabled
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      skillBarEnabled;
    /**
     * Checks whether or not the skill bar is to display cooldowns
     *
     * @return true if enabled, false otherwise
     */
    @Getter private boolean      skillBarCooldowns;
    /**
     * Retrieves the indicator for an unassigned skill slot
     *
     * @return unassigned indicator
     */
    @Getter private ItemStack    unassigned;
    private         List<String> worlds;
    private         boolean      worldEnabled;
    private         boolean      worldEnableList;
    private         Set<String>  skillDisabledRegions;
    private         Set<String>  expDisabledRegions;

    /**
     * <p>Initializes a new settings manager.</p>
     * <p>This is already set up by SkillAPI and shouldn't be
     * instantiated elsewhere. Instead, get it from SkillAPI
     * using the SkillAPI.getSettings() method.</p>
     *
     * @param plugin SkillAPI plugin reference
     */
    public Settings(SkillAPI plugin) {
        this.plugin = plugin;
        CommentedConfig file = new CommentedConfig(plugin, "config");
        file.checkDefaults();
        file.trim();
        file.save();
        config = file.getConfig();
    }

    /**
     * <p>Reloads the settings from SkillAPI's config.yml file.</p>
     * <p>This will fill in any missing values with default values
     * and trim any values that aren't supposed to be there.</p>
     */
    public void reload() {
        try {
            Class.forName("org.bukkit.inventory.meta.Damageable");
            useOldDurability = false;
        } catch (ClassNotFoundException e) {
            useOldDurability = true;
        }
        try {
            Entity.class.getMethod("getBoundingBox");
            useBoundingBoxes = true;
        } catch (NoSuchMethodException e) {
            useBoundingBoxes = false;
        }

        loadExperienceSettings();
        loadAccountSettings();
        loadClassSettings();
        loadManaSettings();
        loadSkillSettings();
        loadItemSettings();
        loadGUISettings();
        loadCastSettings();
        loadComboSettings();
        loadExpSettings();
        loadSkillBarSettings();
        loadLoggingSettings();
        loadWorldSettings();
        loadSaveSettings();
        loadTargetingSettings();
        loadWorldGuardSettings();
    }

    public void loadExperienceSettings() {
        CommentedConfig file = new CommentedConfig(plugin, "exp");
        file.checkDefaults();
        file.save();
        DataSection config = file.getConfig();

        DataSection breakData = config.getSection("break");
        yieldsEnabled = config.getBoolean("enabled", false);
        trackBreak = breakData.getBoolean("allow-replace", true);
        breakYields = loadYields(breakData.getSection("types"));
        placeYields = loadYields(config.getSection("place"));
        craftYields = loadYields(config.getSection("craft"));
    }

    private Map<String, Map<String, Double>> loadYields(DataSection config) {
        Map<String, Map<String, Double>> yields = new HashMap<String, Map<String, Double>>();
        for (String className : config.keys()) {
            HashMap<String, Double> map         = new HashMap<String, Double>();
            DataSection             classYields = config.getSection(className);
            for (String type : classYields.keys()) {
                map.put(type.toUpperCase().replace(" ", "_"), classYields.getDouble(type));
            }
            yields.put(className, map);
        }
        return yields;
    }

    public double getBreakYield(PlayerClass playerClass, Material mat) {
        return getYield(breakYields, playerClass, mat.name());
    }

    public double getPlaceYield(PlayerClass playerClass, Material mat) {
        return getYield(placeYields, playerClass, mat.name());
    }

    public double getCraftYield(PlayerClass playerClass, Material mat) {
        return getYield(craftYields, playerClass, mat.name());
    }

    private double getYield(Map<String, Map<String, Double>> yields, PlayerClass playerClass, String key) {
        double yield = getYield(yields.get(playerClass.getData().getName()), key);
        return yield > 0 ? yield : getYield(yields.get(DEFAULT_YIELD), key);
    }

    private double getYield(Map<String, Double> yields, String key) {
        return yields == null ? 0 : (yields.containsKey(key) ? yields.get(key) : 0);
    }

    public void loadGroupSettings() {
        CommentedConfig file   = new CommentedConfig(plugin, "groups");
        DataSection     config = file.getConfig();
        groups.clear();

        for (String key : config.keys()) {
            groups.put(key.toLowerCase(), new GroupSettings(config.getSection(key)));
        }
        for (String group : SkillAPI.getGroups()) {
            if (!groups.containsKey(group.toLowerCase())) {
                GroupSettings settings = new GroupSettings();
                groups.put(group.toLowerCase(), settings);
                settings.save(config.createSection(group.toLowerCase()));
            }
            config.setComments(group.toLowerCase(), ImmutableList.of(
                    "",
                    " Settings for classes with the group " + group,
                    " If new classes are loaded with different groups,",
                    " the new groups will show up in this file after the first load."));
        }

        file.save();
    }

    /**
     * Retrieves the settings for a class group
     *
     * @param group name of the group to retrieve the settings for
     * @return settings for the class group
     */
    public GroupSettings getGroupSettings(String group) {
        if (!groups.containsKey(group.toLowerCase())) {
            return new GroupSettings();
        }
        return groups.get(group.toLowerCase());
    }

    /**
     * Retrieves the max amount of accounts allowed for a specific player
     * by checking permissions for additional accounts.
     *
     * @param player player to check the max allowed accounts for
     * @return number of allowed accounts
     */
    public int getMaxAccounts(Player player) {
        if (player == null) {
            return getMaxAccounts();
        }
        int max = getMaxAccounts();
        for (Map.Entry<String, Integer> entry : permAccounts.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                max = Math.max(max, entry.getValue());
            }
        }
        return max;
    }

    private void loadAccountSettings() {
        mainGroup = config.getString(ACCOUNT_MAIN);
        onePerClass = config.getBoolean(ACCOUNT_EACH);
        maxAccounts = config.getInt(ACCOUNT_MAX);

        // Permission account amounts
        List<String> list = config.getList(ACCOUNT_PERM);
        for (String item : list) {
            if (!item.contains(":")) {
                continue;
            }

            String[] pieces = item.split(":");
            if (pieces.length != 2) {
                continue;
            }

            try {
                permAccounts.put(pieces[0], Integer.parseInt(pieces[1]));
            } catch (Exception ex) {
                // Invalid setting value
            }
        }
    }

    /**
     * Checks whether or not something can be attacked
     *
     * @param attacker the attacking entity
     * @param target   the target entity
     * @return true if can be attacked, false otherwise
     */
    public boolean canAttack(LivingEntity attacker, LivingEntity target) {
        if (attacker instanceof Player) {
            final Player player = (Player) attacker;
            if (target instanceof Animals && !(target instanceof Tameable)) {
                if (passiveAlly || passiveWorlds.contains(attacker.getWorld().getName())) {return false;}
            } else if (target instanceof Monster) {
                if (monsterEnemy || monsterWorlds.contains(attacker.getWorld().getName())) {return true;}
            } else if (target instanceof Player) {
                if (playerAlly || playerWorlds.contains(attacker.getWorld().getName())) {return false;}

                if (PluginChecker.isPartiesActive() && partiesAlly) {
                    final Parties parties = Parties.getPlugin(Parties.class);
                    final Party   p1      = parties.getJoinedParty(player);
                    final Party   p2      = parties.getJoinedParty((Player) target);
                    return p1 == null || p1 != p2;
                }
                return combatProtection.canAttack(player, (Player) target);
            }
            return combatProtection.canAttack(player, target);
        } else if (attacker instanceof Tameable) {
            Tameable tameable = (Tameable) attacker;
            if (tameable.isTamed() && (tameable.getOwner() instanceof LivingEntity)) {
                return (tameable.getOwner() != target)
                        && canAttack((LivingEntity) tameable.getOwner(), target);
            }
        } else {return !(target instanceof Monster);}

        return combatProtection.canAttack(attacker, target);
    }

    /**
     * Checks whether or not something is an ally
     *
     * @param attacker the attacking entity
     * @param target   the target entity
     * @return true if an ally, false otherwise
     */
    public boolean isAlly(LivingEntity attacker, LivingEntity target) {
        return !canAttack(attacker, target);
    }

    /**
     * Checks whether or not a target is a valid target.
     *
     * @param target target to check
     * @return true if a valid target, false otherwise
     */
    public boolean isValidTarget(final LivingEntity target) {
        return (!target.hasMetadata("NPC") || affectNpcs)
                && (!target.getType().name().equals("ARMOR_STAND") || affectArmorStands);
    }

    /**
     * Swaps out the default combat protection for a custom one
     *
     * @param combatProtection combat protection to use
     */
    public void setCombatProtection(final CombatProtection combatProtection) {
        this.combatProtection = combatProtection;
    }

    private void loadTargetingSettings() {
        if (config.isList(TARGET_MONSTER)) {
            monsterWorlds.addAll(config.getList(TARGET_MONSTER));
            monsterEnemy = false;
        } else {monsterEnemy = config.getBoolean(TARGET_MONSTER);}

        if (config.isList(TARGET_PASSIVE)) {
            passiveWorlds.addAll(config.getList(TARGET_PASSIVE));
            passiveAlly = false;
        } else {passiveAlly = config.getBoolean(TARGET_PASSIVE);}

        if (config.isList(TARGET_PLAYER)) {
            playerWorlds.addAll(config.getList(TARGET_PLAYER));
            playerAlly = false;
        } else {playerAlly = config.getBoolean(TARGET_PLAYER);}

        partiesAlly = config.getBoolean(TARGET_PARTIES);
        affectArmorStands = config.getBoolean(TARGET_STANDS);
        affectNpcs = config.getBoolean(TARGET_NPC);
    }

    /**
     * Checks whether or not auto saving is enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isAutoSave() {
        return auto;
    }

    /**
     * Retrieves the amount of ticks in between each auto save
     *
     * @return frequency of saves
     */
    public int getSaveFreq() {
        return minutes * 60 * 20;
    }

    /**
     * Checks whether or not the plugin is using SQL Database saving
     *
     * @return true if enabled, false otherwise
     */
    public boolean isUseSql() {
        return useSql;
    }

    /**
     * Retrieves the username for the database credentials
     *
     * @return SQL database username
     */
    public String getSqlUser() {
        return sqlUser;
    }

    /**
     * Retrieves the password for the database credentials
     *
     * @return SQL database password
     */
    public String getSqlPass() {
        return sqlPass;
    }

    /**
     * @return time in milliseconds to wait before loading SQL data
     */
    public int getSqlDelay() {
        return sqlDelay;
    }

    private void loadSaveSettings() {
        auto = config.getBoolean(SAVE_AUTO);
        minutes = config.getInt(SAVE_MINS);
        useSql = config.getBoolean(SAVE_SQL);

        DataSection details = config.getSection(SAVE_SQLD);
        sqlDelay = details.getInt("delay");

        if (useSql) {
            sqlHost = details.getString("host");
            sqlPort = details.getString("port");
            sqlDatabase = details.getString("database");
            sqlUser = details.getString("username");
            sqlPass = details.getString("password");
        }
    }

    /**
     * Checks whether or not SkillAPI should modify the max health of players
     *
     * @return true if enabled, false otherwise
     */
    public boolean isModifyHealth() {
        return modifyHealth;
    }

    /**
     * <p>Retrieves the default health for players that do not have a class.</p>
     *
     * @return default health for classless players
     */
    public int getDefaultHealth() {
        return defaultHealth;
    }

    /**
     * Checks whether or not auto-leveled skills are to be shown.
     *
     * @return true if shown, false otherwise
     */
    public boolean isShowingAutoSkills() {
        return showAutoSkills;
    }

    /**
     * Checks whether or not attributes are enabled
     *
     * @return true if enabled, false otherwise
     */
    public boolean isAttributesEnabled() {
        return attributesEnabled;
    }

    /**
     * Checks whether or not the plugin has a valid skill for
     * level up effects loaded.
     *
     * @return true if one is available, false otherwise
     */
    public boolean hasLevelUpEffect() {
        return getLevelUpSkill() != null;
    }

    /**
     * Retrieves the skill used for level up effects
     *
     * @return skill for level up effects
     */
    public DynamicSkill getLevelUpSkill() {
        Skill skill = SkillAPI.getSkill(levelUpSkill);
        return (skill instanceof DynamicSkill) ? (DynamicSkill) skill : null;
    }

    private void loadClassSettings() {
        modifyHealth = config.getBoolean(CLASS_MODIFY);
        defaultHealth = config.getInt(CLASS_HP);
        showAutoSkills = config.getBoolean(CLASS_SHOW);
        attributesEnabled = config.getBoolean(CLASS_ATTRIB);
        attributesDowngrade = config.getBoolean(CLASS_REFUND);
        levelUpSkill = config.getString(CLASS_LEVEL);
    }

    private void loadManaSettings() {
        manaEnabled = config.getBoolean(MANA_ENABLED);
        gainFreq = (int) (config.getDouble(MANA_FREQ) * 20);
    }

    /**
     * Return whether skill mechanics should use 'data' values as CustomModelData
     *
     * @return skill mechanics use CustomModelData
     */
    public boolean useSkillModelData() {return skillModelData;}

    private void loadSkillSettings() {
        allowDowngrade = config.getBoolean(SKILL_DOWNGRADE);
        showSkillMessages = config.getBoolean(SKILL_MESSAGE);
        messageRadius = config.getInt(SKILL_RADIUS);
        knockback = config.getBoolean(SKILL_KNOCKBACK);
        skillModelData = config.getBoolean(SKILL_MODEL_DATA);
        refundOnClassChange = config.getBoolean(SKILL_REFUND_ON_CHANGE);
        if (skillModelData) {
            try {
                ItemMeta.class.getMethod("hasCustomModelData", null);
            } catch (NoSuchMethodException e) {
                skillModelData = false;
                Logger.log("CustomModelData not supported below 1.14+. Using item durability/data instead.");
            }
        }

        filteredBlocks = new ArrayList<>();
        List<String> list = config.getList(SKILL_BLOCKS);
        for (String item : list) {
            item = item.toUpperCase().replace(' ', '_');
            if (item.endsWith("*")) {
                item = item.substring(0, item.length() - 1);
                for (Material mat : Material.values()) {
                    if (mat.name().contains(item)) {
                        filteredBlocks.add(mat);
                    }
                }
            } else {
                try {
                    Material mat = Material.valueOf(item);
                    filteredBlocks.add(mat);
                } catch (Exception ex) {
                    Logger.invalid("Invalid block type \"" + item + "\"");
                }
            }
        }
    }

    /**
     * @return lore for skill requirements
     */
    public String getSkillText(String skill) {
        return skillPre + skill + skillPost;
    }

    /**
     * Retrieves the text used for attribute requirements on items
     *
     * @return lore text for attributes
     */
    public String getAttrReqText(String attr) {
        return attrReqPre + attr + attrReqPost;
    }

    /**
     * @return lore text for giving attributes
     */
    public String getAttrGiveText(String attr) {
        return attrPre + attr + attrPost;
    }

    private void loadItemSettings() {
        checkLore = config.getBoolean(ITEM_LORE);
        dropWeapon = config.getBoolean(ITEM_DROP);
        checkSkills = config.getBoolean(ITEM_SKILLS);
        checkAttributes = config.getBoolean(ITEM_ATTRIBS);
        loreClassText = config.getString(ITEM_CLASS).toLowerCase();
        loreLevelText = config.getString(ITEM_LEVEL).toLowerCase();
        loreExcludeText = config.getString(ITEM_EXCLUDE).toLowerCase();

        String temp  = config.getString(ITEM_SKILL).toLowerCase();
        int    index = temp.indexOf('{');
        skillPre = temp.substring(0, index);
        skillPost = temp.substring(index + 7);

        temp = config.getString(ITEM_ATTR).toLowerCase();
        index = temp.indexOf('{');
        attrReqPre = temp.substring(0, index);
        attrReqPost = temp.substring(index + 6);

        temp = config.getString(ITEM_STATS).toLowerCase();
        index = temp.indexOf('{');
        attrPre = temp.substring(0, index);
        attrPost = temp.substring(index + 6);

        List<String> slotList = config.getList(ITEM_SLOTS);
        if (!VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {slotList.remove("40");}
        slots = new int[slotList.size()];
        for (int i = 0; i < slots.length; i++) {slots[i] = NumberParser.parseInt(slotList.get(i));}
    }

    /**
     * Check whether or not to use the title display
     * on the given message type
     *
     * @param type type of message to check for
     * @return true if should use title display, false otherwise
     */
    public boolean useTitle(TitleType type) {
        return useTitle && type != null && titleMessages.contains(type.name().toLowerCase());
    }

    private void loadGUISettings() {
        oldHealth = config.getBoolean(GUI_OLD);
        forceScaling = config.getBoolean(GUI_FORCE);
        levelBar = config.getString(GUI_LVLBAR);
        levelText = TextFormatter.colorString(config.getString(GUI_LVLTXT, "Level"));
        foodBar = config.getString(GUI_FOOD);
        useActionBar = config.getBoolean(GUI_ACTION);
        actionText = config.getString(GUI_TEXT);
        showScoreboard = config.getBoolean(GUI_BOARD);
        showClassName = config.getBoolean(GUI_NAME);
        showClassLevel = config.getBoolean(GUI_LEVEL);
        showBinds = config.getBoolean(GUI_BINDS);
        bindText = config.getString(GUI_BIND_TEXT);
        useTitle = config.getBoolean(GUI_TITLE);
        titleDuration = (int) (20 * config.getFloat(GUI_DUR));
        titleFadeIn = (int) (20 * config.getFloat(GUI_FADEI));
        titleFadeOut = (int) (20 * config.getFloat(GUI_FADEO));
        titleMessages = config.getList(GUI_LIST);
        useGUIModelData = config.getBoolean(GUI_CUSTOMMODELDATA, false);
        if (useGUIModelData) {
            try {
                ItemMeta.class.getMethod("hasCustomModelData");
                useGUIModelData = true;
            } catch (NoSuchMethodException e) {
                useGUIModelData = false;
                Logger.log("CustomModelData not supported below 1.14+. Using item durability/data instead.");
            }
        }
    }

    /**
     * @return true if using bar format, false otherwise
     */
    public boolean isUsingBars() {
        return castEnabled && castBars && !combatEnabled;
    }

    public boolean isUsingWand() {
        return castEnabled && !castBars && !combatEnabled;
    }

    public boolean isUsingCombat() {
        return castEnabled && combatEnabled;
    }

    private void loadCastSettings() {
        castEnabled = config.getBoolean(CAST_ENABLED);
        castBars = config.getBoolean(CAST_BARS);
        combatEnabled = config.getBoolean(CAST_COMBAT);
        castSlot = config.getInt(CAST_SLOT) - 1;
        castCooldown = (long) (config.getDouble(CAST_COOLDOWN) * 1000);
        castItem = GUITool.parseItem(config.getSection(CAST_ITEM));
        hoverItem = GUITool.parseItem(config.getSection(CAST_HOVER));
        instantItem = GUITool.parseItem(config.getSection(CAST_INSTANT));
        castEnabled = castEnabled && castItem != null;
        IndicatorSettings.load(config.getSection(CAST_INDICATOR));
    }

    private void loadComboSettings() {
        combosEnabled = config.getBoolean(COMBO_ENABLED);
        customCombosAllowed = combosEnabled && config.getBoolean(COMBO_CUSTOM);
        shouldAutoAssignCombos = combosEnabled && config.getBoolean(COMBO_AUTO, true);
        comboSize = config.getInt(COMBO_SIZE);
        clickTime = (int) (1000 * config.getDouble(COMBO_TIME));

        enabledClicks = new boolean[Click.values().length + 1];
        for (int i = 1; i <= Click.values().length; i++) {
            final String key = COMBO_CLICK + Click.getById(i).name().toLowerCase().replace('_', '-');
            enabledClicks[i] = config.getBoolean(key);
        }

        if (enabledClicks[Click.RIGHT_SHIFT.getId()] || enabledClicks[Click.LEFT_SHIFT.getId()]) {
            enabledClicks[Click.SHIFT.getId()] = false;
        }
    }

    /**
     * Gets the required amount of experience at a given level
     *
     * @param level level of the class
     * @return required experience to gain a level
     */
    public int getRequiredExp(int level) {
        if (useCustomExp) {
            double result = expCustom.compute(level, 0);
            return (int) result;
        } else return expFormula.calculate(level);

    }

    /**
     * Gets the experience yield of a mob
     *
     * @param mob mob to get the yield of
     * @return experience yield
     */
    public double getYield(String mob) {
        mob = mob.toLowerCase();
        if (!yields.containsKey(mob)) {
            return 0;
        } else {
            return yields.get(mob);
        }
    }

    /**
     * @param world world a player died in
     * @return true if the world is blacklisted for losing experience
     */
    public boolean shouldIgnoreExpLoss(final World world) {
        return expLostBlacklist.contains(world.getName());
    }

    private void loadExpSettings() {
        this.useOrbs = config.getBoolean(EXP_BASE + "use-exp-orbs");
        this.blockSpawner = config.getBoolean(EXP_BASE + "block-mob-spawner");
        this.blockEgg = config.getBoolean(EXP_BASE + "block-mob-egg");
        this.blockCreative = config.getBoolean(EXP_BASE + "block-creative");
        this.showExpMessages = config.getBoolean(EXP_BASE + "exp-message-enabled");
        this.showLevelMessages = config.getBoolean(EXP_BASE + "level-message-enabled");
        this.showLossMessages = config.getBoolean(EXP_BASE + "lose-exp-message");
        this.expLostBlacklist = new HashSet<>(config.getList(EXP_BASE + "lose-exp-blacklist"));

        DataSection formula = config.getSection(EXP_BASE + "formula");
        int         x       = formula.getInt("x");
        int         y       = formula.getInt("y");
        int         z       = formula.getInt("z");
        expFormula = new ExpFormula(x, y, z);

        expCustom = new Formula(config.getString(EXP_BASE + "custom-formula"), new CustomValue("lvl"));
        useCustomExp = config.getBoolean(EXP_BASE + "use-custom") && expCustom.isValid();

        DataSection yields = config.getSection(EXP_BASE + "yields");
        this.yields.clear();
        for (String key : yields.keys()) {
            this.yields.put(key, yields.getDouble(key));
        }
    }

    private void loadSkillBarSettings() {
        DataSection bar = config.getSection("Skill Bar");
        skillBarEnabled = bar.getBoolean("enabled", false) && !castEnabled;
        skillBarCooldowns = bar.getBoolean("show-cooldown", true);

        DataSection icon = bar.getSection("empty-icon");
        Material    mat  = Material.matchMaterial(icon.getString("material", "PUMPKIN_SEEDS"));
        if (mat == null) {mat = Material.PUMPKIN_SEEDS;}
        unassigned = new ItemStack(mat);

        ItemMeta meta = unassigned.getItemMeta();

        final int data = icon.getInt("data", 0);
        if (useGUIModelData) {
            if (data != 0) {
                meta.setCustomModelData(data);
            }
        } else {
            unassigned.setData(new MaterialData(mat, (byte) data));
        }

        if (icon.isList("text")) {
            List<String> format = TextFormatter.colorStringList(icon.getList("text"));
            meta.setDisplayName(format.remove(0));
            meta.setLore(format);
        } else {meta.setDisplayName(TextFormatter.colorString(icon.getString("text", "&7Unassigned")));}

        if (useOldDurability) {
            unassigned.setItemMeta(meta);
            unassigned.setDurability((short) icon.getInt("durability", 0));
        } else {
            if (meta instanceof org.bukkit.inventory.meta.Damageable) {
                ((Damageable) meta).setDamage((short) icon.getInt("durability", 0));
            }
            unassigned.setItemMeta(meta);
        }

        DataSection layout     = bar.getSection("layout");
        int         skillCount = 0;
        for (int i = 0; i < 9; i++) {
            DataSection slot = layout.getSection((i + 1) + "");
            defaultBarLayout[i] = slot.getBoolean("skill", i <= 5);
            lockedSlots[i] = slot.getBoolean("locked", false);
            if (isUsingCombat() && i == castSlot) {
                lockedSlots[i] = true;
                defaultBarLayout[i] = false;
            }
            if (defaultBarLayout[i]) {
                skillCount++;
            }
        }
        if (skillCount == 9) {
            Logger.invalid("Invalid Skill Bar Setup - Cannot have all 9 skill slots!");
            Logger.invalid("  -> Setting last slot to be a weapon slot");
            defaultBarLayout[8] = false;
        }
    }

    private void loadLoggingSettings() {
        Logger.loadLevels(config.getSection("Logging"));
    }

    /**
     * Checks whether or not SkillAPI is active in the world
     *
     * @param world world to check
     * @return true if active, false otherwise
     */
    public boolean isWorldEnabled(World world) {
        return isWorldEnabled(world.getName());
    }

    /**
     * Checks whether or not SkillAPI is active in the world with
     * the given name.
     *
     * @param world world name
     * @return true if active, false otherwise
     */
    public boolean isWorldEnabled(String world) {
        return !worldEnabled || (worldEnableList == worlds.contains(world));
    }

    private void loadWorldSettings() {
        worldEnabled = config.getBoolean(WORLD_ENABLE);
        worldEnableList = config.getBoolean(WORLD_TYPE);
        worlds = config.getList(WORLD_LIST);
    }

    public boolean areSkillsDisabledForRegion(final String region) {
        return skillDisabledRegions.contains(region);
    }

    public boolean isExpDisabledForRegion(final String region) {
        return expDisabledRegions.contains(region);
    }

    private void loadWorldGuardSettings() {
        final CommentedConfig config = new CommentedConfig(plugin, "worldGuard");
        config.checkDefaults();
        config.trim();
        config.save();
        final DataSection data = config.getConfig();

        skillDisabledRegions = ImmutableSet.copyOf(data.getList(WG_SKILLS));
        expDisabledRegions = ImmutableSet.copyOf(data.getList(WG_EXP));
    }


}
