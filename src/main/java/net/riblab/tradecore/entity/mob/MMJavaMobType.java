package net.riblab.tradecore.entity.mob;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.bukkit.compatibility.WorldGuardSupport;
import io.lumine.mythic.bukkit.events.MythicMobPreSpawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.menus.mobs.MobMenuContext;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.mobs.model.MobModel;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import net.riblab.tradecore.TradeCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MMJavaMobType implements MythicMob, SkillHolder, MenuData<MobMenuContext> {
    private final MythicBukkit plugin;
    private final MobExecutor manager;
    private Pack pack;
    private File file;
    private MythicConfig config;
    private String internalName;
    private ItemStack cachedMenuItem;
    protected String entityTypeString;
    protected MythicEntityType entityType;
    protected BukkitEntityType entityBaseSpawner;
    protected PlaceholderString displayName;
    private String eggDisplay;
    protected String faction = null;
    private MobModel model;
    private DropTable dropTable;
    private DropTable equipmentTable;
    protected PlaceholderDouble attrHealth;
    protected PlaceholderDouble attrDamage;
    protected PlaceholderDouble attrArmor;
    protected PlaceholderDouble attrMovementSpeed;
    protected PlaceholderDouble attrFlyingSpeed;
    protected double attrKnockbackResist;
    protected double attrFollowRange;
    protected double attrAttackSpeed;
    protected double optionReviveHealth;
    protected double lvlModDamage;
    protected double lvlModHealth;
    protected double lvlModArmor;
    protected double lvlModKBR;
    protected double lvlModPower;
    protected double lvlModSpeed;
    protected double lvlModAttackSpeed;
    protected DespawnMode despawnMode;
    protected boolean optionShowHealthInChat;
    protected boolean optionSilent;
    protected boolean optionNoAI;
    protected boolean optionGlowing;
    protected boolean optionInvincible;
    protected boolean optionCollidable;
    protected boolean optionNoGravity;
    protected boolean optionInteractable;
    protected boolean optionHealOnReload;
    protected Boolean optionLockPitch;
    protected boolean useBossBar;
    protected int bossBarRange;
    protected int bossBarRangeSq;
    protected PlaceholderString bossBarTitle;
    protected AbstractBossBar.BarColor bossBarColor;
    protected AbstractBossBar.BarStyle bossBarStyle;
    protected boolean bossBarCreateFog;
    protected boolean bossBarDarkenSky;
    protected boolean bossBarPlayMusic;
    protected Optional<String> mount;
    protected Optional<String> rider;
    private Map<String, PlaceholderDouble> stats;
    private Map<String, Double> damageModifiers;
    private Map<String, Double> entityDamageModifiers;
    private List<String> levelmods;
    private List<String> aiGoalSelectors;
    private List<String> aiTargetSelectors;
    private Map<String, Float> aiPathfindingMalus;
    private String aiNavigator;
    private boolean hasCombatSkills;
    private Map<SkillTrigger, Queue<SkillMechanic>> skills;
    private Queue<SkillMechanic> timerSkills;
    private Map<String, List<SkillMechanic>> signalSkills;
    private boolean usingTimers;
    int size;
    private int noDamageTicks;
    private int maxAttackRange;
    private int maxAttackableRange;
    private int maxThreatDistance;
    private boolean alwaysShowName;
    private boolean showNameOnDamage;
    private boolean useThreatTable;
    private boolean useImmunityTable;
    private boolean useCustomNameplate;
    private boolean optionTTFromDamage;
    private boolean optionTTDecayUnreachable;
    private Boolean repeatAllSkills;
    private Boolean preventOtherDrops;
    private Boolean preventRandomEquipment;
    private Boolean preventLeashing;
    private Boolean preventRename;
    private Boolean preventEndermanTeleport;
    private Boolean preventItemPickup;
    private Boolean preventSilverfishInfection;
    private Boolean preventSunburn;
    private Boolean preventExploding;
    private Boolean preventMobKillDrops;
    private Boolean preventTransformation;
    private Boolean preventMounts;
    private Boolean passthroughDamage;
    private Boolean applyInvisibility;
    private Boolean digOutOfGround;
    private Boolean usesHealthBar;
    protected double spawnVelocityX;
    protected double spawnVelocityXMax;
    protected double spawnVelocityY;
    protected double spawnVelocityYMax;
    protected double spawnVelocityZ;
    protected double spawnVelocityZMax;
    protected Boolean spawnVelocityXRange;
    protected Boolean spawnVelocityYRange;
    protected Boolean spawnVelocityZRange;
    protected List<PlaceholderString> killMessages;
    private String disguise;
    private boolean fakePlayer;
    private final transient ReentrantLock skillLock;

    public MMJavaMobType(MobExecutor manager, String internalName, String mobtype, String displayName) {
        this.despawnMode = DespawnMode.NORMAL;
        this.optionShowHealthInChat = false;
        this.optionSilent = false;
        this.optionNoAI = false;
        this.optionGlowing = false;
        this.optionInvincible = false;
        this.optionCollidable = true;
        this.optionNoGravity = true;
        this.optionInteractable = true;
        this.optionHealOnReload = false;
        this.optionLockPitch = false;
        this.useBossBar = false;
        this.mount = Optional.empty();
        this.rider = Optional.empty();
        this.levelmods = Lists.newArrayList();
        this.aiGoalSelectors = Lists.newArrayList();
        this.aiTargetSelectors = Lists.newArrayList();
        this.aiPathfindingMalus = Maps.newConcurrentMap();
        this.hasCombatSkills = false;
        this.skills = Maps.newConcurrentMap();
        this.timerSkills = Queues.newConcurrentLinkedQueue();
        this.signalSkills = Maps.newConcurrentMap();
        this.usingTimers = false;
        this.alwaysShowName = true;
        this.showNameOnDamage = true;
        this.optionTTFromDamage = true;
        this.optionTTDecayUnreachable = true;
        this.repeatAllSkills = false;
        this.preventOtherDrops = false;
        this.preventRandomEquipment = false;
        this.preventLeashing = false;
        this.preventRename = true;
        this.preventEndermanTeleport = true;
        this.preventItemPickup = true;
        this.preventSilverfishInfection = true;
        this.preventSunburn = false;
        this.preventExploding = false;
        this.preventMobKillDrops = false;
        this.preventTransformation = true;
        this.preventMounts = false;
        this.passthroughDamage = false;
        this.applyInvisibility = false;
        this.digOutOfGround = false;
        this.usesHealthBar = false;
        this.spawnVelocityX = 0.0;
        this.spawnVelocityXMax = 0.0;
        this.spawnVelocityY = 0.0;
        this.spawnVelocityYMax = 0.0;
        this.spawnVelocityZ = 0.0;
        this.spawnVelocityZMax = 0.0;
        this.spawnVelocityXRange = false;
        this.spawnVelocityYRange = false;
        this.spawnVelocityZRange = false;
        this.disguise = null;
        this.fakePlayer = false;
        this.skillLock = new ReentrantLock();
        this.plugin = (MythicBukkit)manager.getPlugin();
        this.manager = manager;
        this.pack = null;
        this.config = null;
        this.file = null;
        this.internalName = internalName;
        MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Loading MythicMob type '{0}'...", new Object[]{this.internalName});
        this.entityTypeString = mobtype;
        if (this.entityTypeString == null) {
            BukkitEntityType me = BukkitEntityType.getMythicEntity(internalName);
            if (me == null) {
//                MythicLogger.errorMobConfig(this, mc, "Could not load MythicMob " + this.internalName + "! No Type specified.");
                this.entityTypeString = "NULL";
                this.entityType = MythicEntityType.COD;
                this.entityBaseSpawner = BukkitEntityType.getMythicEntity(MythicEntityType.COD);
                this.displayName = PlaceholderString.of("ERROR: MOB TYPE FOR '" + this.internalName + "' IS NOT OPTIONAL");
                return;
            }

            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "+ EntityType is vanilla override for {0}", new Object[]{this.entityTypeString});
            this.entityType = MythicEntityType.get(internalName);
            this.entityBaseSpawner = me;
        } else {
            this.getManager();
            this.entityTypeString = MobExecutor.convertMobTypeAliases(this.entityTypeString);
            this.entityType = MythicEntityType.get(this.entityTypeString);
            this.entityBaseSpawner = BukkitEntityType.getMythicEntity(this.entityType);
        }

        if (this.entityBaseSpawner == null) {
            MythicLogger.error("Could not load MythicMob {0}! Invalid type specified.", new Object[]{this.internalName});
            this.entityTypeString = "NULL";
            this.entityType = MythicEntityType.COD;
            this.entityBaseSpawner = BukkitEntityType.getMythicEntity(MythicEntityType.COD);
            this.displayName = PlaceholderString.of("ERROR: MOB TYPE FOR '" + this.internalName + "' IS INVALID");
        } else {
            this.entityBaseSpawner.instantiate(new MythicConfigImpl(new File(TradeCore.getInstance().getDataFolder() + "/dummy.yml")));//空のファイルを渡す
            if (displayName != null) {
                this.displayName = PlaceholderString.of(displayName);
            }

            //modelengine互換
//            if (mc.isSet("Model")) {
//                Schedulers.sync().runLater(() -> {
//                    if (mc.isSet("Model.Type")) {
//                        String modelType = mc.getString("Model.Type").toUpperCase();
//                        if (!modelType.startsWith("MPE") && !modelType.equalsIgnoreCase("MINIATUREPE")) {
//                            if (((MythicBukkit)manager.getPlugin()).getCompatibility().getModelEngine().isPresent()) {
//                                this.model = ((AbstractModelEngineSupport)((MythicBukkit)manager.getPlugin()).getCompatibility().getModelEngine().get()).createMobModel(this, mc);
//                            }
//                        } else if (((MythicBukkit)manager.getPlugin()).getCompatibility().getMiniaturePets().isPresent()) {
//                            this.model = new MPetsModel(this, mc);
//                        }
//                    } else if (((MythicBukkit)manager.getPlugin()).getCompatibility().getModelEngine().isPresent()) {
//                        this.model = ((AbstractModelEngineSupport)((MythicBukkit)manager.getPlugin()).getCompatibility().getModelEngine().get()).createMobModel(this, mc);
//                    }
//
//                }, 1L);
//            } else {
//                this.model = null;
//            }

            this.eggDisplay = this.internalName;
            this.attrHealth = PlaceholderDouble.of("10");
            this.attrDamage = PlaceholderDouble.of("1");
            this.attrArmor = PlaceholderDouble.of(null);
            this.optionReviveHealth = 10;
            this.optionInvincible = false;
            this.faction = null;
            this.mount = Optional.ofNullable(null);
            this.rider = Optional.ofNullable(null);
            boolean despawnByDefault = this.getPlugin().getConfiguration().getDespawnMobsByDefault();
            String shouldDespawn = despawnByDefault ? "TRUE" : "FALSE";
            boolean isPersistent = false;
            if (isPersistent) {
                shouldDespawn = DespawnMode.PERSISTENT.toString();
            }

            this.despawnMode = DespawnMode.get(shouldDespawn);
            this.attrAttackSpeed = 0.0;
            this.attrMovementSpeed = null;
            this.attrFlyingSpeed = PlaceholderDouble.of("-1");
            this.attrKnockbackResist = 0.0;
            this.attrFollowRange = -1.0;
            this.attrAttackSpeed = 0.0;
            this.optionGlowing = false;
            this.optionCollidable = true;
            this.optionNoGravity = false;
            this.optionInteractable = false;
            this.optionSilent = false;
            this.optionNoAI = false;
            this.optionHealOnReload = false;
            this.noDamageTicks = 10 * 2;
            this.optionLockPitch = false;
            this.useBossBar = false;
            this.bossBarTitle = PlaceholderString.of(this.displayName == null ? "" : this.displayName.toString());
            this.bossBarRange = 64;
            this.bossBarRangeSq = (int)Math.pow((double)this.bossBarRange, 2.0);
            String bossBarColor = "WHITE";
            String bossBarStyle = "SOLID";

            try {
                this.bossBarColor = AbstractBossBar.BarColor.valueOf(bossBarColor);
            } catch (Exception var47) {
                this.bossBarColor = AbstractBossBar.BarColor.WHITE;
            }

            try {
                this.bossBarStyle = AbstractBossBar.BarStyle.valueOf(bossBarStyle);
            } catch (Exception var46) {
                this.bossBarStyle = AbstractBossBar.BarStyle.SOLID;
            }

            this.bossBarCreateFog = false;
            this.bossBarDarkenSky = false;
            this.bossBarPlayMusic = false;
            this.usesHealthBar = false;
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Loading mob skills... ", new Object[0]);
            List<String> nSkills = new ArrayList<>();//TODO:ちゃんとスキル設定
            Iterator var18 = nSkills.iterator();

            while(true) {
                String signal;
                while(var18.hasNext()) {
                    String s = (String)var18.next();
                    MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Loading mechanic line: {0}", new Object[]{s});
                    s = MythicLineConfigImpl.unparseBlock(s);
//                    SkillMechanic ms = this.getPlugin().getSkillManager().getMechanic(mc.getFile(), s);
                      SkillMechanic ms = null;
                    if (ms == null) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "-! Mechanic was not found. Skipping.", new Object[0]);
                    } else {
                        ms.setParent(this);
                        Pattern Rpattern;
                        Matcher Rmatcher;
                        if (s.contains("~onTimer")) {
                            Rpattern = Pattern.compile("~onTimer:([0-9]+)");
                            Rmatcher = Rpattern.matcher(s);
                            Rmatcher.find();

                            int interval;
                            try {
                                interval = Integer.parseInt(Rmatcher.group(1));
                            } catch (Exception var49) {
//                                MythicLogger.errorMobConfig(this, mc, "Error parsing Timer skill, invalid interval specified (must be an integer). AbstractSkill=" + s);
                                continue;
                            }

                            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Mechanic set on timer with interval {0}", new Object[]{interval});
                            ms.setTimerInterval(interval);
                            this.timerSkills.add(ms);
                        } else if (s.contains("~onSignal:")) {
                            Rpattern = Pattern.compile("~onSignal:([a-zA-Z0-9_-]*)");
                            Rmatcher = Rpattern.matcher(s);
                            Rmatcher.find();

                            try {
                                signal = Rmatcher.group(1);
                            } catch (Exception var50) {
//                                MythicLogger.errorMobConfig(this, mc, "Error parsing Signal skill, invalid signal specified (contains invalid characters). AbstractSkill=" + s);
                                continue;
                            }

                            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Mechanic set on Signal with key '{0}'", new Object[]{signal});
                            if (this.signalSkills.containsKey(signal)) {
                                ((List)this.signalSkills.get(signal)).add(ms);
                            } else {
                                List<SkillMechanic> signalMechanics = new ArrayList();
                                signalMechanics.add(ms);
                                this.signalSkills.put(signal, signalMechanics);
                            }
                        } else {
                            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Loading mechanic to base skill tree...", new Object[0]);
                            SkillTrigger trigger = ms.getTrigger();
                            if (!this.skills.containsKey(trigger)) {
                                this.skills.put(trigger, Queues.newConcurrentLinkedQueue());
                            }

                            ((Queue)this.skills.get(ms.getTrigger())).add(ms);
                        }
                    }
                }

                if (this.timerSkills.size() > 0) {
                    this.usingTimers = true;
                }

                if (this.hasSkills(SkillTriggers.COMBAT)) {
                    this.hasCombatSkills = true;
                }

                this.optionShowHealthInChat = false;
                this.useThreatTable = false;
                this.useImmunityTable = false;
                this.useCustomNameplate = false;
                this.useThreatTable = false;
                this.useImmunityTable = false;
                this.useThreatTable = false;
                this.optionTTFromDamage = false;
                this.optionTTDecayUnreachable = false;
                this.useThreatTable = false;
                this.optionTTFromDamage = false;
                this.optionTTDecayUnreachable = false;
                this.maxAttackRange = 64;
                this.maxAttackableRange = 256;
                this.maxThreatDistance = 40;
                this.alwaysShowName = false;
                this.showNameOnDamage = false;
                this.repeatAllSkills = false;
                this.preventOtherDrops = this.getPlugin().getConfiguration().getPreventOtherDropsByDefault();
                this.preventRandomEquipment = false;
                this.preventLeashing = true;
                this.preventRename = true;
                this.preventSunburn = false;
                this.preventEndermanTeleport = false;
                this.preventEndermanTeleport = false;
                this.preventTransformation = true;
                this.preventMounts = false;
                this.preventItemPickup = true;
                this.preventMobKillDrops = false;
                this.passthroughDamage = false;
                this.applyInvisibility = false;
                this.aiNavigator = null;
                List<String> lstAIGoalSelectors = null;
                if (lstAIGoalSelectors != null) {
                    Iterator var54 = lstAIGoalSelectors.iterator();

                    while(var54.hasNext()) {
                        String s = (String)var54.next();
                        s = MythicLineConfigImpl.unparseBlock(s);
                        this.aiGoalSelectors.add(s);
                    }
                }

                List<String> lstAITargetSelectors = null;
                if (lstAITargetSelectors != null) {
                    Iterator var57 = lstAITargetSelectors.iterator();

                    while(var57.hasNext()) {
                        signal = (String)var57.next();
                        signal = MythicLineConfigImpl.unparseBlock(signal);
                        this.aiTargetSelectors.add(signal);
                    }
                }

                List<String> lstAIPathfindingMalus = null;
                if (lstAIPathfindingMalus != null) {
                    Iterator var60 = lstAIPathfindingMalus.iterator();

                    while(var60.hasNext()) {
                        String s = (String)var60.next();

                        try {
                            String[] split = s.split(" ");
                            this.aiPathfindingMalus.put(split[0], Float.parseFloat(split[1]));
                        } catch (Error | Exception var45) {
//                            MythicLogger.errorMobConfig(this, mc, "Invalid format for PathfindingMalus line " + s);
                            var45.printStackTrace();
                        }
                    }
                }

                List<String> drops = new ArrayList<>();
                this.dropTable = new DropTable(null, "Mob:" + this.internalName, drops, true);
                List<String> equipment = new ArrayList<>();
                this.equipmentTable = new DropTable(null, "MobEquipment:" + this.internalName, equipment, true);
                List<String> lstStats = new ArrayList<>();
                this.stats = Maps.newConcurrentMap();
                String strSpawnVelocityX;
                if (!lstStats.isEmpty()) {
                    Iterator var24 = lstStats.iterator();

                    while(var24.hasNext()) {
                        String stat = (String)var24.next();

                        try {
                            String[] split = stat.split(" ");
                            strSpawnVelocityX = split[0];
                            PlaceholderDouble value = PlaceholderDouble.of(split[1]);
                            this.stats.put(strSpawnVelocityX, value);
                        } catch (Exception var44) {
//                            MythicLogger.errorMobConfig(this, mc, "Invalid syntax for Stat line: " + stat);
                        }
                    }
                }

                List<String> lstDamageMod = new ArrayList<>();
                this.damageModifiers = new HashMap();
                String strSpawnVelocityY;
                if (lstDamageMod != null && !lstDamageMod.isEmpty()) {
                    Iterator var66 = lstDamageMod.iterator();

                    while(var66.hasNext()) {
                        String dm = (String)var66.next();

                        try {
                            String[] split = dm.split(" ");
                            strSpawnVelocityY = split[0];
                            Double mod = Double.valueOf(split[1]);
                            this.damageModifiers.put(strSpawnVelocityY, mod);
                        } catch (Exception var43) {
//                            MythicLogger.errorMobConfig(this, mc, "Invalid syntax for DamageModifier line: " + dm);
                        }
                    }
                }

                List<String> lstEntDamageMod = new ArrayList<>();
                this.entityDamageModifiers = new HashMap();
                String strSpawnVelocityZ;
                if (lstEntDamageMod != null && lstEntDamageMod.size() > 0) {
                    Iterator var69 = lstEntDamageMod.iterator();

                    while(var69.hasNext()) {
                        strSpawnVelocityX = (String)var69.next();

                        try {
                            String[] split = strSpawnVelocityX.split(" ");
                            strSpawnVelocityZ = split[0];
                            double mod = Double.valueOf(split[1]);
                            this.entityDamageModifiers.put(strSpawnVelocityZ, mod);
                        } catch (Exception var42) {
//                            MythicLogger.errorMobConfig(this, mc, "Invalid syntax for DamageModifier");
                        }
                    }
                }

                List<String> killMessages = new ArrayList<>();
                if (killMessages != null && killMessages.size() > 0) {
                    MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Loading mob kill messages...", new Object[0]);
                    if (this.killMessages == null) {
                        this.killMessages = new ArrayList();
                    }

                    killMessages.forEach((message) -> {
                        this.killMessages.add(PlaceholderString.of(message));
                    });
                }

                this.lvlModDamage = -1.0;
                this.lvlModHealth = -1.0;
                this.lvlModKBR = -1.0;
                this.lvlModPower = -1.0;
                this.lvlModArmor = -1.0;
                this.lvlModSpeed = -1.0;
                this.lvlModAttackSpeed = -1.0;
                if (this.attrDamage != null && this.attrDamage.isStatic()) {
                    try {
                        if (this.lvlModDamage < 0.0) {
                            if (ConfigExecutor.defaultLevelModifierDamage.startsWith("+")) {
                                this.lvlModDamage = Double.valueOf(ConfigExecutor.defaultLevelModifierDamage.substring(1));
                            } else if (ConfigExecutor.defaultLevelModifierDamage.startsWith("*")) {
                                this.lvlModDamage = this.attrDamage.get() * Double.valueOf(ConfigExecutor.defaultLevelModifierDamage.substring(1));
                            } else {
                                this.lvlModDamage = this.attrDamage.get() * Double.valueOf(ConfigExecutor.defaultLevelModifierDamage);
                            }
                        }
                    } catch (Exception var41) {
                        MythicLogger.error("Error calculating Damage Level Modifier: Default configuration is bad.", var41);
                    }
                }

                if (this.attrHealth != null && this.attrHealth.isStatic()) {
                    try {
                        if (this.lvlModHealth < 0.0) {
                            if (ConfigExecutor.defaultLevelModifierHealth.startsWith("+")) {
                                this.lvlModHealth = Double.valueOf(ConfigExecutor.defaultLevelModifierHealth.substring(1));
                            } else if (ConfigExecutor.defaultLevelModifierHealth.startsWith("*")) {
                                this.lvlModHealth = this.attrHealth.get() * Double.valueOf(ConfigExecutor.defaultLevelModifierHealth.substring(1));
                            } else {
                                this.lvlModHealth = this.attrHealth.get() * Double.valueOf(ConfigExecutor.defaultLevelModifierHealth);
                            }
                        }
                    } catch (Exception var40) {
                        MythicLogger.error("Error calculating Health Level Modifier: Default configuration is bad.", var40);
                    }
                }

                try {
                    if (this.lvlModPower < 0.0) {
                        if (ConfigExecutor.defaultLevelModifierPower.startsWith("+")) {
                            this.lvlModPower = Double.valueOf(ConfigExecutor.defaultLevelModifierPower.substring(1));
                        } else if (ConfigExecutor.defaultLevelModifierPower.startsWith("*")) {
                            this.lvlModPower = Double.valueOf(ConfigExecutor.defaultLevelModifierPower.substring(1));
                        } else {
                            this.lvlModPower = Double.valueOf(ConfigExecutor.defaultLevelModifierPower);
                        }
                    }
                } catch (Exception var39) {
                    MythicLogger.error("Error calculating Power Level Modifier: Default configuration is bad.", var39);
                }

                if (this.attrArmor != null && this.attrArmor.isStatic()) {
                    try {
                        if (this.lvlModArmor < 0.0) {
                            if (ConfigExecutor.defaultLevelModifierArmor.startsWith("+")) {
                                this.lvlModArmor = Double.valueOf(ConfigExecutor.defaultLevelModifierArmor.substring(1));
                            } else if (ConfigExecutor.defaultLevelModifierArmor.startsWith("*")) {
                                this.lvlModArmor = this.attrArmor.get() * Double.valueOf(ConfigExecutor.defaultLevelModifierArmor.substring(1));
                            } else {
                                this.lvlModArmor = Double.valueOf(ConfigExecutor.defaultLevelModifierArmor);
                            }
                        }
                    } catch (Exception var38) {
                        MythicLogger.error("Error calculating Armor Level Modifier: Default configuration is bad.", var38);
                    }
                }

                try {
                    if (this.lvlModKBR < 0.0) {
                        if (ConfigExecutor.defaultLevelModifierKBR.startsWith("+")) {
                            this.lvlModKBR = Double.valueOf(ConfigExecutor.defaultLevelModifierKBR.substring(1));
                        } else if (ConfigExecutor.defaultLevelModifierKBR.startsWith("*")) {
                            this.lvlModKBR = this.attrKnockbackResist * Double.valueOf(ConfigExecutor.defaultLevelModifierKBR.substring(1));
                        } else {
                            this.lvlModKBR = Double.valueOf(ConfigExecutor.defaultLevelModifierKBR);
                        }
                    }
                } catch (Exception var48) {
                    MythicLogger.error("Error calculating KBR Level Modifier: Default configuration is bad.");
                    if (ConfigExecutor.debugLevel > 0) {
                        var48.printStackTrace();
                    }
                }

                this.digOutOfGround = false;
                strSpawnVelocityX = "0";
                strSpawnVelocityY = "0";
                strSpawnVelocityZ = "0";
                String[] split;
                if (strSpawnVelocityX.contains("to")) {
                    split = strSpawnVelocityX.split("to");

                    try {
                        this.spawnVelocityX = Double.valueOf(split[0]);
                        this.spawnVelocityXMax = Double.valueOf(split[1]);
                        this.spawnVelocityXRange = true;
                    } catch (Exception var37) {
                        this.spawnVelocityX = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityX.", new Object[]{this.internalName});
                    }
                } else {
                    try {
                        this.spawnVelocityX = Double.valueOf(strSpawnVelocityX);
                    } catch (Exception var36) {
                        this.spawnVelocityX = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityX.", new Object[]{this.internalName});
                    }
                }

                if (strSpawnVelocityY.contains("to")) {
                    split = strSpawnVelocityY.split("to");

                    try {
                        this.spawnVelocityY = Double.valueOf(split[0]);
                        this.spawnVelocityYMax = Double.valueOf(split[1]);
                        this.spawnVelocityYRange = true;
                    } catch (Exception var35) {
                        this.spawnVelocityY = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityY.", new Object[]{this.internalName});
                    }
                } else {
                    try {
                        this.spawnVelocityY = Double.valueOf(strSpawnVelocityY);
                    } catch (Exception var34) {
                        this.spawnVelocityY = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityY.", new Object[]{this.internalName});
                    }
                }

                if (strSpawnVelocityZ.contains("to")) {
                    split = strSpawnVelocityZ.split("to");

                    try {
                        this.spawnVelocityZ = Double.valueOf(split[0]);
                        this.spawnVelocityZMax = Double.valueOf(split[1]);
                        this.spawnVelocityZRange = true;
                    } catch (Exception var33) {
                        this.spawnVelocityZ = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityZ.", new Object[]{this.internalName});
                    }
                } else {
                    try {
                        this.spawnVelocityZ = Double.valueOf(strSpawnVelocityZ);
                    } catch (Exception var32) {
                        this.spawnVelocityZ = 0.0;
                        MythicLogger.error("Error loading MythicMob {0}! Invalid value for SpawnModifier.VelocityZ.", new Object[]{this.internalName});
                    }
                }

                this.disguise = null;

                this.buildCache(false);
                return;
            }
        }
    }

    public void buildCache() {
        this.buildCache(true);
    }

    public void buildCache(boolean refreshMobs) {
        this.cachedMenuItem = this.entityBaseSpawner.getHead();
        this.cachedMenuItem = ItemFactory.of(this.cachedMenuItem).hideAttributes().lore("<red> ").lore("<rainbow>▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃").lore("<red> ").lore("<gray>⊳ <white>Left-Click to get mob egg").lore("<gray>⊳ <white>Right-Click to Edit <red>(ALPHA FEATURE)").build();
        if (refreshMobs) {
            this.getManager().getActiveMobs((am) -> {
                return am.getType().equals(this);
            }).forEach((mob) -> {
                mob.remountType();
            });
        }

    }

    public ActiveMob spawn(AbstractLocation location, double level) {
        return this.spawn(location, level, SpawnReason.OTHER);
    }

    public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason) {
        return this.spawn(location, level, reason, (Consumer)null);
    }

    public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc) {
        return this.spawn(location, level, reason, prespawnFunc, (MythicSpawner)null);
    }

    public ActiveMob spawn(AbstractLocation location, double level, SpawnReason reason, Consumer<Entity> prespawnFunc, MythicSpawner spawner) {
        this.getManager();
        MobExecutor.spawnflag = true;

        ActiveMob var11;
        try {
            MythicMobPreSpawnEvent preEvent = (MythicMobPreSpawnEvent) Events.callAndReturn(new MythicMobPreSpawnEvent(this, location, level, reason));
            WorldGuardSupport worldGuardSupport;
            if (preEvent.isCancelled()) {
                worldGuardSupport = null;
                return null;
            }

            ActiveMob am;
            if (this.getPlugin().getCompatibility().getWorldGuard().isPresent()) {
                worldGuardSupport = (WorldGuardSupport)this.getPlugin().getCompatibility().getWorldGuard().get();
                if (!worldGuardSupport.getLocationAllowsMobSpawning(BukkitAdapter.adapt(location))) {
                    am = null;
                    return am;
                }
            }

            MythicMobSpawnEvent event;
            Entity e;
            try {
                if (this.entityBaseSpawner == null) {
                    am = null;
                    return am;
                }

                e = BukkitAdapter.adapt(this.entityBaseSpawner.spawn(location, reason, (entity) -> {
                    if (this.applyInvisibility && entity instanceof LivingEntity) {
                        ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
                    }

                    if (prespawnFunc != null) {
                        prespawnFunc.accept(entity);
                    }

                }));
            } catch (Exception var16) {
                MythicLogger.errorMobConfig(this, this.config, "Mob type may not be supported on this version of Minecraft. Enable debugging for more information.");
                this.getPlugin().getConfiguration();
                if (ConfigExecutor.debugLevel > 0) {
                    var16.printStackTrace();
                }
                
                return null;
            } catch (Error var17) {
                MythicLogger.errorMobConfig(this, this.config, "Mob type may not be supported on this version of Minecraft. Enable debugging for more information.");
                this.getPlugin().getConfiguration();
                if (ConfigExecutor.debugLevel > 0) {
                    var17.printStackTrace();
                }
                
                return null;
            }

            am = new ActiveMob(BukkitAdapter.adapt(e), this, 0.0);
            if (spawner != null) {
                am.setSpawner(spawner);
            }

            MythicLogger.debug(MythicLogger.DebugLevel.INFO, "Calling MythicMobSpawnEvent for " + this.getInternalName() + " (level: " + level + ")", new Object[0]);
            event = new MythicMobSpawnEvent(am, level, reason);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.getManager().registerActiveMob(am);
                level = event.getMobLevel();
                am.setLevel(level);
                am = this.applySpawnModifiers(am);
                if (this.hasSkills(SkillTriggers.SPAWN)) {
                    new TriggeredSkill(SkillTriggers.SPAWN, am, (AbstractEntity)null);
                }

                var11 = am;
                return var11;
            }

            e.remove();
            var11 = null;
        } finally {
            this.getManager();
            MobExecutor.spawnflag = false;
        }

        return var11;
    }

    public ActiveMob applyMobOptions(ActiveMob am, double level) {
        AbstractEntity aEntity = am.getEntity();
        Entity entity = aEntity.getBukkitEntity();
        if (am.getEntity().isLiving()) {
            LivingEntity asLiving = (LivingEntity)entity;
            if (asLiving.isDead()) {
                entity.remove();
                am.unregister(true);
                return am;
            }

            if (am.getDespawnMode().getDespawnWithoutNearbyPlayers()) {
                asLiving.setRemoveWhenFarAway(true);
            } else {
                asLiving.setRemoveWhenFarAway(false);
            }

            if (am.getDespawnMode().getSavesToDisk()) {
                asLiving.setPersistent(true);
            } else {
                asLiving.setPersistent(false);
            }

            if (this.alwaysShowName) {
                asLiving.setCustomNameVisible(true);
            }

            if (this.preventItemPickup) {
                asLiving.setCanPickupItems(false);
            }

            if (this.applyInvisibility) {
                asLiving.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
            }

            double health = this.getHealth(am);
            double hp;
            double damage;
            if (health > 0.0) {
                if (this.optionHealOnReload) {
                    aEntity.setHealthAndMax(health);
                } else {
                    hp = aEntity.getHealth();
                    if (hp < aEntity.getMaxHealth()) {
                        damage = hp / aEntity.getMaxHealth();
                        aEntity.setMaxHealth(health);
                        aEntity.setHealth(health * damage);
                    } else {
                        aEntity.setHealthAndMax(health);
                    }
                }
            }

            hp = this.getArmor(am);
            if (hp > 0.0) {
                aEntity.setArmor(hp);
            }

            damage = this.getDamage(am);
            if (damage >= 0.0) {
                aEntity.setDamage(damage);
            }

            double movementSpeed = this.getMovementSpeed(am);
            if (movementSpeed >= 0.0) {
                asLiving.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
            }

            double flyingSpeed = this.getFlyingSpeed(am);
            if (flyingSpeed >= 0.0) {
                asLiving.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(flyingSpeed);
            }

            if (this.attrAttackSpeed != 0.0 && asLiving.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
                asLiving.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(this.getAttackSpeed(level));
            }

            if (this.attrFollowRange >= 0.0) {
                asLiving.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(this.attrFollowRange);
            }

            if (this.attrKnockbackResist != 0.0) {
                asLiving.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(this.getKnockbackResistance(level));
            }

            if (!this.optionCollidable) {
                asLiving.setCollidable(false);
            }

            asLiving.setMaximumNoDamageTicks(this.noDamageTicks);
            Schedulers.sync().runLater(() -> {
                if (am.getEntity() == null) {
                    MythicLogger.debug(MythicLogger.DebugLevel.INFO, "MythicMob {0} failed to spawn - was despawned by other plugin", new Object[]{this.internalName});
                } else {
                    EntityEquipment ee;
                    if (this.preventRandomEquipment && System.currentTimeMillis() - am.getSpawnTime() < 5000L) {
                        ee = asLiving.getEquipment();

                        assert ee != null;

                        ee.clear();
                        ee.setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
                    }

                    this.equipmentTable.generate(new DropMetadataImpl(am, am.getEntity())).equip(am.getEntity());
                    ee = asLiving.getEquipment();

                    assert ee != null;

                    if (asLiving instanceof Creature) {
                        ee.setItemInMainHandDropChance(0.0F);
                        ee.setItemInOffHandDropChance(0.0F);
                        ee.setHelmetDropChance(0.0F);
                        ee.setChestplateDropChance(0.0F);
                        ee.setLeggingsDropChance(0.0F);
                        ee.setBootsDropChance(0.0F);
                    }

                    if (this.useCustomNameplate) {
                        am.setShowCustomNameplate(true);
                    }

                }
            }, 5L);
            if (this.getDisplayName() != null) {
                am.getEntity().setCustomName(am.getDisplayName());
            }
        }

        if (this.optionInvincible) {
            entity.setInvulnerable(true);
        }

        if (this.optionGlowing) {
            entity.setGlowing(true);
        }

        if (this.optionNoGravity) {
            entity.setGravity(false);
        }

        if (this.optionSilent) {
            entity.setSilent(true);
        }

        if (this.model != null) {
            this.model.apply(am.getEntity());
        }

        am.getEntity().setMetadata("mobname", this.getInternalName());
        am.getEntity().setMetadata("mythicmob", "true");
        AbstractEntity rider;
        ActiveMob riderInstance;
        if (this.mount.isPresent()) {
            this.getManager();
            if (!MobExecutor.mountflag && this.getManager().getMythicMob((String)this.mount.get()) != null) {
                this.getManager();
                MobExecutor.mountflag = true;
                if (entity.getVehicle() != null) {
                    entity.getVehicle().remove();
                }

                rider = ((MythicMob)this.getManager().getMythicMob((String)this.mount.get()).get()).spawn(am.getLocation(), level, SpawnReason.SUMMON).getEntity();
                this.getManager();
                MobExecutor.mountflag = false;
                rider.setPassenger(entity);
                riderInstance = this.getManager().getMythicMobInstance(rider);
                am.setMount(riderInstance);
                riderInstance.setParent(am);
            }
        }

        if (this.rider.isPresent()) {
            this.getManager();
            if (!MobExecutor.mountflag && this.getManager().getMythicMob((String)this.rider.get()) != null) {
                this.getManager();
                MobExecutor.mountflag = true;
                rider = ((MythicMob)this.getManager().getMythicMob((String)this.rider.get()).get()).spawn(am.getLocation(), level, SpawnReason.SUMMON).getEntity();
                this.getManager();
                MobExecutor.mountflag = false;
                rider.setPassenger(entity);
                riderInstance = this.getManager().getMythicMobInstance(rider);
                riderInstance.setMount(am);
                riderInstance.setParent(am);
            }
        }

        return am;
    }

    public ActiveMob applyMobVolatileOptions(ActiveMob am) {
        Entity e = am.getEntity().getBukkitEntity();
        if (this.hasFaction()) {
            e.setMetadata("Faction", new FixedMetadataValue(this.getPlugin(), this.getFaction()));
        }

        if (am.getEntity().isMob()) {
            if (this.optionNoAI) {
                am.getEntity().setAI(false);
            }

            if (ConfigExecutor.EnableAIModifiers) {
                if (this.aiNavigator != null) {
                    this.getPlugin().getVolatileCodeHandler().getAIHandler().setNavigationController(am.getEntity(), this.aiNavigator);
                }

                if (this.getAIGoalSelectors() != null) {
                    this.getPlugin().getVolatileCodeHandler().getAIHandler().addPathfinderGoals((LivingEntity)e, this.getAIGoalSelectors());
                }

                if (this.getAITargetSelectors() != null) {
                    this.getPlugin().getVolatileCodeHandler().getAIHandler().addTargetGoals((LivingEntity)e, this.getAITargetSelectors());
                }

                if (!this.aiPathfindingMalus.isEmpty()) {
                    Iterator var3 = this.aiPathfindingMalus.entrySet().iterator();

                    while(var3.hasNext()) {
                        Map.Entry<String, Float> entry = (Map.Entry)var3.next();
                        this.getPlugin().getVolatileCodeHandler().getAIHandler().setPathfindingMalus(am.getEntity(), (String)entry.getKey(), (Float)entry.getValue());
                    }
                }
            }
        }

        if (this.disguise != null && CompatibilityManager.LibsDisguises != null) {
            Schedulers.sync().run(() -> {
                CompatibilityManager.LibsDisguises.setDisguise(am, this.disguise);
            });
        }

        return am;
    }

    public ActiveMob applySpawnModifiers(ActiveMob am) {
        AbstractEntity e = am.getEntity();
        AbstractVector v = e.getVelocity();
        double vl;
        if (this.spawnVelocityXRange) {
            vl = Numbers.randomDouble() * (this.spawnVelocityXMax - this.spawnVelocityX) + this.spawnVelocityX;
            v.setX(vl);
        } else {
            v.setX(this.spawnVelocityX);
        }

        if (this.spawnVelocityYRange) {
            vl = Numbers.randomDouble() * (this.spawnVelocityYMax - this.spawnVelocityY) + this.spawnVelocityY;
            v.setY(vl);
        } else {
            v.setY(this.spawnVelocityY);
        }

        if (this.spawnVelocityZRange) {
            vl = Numbers.randomDouble() * (this.spawnVelocityZMax - this.spawnVelocityZ) + this.spawnVelocityZ;
            v.setZ(vl);
        } else {
            v.setZ(this.spawnVelocityZ);
        }

        e.setVelocity(v);
        return am;
    }

    public void executeSkills(SkillTrigger cause, SkillMetadata data) {
        try {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Running Mechanics for ActiveMob {0} (uuid: {1})", new Object[]{this.internalName, data.getCaster().getEntity().getUniqueId()});
            if (data.getCaster().getEntity() == null || !data.getCaster().getEntity().getWorld().isLoaded()) {
                MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "! Mob is not loaded. Ignoring skills.", new Object[0]);
                return;
            }

            Iterator var3;
            SkillMechanic ms;
            if (data.getIsAsync()) {
                var3 = this.getSkills(cause).iterator();

                while(var3.hasNext()) {
                    ms = (SkillMechanic)var3.next();
                    MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Running Mechanics for ActiveMob '{0}' (uuid: {1})", new Object[]{this.internalName, data.getCaster().getEntity().getUniqueId()});
                    if (!ms.getRunAsync() && ms.isUsableFromCaster(data)) {
                        ms.execute(data.deepClone().setIsAsync(false));
                    }
                }

                Schedulers.async().run(() -> {
                    Iterator var4 = this.getSkills(cause).iterator();

                    while(var4.hasNext()) {
                        SkillMechanic ms2 = (SkillMechanic)var4.next();
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Evaluating SkillMechanic {0}", new Object[]{ms2.getConfigLine()});
                        if (ms2.getRunAsync() && ms2.isUsableFromCaster(data)) {
                            ms2.execute(data);
                        }
                    }

                });
            } else {
                var3 = this.getSkills(cause).iterator();

                while(var3.hasNext()) {
                    ms = (SkillMechanic)var3.next();
                    if (ms.isUsableFromCaster(data)) {
                        ms.execute(data);
                    }
                }
            }
        } catch (Error | Exception var5) {
            MythicLogger.errorMobConfig(this, this.config, "Error processing skills for mob " + this.internalName + ". Mob type may no longer exist, or may have become corrupted by a server crash, and will be removed.");
            if (ConfigExecutor.debugLevel > 0) {
                var5.printStackTrace();
            }

            if (data.getCaster() != null && data.getCaster() instanceof ActiveMob) {
                this.getPlugin().getMobManager().unregisterActiveMob((ActiveMob)data.getCaster());
            }
        }

    }

    public void executeSignalSkill(String signal, SkillMetadata data) {
        MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Executing signal skills...", new Object[0]);
        Iterator var3;
        SkillMechanic ms;
        if (this.signalSkills.containsKey(signal)) {
            var3 = ((List)this.signalSkills.get(signal)).iterator();

            while(var3.hasNext()) {
                ms = (SkillMechanic)var3.next();
                if (ms.isUsableFromCaster(data)) {
                    ms.execute(data);
                }
            }
        }

        var3 = this.getSkills(SkillTriggers.SIGNAL).iterator();

        while(var3.hasNext()) {
            ms = (SkillMechanic)var3.next();
            if (ms.isUsableFromCaster(data)) {
                ms.execute(data);
            }
        }

    }

    public Queue<SkillMechanic> getSkills(SkillTrigger trigger) {
        if (!this.hasCombatSkills || trigger != SkillTriggers.SPAWN && trigger != SkillTriggers.ATTACK && trigger != SkillTriggers.DAMAGED && trigger != SkillTriggers.DEATH) {
            return (Queue)this.skills.getOrDefault(trigger, new LinkedList());
        } else {
            Queue<SkillMechanic> skills = (Queue)this.skills.getOrDefault(trigger, new LinkedList());
            skills.addAll((Collection)this.skills.get(SkillTriggers.COMBAT));
            return skills;
        }
    }

    public boolean hasSkills(SkillTrigger trigger) {
        return this.skills.containsKey(trigger);
    }

    public Queue<SkillMechanic> getTimerSkills() {
        return this.timerSkills;
    }

    public void setEntityType(MythicEntityType type) {
        this.entityTypeString = type.toString();
        this.entityType = type;
        this.entityBaseSpawner = BukkitEntityType.getMythicEntity(this.entityType);
        this.entityBaseSpawner.instantiate(this.config);
        this.config.set("Type", this.entityTypeString);
    }

    public String getEntityTypeString() {
        return this.entityTypeString;
    }

    public BukkitEntityType getMythicEntity() {
        return this.entityBaseSpawner;
    }

    public DespawnMode getDespawnMode() {
        return this.despawnMode;
    }

    public double getPerLevelHealth() {
        return this.lvlModHealth;
    }

    public double getPerLevelDamage() {
        return this.lvlModDamage;
    }

    public double getPerLevelPower() {
        return this.lvlModPower;
    }

    public PlaceholderDouble getArmor() {
        return this.attrArmor;
    }

    public double getPerLevelArmor() {
        return this.lvlModArmor;
    }

    public double getArmor(ActiveMob am) {
        if (this.attrArmor == null) {
            return -1.0;
        } else {
            double level = am.getLevel();
            double attr = this.attrArmor.get(am);
            if (level > 1.0 && this.lvlModArmor > 0.0) {
                attr += this.lvlModArmor * (level - 1.0);
            }

            return attr;
        }
    }

    public PlaceholderDouble getDamage() {
        return this.attrDamage;
    }

    public double getDamage(ActiveMob am) {
        if (this.attrDamage == null) {
            return -1.0;
        } else {
            double level = am.getLevel();
            double damage = this.attrDamage.get(am);
            if (this.lvlModDamage > 0.0) {
                if (level > 1.0) {
                    damage += this.lvlModDamage * (level - 1.0);
                }
            } else if (this.getPlugin().getConfiguration().getScalingEquationDamage() != null) {
                damage = this.getPlugin().getConfiguration().getScalingEquationDamage().setVariable("v", damage).setVariable("l", am.getLevel()).evaluate();
            }

            return damage;
        }
    }

    public double getMovementSpeed(ActiveMob am) {
        if (this.attrMovementSpeed == null) {
            return -1.0;
        } else {
            double level = am.getLevel();
            double attr = this.attrMovementSpeed.get(am);
            if (level > 1.0 && this.lvlModSpeed > 0.0) {
                attr += this.lvlModSpeed * (level - 1.0);
            }

            return attr;
        }
    }

    public double getReviveHealth(ActiveMob am) {
        return this.optionReviveHealth <= 0.0 ? this.getHealth(am) : this.optionReviveHealth;
    }

    public double getFlyingSpeed(ActiveMob am) {
        return this.attrFlyingSpeed.get(am);
    }

    public double getKnockbackResistance(double level) {
        double attr = this.attrKnockbackResist;
        if (level > 1.0 && this.lvlModKBR > 0.0) {
            attr += this.lvlModKBR * (level - 1.0);
        }

        return attr;
    }

    public double getAttackSpeed(double level) {
        double attr = this.attrAttackSpeed;
        if (level > 1.0 && this.lvlModAttackSpeed > 0.0) {
            attr += this.lvlModAttackSpeed * (level - 1.0);
        }

        return attr;
    }

    public boolean hasFaction() {
        return this.faction != null;
    }

    public String getFaction() {
        return this.faction;
    }

    public PlaceholderDouble getHealth() {
        return this.attrHealth;
    }

    public double getHealth(ActiveMob am) {
        if (this.attrHealth == null) {
            return -1.0;
        } else {
            double health = this.attrHealth.get(am);
            double level = am.getLevel();
            if (this.lvlModHealth > 0.0) {
                if (level > 1.0) {
                    health += this.lvlModHealth * (level - 1.0);
                }
            } else if (this.getPlugin().getConfiguration().getScalingEquationHealth() != null) {
                health = this.getPlugin().getConfiguration().getScalingEquationHealth().setVariable("v", health).setVariable("l", am.getLevel()).evaluate();
            }

            return health;
        }
    }

    public boolean getIsInvincible() {
        return this.optionInvincible;
    }

    public boolean usesThreatTable() {
        return this.useThreatTable;
    }

    public boolean usesImmunityTable() {
        return this.useImmunityTable;
    }

    public boolean getThreatTableUseDamageTaken() {
        return this.optionTTFromDamage;
    }

    public boolean getThreatTableDecaysUnreachable() {
        return this.optionTTDecayUnreachable;
    }

    public List<String> getLevelModifiers() {
        return this.levelmods;
    }

    public List<String> getAIGoalSelectors() {
        return this.aiGoalSelectors;
    }

    public List<String> getAITargetSelectors() {
        return this.aiTargetSelectors;
    }

    public boolean hasKillMessages() {
        return this.killMessages != null && this.killMessages.size() > 0;
    }

    public PlaceholderString getKillMessage() {
        return !this.hasKillMessages() ? null : (PlaceholderString)this.killMessages.get(Numbers.randomInt(this.killMessages.size()));
    }

    public double getSpawnVelocityX() {
        return this.spawnVelocityX;
    }

    public double getSpawnVelocityY() {
        return this.spawnVelocityY;
    }

    public double getSpawnVelocityZ() {
        return this.spawnVelocityZ;
    }

    public boolean getIsInteractable() {
        return this.optionInteractable;
    }

    public boolean usesBossBar() {
        return this.useBossBar;
    }

    public int getBossBarRangeSquared() {
        return this.bossBarRangeSq;
    }

    public Optional<AbstractBossBar> getBossBar() {
        if (!this.useBossBar) {
            return Optional.empty();
        } else {
            AbstractBossBar bar = this.getPlugin().getBootstrap().createBossBar(" ", this.bossBarColor, this.bossBarStyle);
            bar.setProgress(1.0);
            if (this.bossBarCreateFog) {
                bar.setCreateFog(true);
            }

            if (this.bossBarDarkenSky) {
                bar.setDarkenSky(true);
            }

            if (this.bossBarPlayMusic) {
                bar.setPlayBossMusic(true);
            }

            return Optional.of(bar);
        }
    }

    public PlaceholderString getBossBarTitle() {
        return this.bossBarTitle;
    }

    public boolean equals(Object o) {
        return o instanceof MobType ? ((MobType)o).getInternalName().equals(this.internalName) : false;
    }

    public String toString() {
        return "MythicMob{" + this.internalName + "}";
    }

    public int compareTo(MythicMob mm) {
        return this.internalName.compareTo(mm.getInternalName());
    }

    public boolean getShowHealthInChat() {
        return this.optionShowHealthInChat;
    }

    public boolean getShowNameOnDamaged() {
        return this.showNameOnDamage;
    }

    public Icon<MobMenuContext> getIcon() {
        String display = this.displayName == null ? "<red>None" : this.displayName.get();
        ArrayList<String> lore  = Lists.newArrayList(new String[]{"<red>", Text.colorizeLegacy("<gold>Type<white>: " + this.entityType.toString()), Text.colorizeLegacy("<gold>Display<white>: " + display), Text.colorizeLegacy("<red> "), Text.colorizeLegacy("<gray>⊳ <yellow>Left-Click to get Mob Egg"), Text.colorizeLegacy("<gray>⊳ <yellow>Right-Click to Edit")});
        
        return new Icon<MobMenuContext>((_state, player) -> {
            return ItemFactory.of(this.cachedMenuItem);
        }, null, null,
                (_state, player) -> {
                    return lore;
                },
                null, null, null, null, null, true);
                
//                IconBuilder.create().itemStack(this.cachedMenuItem).hideFlags(true).name(this.internalName).lore((a) -> {
//            String display = this.displayName == null ? "<red>None" : this.displayName.get();
//            return Lists.newArrayList(new String[]{"<red>", Text.colorizeLegacy("<gold>Type<white>: " + this.entityType.toString()), Text.colorizeLegacy("<gold>Display<white>: " + display), Text.colorizeLegacy("<red> "), Text.colorizeLegacy("<gray>⊳ <yellow>Left-Click to get Mob Egg"), Text.colorizeLegacy("<gray>⊳ <yellow>Right-Click to Edit")});
//        }).click((context, player) -> {
//            player.performCommand("mythicmobs egg get " + this.internalName);
//            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
//        }).rightClick((context, player) -> {
//            MobEditorMenuContext ctx = new MobEditorMenuContext(this, context);
//            this.getPlugin().getMenuManager().getMobEditorMenu().open(player, ctx);
//        }).build();
    }

    public MythicBukkit getPlugin() {
        return this.plugin;
    }

    public MobExecutor getManager() {
        return this.manager;
    }

    public Pack getPack() {
        return this.pack;
    }

    public File getFile() {
        return this.file;
    }

    public MythicConfig getConfig() {
        return this.config;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public ItemStack getCachedMenuItem() {
        return this.cachedMenuItem;
    }

    public MythicEntityType getEntityType() {
        return this.entityType;
    }

    public BukkitEntityType getEntityBaseSpawner() {
        return this.entityBaseSpawner;
    }

    public PlaceholderString getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(PlaceholderString displayName) {
        this.displayName = displayName;
    }

    public String getEggDisplay() {
        return this.eggDisplay;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public MobModel getModel() {
        return this.model;
    }

    public DropTable getDropTable() {
        return this.dropTable;
    }

    public DropTable getEquipmentTable() {
        return this.equipmentTable;
    }

    public PlaceholderDouble getAttrHealth() {
        return this.attrHealth;
    }

    public void setAttrHealth(PlaceholderDouble attrHealth) {
        this.attrHealth = attrHealth;
    }

    public PlaceholderDouble getAttrDamage() {
        return this.attrDamage;
    }

    public void setAttrDamage(PlaceholderDouble attrDamage) {
        this.attrDamage = attrDamage;
    }

    public PlaceholderDouble getAttrArmor() {
        return this.attrArmor;
    }

    public void setAttrArmor(PlaceholderDouble attrArmor) {
        this.attrArmor = attrArmor;
    }

    public PlaceholderDouble getAttrMovementSpeed() {
        return this.attrMovementSpeed;
    }

    public void setAttrMovementSpeed(PlaceholderDouble attrMovementSpeed) {
        this.attrMovementSpeed = attrMovementSpeed;
    }

    public PlaceholderDouble getAttrFlyingSpeed() {
        return this.attrFlyingSpeed;
    }

    public void setAttrFlyingSpeed(PlaceholderDouble attrFlyingSpeed) {
        this.attrFlyingSpeed = attrFlyingSpeed;
    }

    public double getAttrKnockbackResist() {
        return this.attrKnockbackResist;
    }

    public void setAttrKnockbackResist(double attrKnockbackResist) {
        this.attrKnockbackResist = attrKnockbackResist;
    }

    public double getAttrFollowRange() {
        return this.attrFollowRange;
    }

    public void setAttrFollowRange(double attrFollowRange) {
        this.attrFollowRange = attrFollowRange;
    }

    public double getAttrAttackSpeed() {
        return this.attrAttackSpeed;
    }

    public void setAttrAttackSpeed(double attrAttackSpeed) {
        this.attrAttackSpeed = attrAttackSpeed;
    }

    public double getOptionReviveHealth() {
        return this.optionReviveHealth;
    }

    public void setOptionReviveHealth(double optionReviveHealth) {
        this.optionReviveHealth = optionReviveHealth;
    }

    public Boolean getOptionLockPitch() {
        return this.optionLockPitch;
    }

    public Map<String, PlaceholderDouble> getStats() {
        return this.stats;
    }

    public Map<String, Double> getDamageModifiers() {
        return this.damageModifiers;
    }

    public Map<String, Double> getEntityDamageModifiers() {
        return this.entityDamageModifiers;
    }

    public String getAiNavigator() {
        return this.aiNavigator;
    }

    public boolean isUsingTimers() {
        return this.usingTimers;
    }

    public int getNoDamageTicks() {
        return this.noDamageTicks;
    }

    public int getMaxAttackRange() {
        return this.maxAttackRange;
    }

    public int getMaxAttackableRange() {
        return this.maxAttackableRange;
    }

    public int getMaxThreatDistance() {
        return this.maxThreatDistance;
    }

    public boolean isUseCustomNameplate() {
        return this.useCustomNameplate;
    }

    public Boolean getRepeatAllSkills() {
        return this.repeatAllSkills;
    }

    public Boolean getPreventOtherDrops() {
        return this.preventOtherDrops;
    }

    public Boolean getPreventRandomEquipment() {
        return this.preventRandomEquipment;
    }

    public Boolean getPreventLeashing() {
        return this.preventLeashing;
    }

    public Boolean getPreventRename() {
        return this.preventRename;
    }

    public Boolean getPreventEndermanTeleport() {
        return this.preventEndermanTeleport;
    }

    public Boolean getPreventItemPickup() {
        return this.preventItemPickup;
    }

    public Boolean getPreventSilverfishInfection() {
        return this.preventSilverfishInfection;
    }

    public Boolean getPreventSunburn() {
        return this.preventSunburn;
    }

    public Boolean getPreventExploding() {
        return this.preventExploding;
    }

    public Boolean getPreventMobKillDrops() {
        return this.preventMobKillDrops;
    }

    public Boolean getPreventTransformation() {
        return this.preventTransformation;
    }

    public Boolean getPreventMounts() {
        return this.preventMounts;
    }

    public Boolean getPassthroughDamage() {
        return this.passthroughDamage;
    }

    public Boolean getApplyInvisibility() {
        return this.applyInvisibility;
    }

    public Boolean getDigOutOfGround() {
        return this.digOutOfGround;
    }

    public Boolean getUsesHealthBar() {
        return this.usesHealthBar;
    }

    public double getSpawnVelocityXMax() {
        return this.spawnVelocityXMax;
    }

    public double getSpawnVelocityYMax() {
        return this.spawnVelocityYMax;
    }

    public double getSpawnVelocityZMax() {
        return this.spawnVelocityZMax;
    }

    public Boolean getSpawnVelocityXRange() {
        return this.spawnVelocityXRange;
    }

    public Boolean getSpawnVelocityYRange() {
        return this.spawnVelocityYRange;
    }

    public Boolean getSpawnVelocityZRange() {
        return this.spawnVelocityZRange;
    }

    public String getDisguise() {
        return this.disguise;
    }

    public boolean isFakePlayer() {
        return this.fakePlayer;
    }
}
