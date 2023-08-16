package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.craft.TCRecipes;
import net.riblab.tradecore.craft.VanillaCraftHandler;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.TCMob;
import net.riblab.tradecore.mob.TCMobs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static net.riblab.tradecore.Materials.transparentBlocks;


public final class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    @Getter
    private EconomyImplementer economy;
    private VaultHook vaultHook;
    @Getter
    private ConfigManager configManager;
    @Getter
    private ProtocolManager protocolManager;
    private EventHandler eventHandler;
    
    public TradeCore() {
        instance = this;
    }

    public static TradeCore getInstance() {
        return instance;
    }
    
    @Getter
    private static boolean isWGLoaded;
    
    public static final String merchantName = "買い取り商";
    
    static {
        //安全にenumを初期化
        TCItems.values();
        TCMobs.values();
        LootTables.values();
        TCRecipes.values();
    }
    
    @Override
    public void onLoad(){
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true)); // Load with verbose output

        CommandAPICommand setMoneyCommand = new CommandAPICommand("setmoney")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new DoubleArgument("money", 0, Integer.MAX_VALUE))
                .executesPlayer((player, args) -> {
                    Player player1 = (Player) args.get(0);
                    Double money = (Double) args.get(1);

                    economy.withdrawPlayer(player1, economy.getBalance(player1));
                    economy.depositPlayer(player1, money);
                });
        setMoneyCommand.setPermission(CommandPermission.OP);
        setMoneyCommand.register();

        CommandAPICommand tcGiveCommand = new CommandAPICommand("tcgive")
                .withArguments(TCItems.customITCItemArgument("item"))
                .withArguments(new IntegerArgument("amount", 1, 1000))
                .executesPlayer((player, args) -> {
                    ITCItem itcItem = (ITCItem) args.get(0);
                    int amount = (int) args.get(1);
                    ItemStack newStack = itcItem.getItemStack();
                    newStack.setAmount(amount);
                    player.getInventory().addItem(newStack);
                });
        tcGiveCommand.setPermission(CommandPermission.OP);
        tcGiveCommand.register();

        CommandAPICommand sellCommand = new CommandAPICommand("sell")
                .executesPlayer((player, args) -> {
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    FakeVillagerService.spawnFakeVillager(player, merchantName, spawnLocation);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLocation, 10,1,1,1);
                });
        sellCommand.setPermission(CommandPermission.NONE);
        sellCommand.register();

        CommandAPICommand spawnCommand = new CommandAPICommand("tcspawn")
                .withArguments(TCMobs.customITCMobArgument("mobname"))
                .executesPlayer((player, args) -> {
                    TCMob type = (TCMob) args.get(0);
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());
                    
                    CustomMobService.spawn(player, spawnLocation, type);
                });
        spawnCommand.setPermission(CommandPermission.OP);
        spawnCommand.register();
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager();
        configManager.load();
        eventHandler = new EventHandler();
        new VanillaCraftHandler();
        
        economy = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");

        CommandAPI.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!economy.hasAccount(player))
                economy.createPlayerAccount(player);
            
            addSlowDig(player);
        });

        protocolManager = ProtocolLibrary.getProtocolManager();

        //所持金と投票券表示
        new BukkitRunnable(){
            @Override
            public void run() {
                String negativeSpace = TCResourcePackData.IconsFont.NEGATIVE_SPACE.get_char();
                Bukkit.getOnlinePlayers().forEach(player ->{
                    int balance = (int)economy.getBalance(player);
                    int tickets = economy.getPlayTickets(player);
                    Component text = Component.text("");
                    text = text.append(Component.text(negativeSpace + negativeSpace + negativeSpace + negativeSpace + TCResourcePackData.IconsFont.COIN.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + balance).font(TCResourcePackData.yPlus12FontName));
                    text = text.append(Component.text("                         " + TCResourcePackData.IconsFont.VOTE_TICKET.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + tickets).font(TCResourcePackData.yPlus12FontName));
                    player.sendActionBar(text);
                });
            }
        }.runTaskTimer(this, 0, 20);
        
        //定期的にコンフィグを保存
        new BukkitRunnable(){
            @Override
            public void run() {
                configManager.save();
            }
        }.runTaskTimer(this, 0, 3600);
        
        //10分に1回プレイチケット配布
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> economy.depositTickets(player, 1));
            }
        }.runTaskTimer(this, 0, 12000);

        //買い取り商人
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        Integer integer = FakeVillagerService.getCurrentID(player);
                        if (integer != null && id == integer) {
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    UISell.open(event.getPlayer());
                                }
                            }.runTaskLater(TradeCore.getInstance(), 0);
                        }
                    }
                }
        );
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        CommandAPI.onDisable();
        configManager.save();
        
        CustomMobService.deSpawnAll();
        
        Bukkit.getOnlinePlayers().forEach(player -> removeSlowDig(player));
    }

    /**
     * カスタムブロック破壊を実装
     * @param player
     */
    public static void addSlowDig(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, -1, false, false), true);
    }

    /**
     * カスタムブロック破壊を除去
     * @param player
     */
    public static void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    /**
     * BukkitのOnDisableでエラーが出ないようにクラスを強制的にロードする
     * @param klass
     * @param <T>
     * @return
     */
    public static <T> Class<T> forceInit(Class<T> klass) {
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
        return klass;
    }
    
    public static void dropItemByLootTable(Block block, Map<Float, ITCItem> table){
        Random random = new Random();
        table.forEach((aFloat, itcItem) -> {
            float rand = random.nextFloat();
            if(rand < aFloat){
                block.getWorld().dropItemNaturally(block.getLocation(), itcItem.getItemStack());
            }
        });
    }
    
    public static void trySpawnMob(Player player, Block block, Map<TCMob, Float> table){
        Random random = new Random();
        table.forEach((itcmob, aFloat) -> {
            float rand = random.nextFloat();
            if(rand < aFloat){
                Location safeLocation = findSafeLocationToSpawn(block, 5);
                if(safeLocation != null)
                    CustomMobService.spawn(player, safeLocation, itcmob);
            }
        });
    }
    
    public static Location findSafeLocationToSpawn(Block block, int radius){
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Block tryBlock = block.getRelative(random.nextInt(radius * 2) - radius + 1, random.nextInt(radius * 2) -radius, random.nextInt(radius * 2) -radius);
            if(tryBlock.getType() != Material.AIR || tryBlock.getRelative(BlockFace.UP).getType() != Material.AIR)
                continue;
            
            return tryBlock.getLocation().add(new Vector(0.5f, 0, 0.5f));
        }
        
        return null; //何回探しても安全な場所がなかったらモブのスポーンを諦める
    }
}
