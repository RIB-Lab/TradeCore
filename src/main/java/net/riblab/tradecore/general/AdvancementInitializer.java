package net.riblab.tradecore.general;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.TaskAdvancement;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

/**
 * 進捗管理クラス
 */
public class AdvancementInitializer {

    private AdvancementTab primitiveAgeTab;
    private UltimateAdvancementAPI getAPI(){
        return TradeCore.getInstance().getAdvancementAPI();
    };
    
    public AdvancementInitializer(){
        getAPI().disableVanillaAdvancements();
        
        primitiveAgeTab = getAPI().createAdvancementTab("primitive_age");

        AdvancementDisplay gatherPebbleAdvDisplay = new AdvancementDisplay(TCItems.PEBBLE.get().getItemStack(), "ようこそ", AdvancementFrameType.TASK, true, true, 0, 0, "素手で草を壊して小石を集めた");
        RootAdvancement gatherPebbleAdv = new RootAdvancement(primitiveAgeTab, "adv_gather_pebble", gatherPebbleAdvDisplay, "textures/block/stone.png", 1);
        
        AdvancementDisplay craftHatchetAdvDisplay = new AdvancementDisplay(TCItems.HATCHET.get().getItemStack(), "知恵          ", AdvancementFrameType.TASK, true, true, 1, 0, "小石で葉ブロックを壊して入手した棒と小石をクラフトして、最初のツールを入手した");
        BaseAdvancement craftHatchetAdv = new BaseAdvancement("adv_craft_hatchet", craftHatchetAdvDisplay, gatherPebbleAdv, 1);

        AdvancementDisplay craftTableAdvDisplay = new AdvancementDisplay(Material.CRAFTING_TABLE, "作業(を依頼する)台", AdvancementFrameType.TASK, true, true, 2, 0, "ハチェットで木を伐って丸太を4つ集めて、作業台をクラフトした");
        BaseAdvancement craftTableAdv = new BaseAdvancement("adv_craft_table", craftTableAdvDisplay, craftHatchetAdv, 1);

        AdvancementDisplay woodenAxeAdvDisplay = new AdvancementDisplay(TCItems.WOODEN_AXE.get().getItemStack(), "高効率伐採", AdvancementFrameType.TASK, true, true, 3, 0, "木の斧で伐採した");
        BaseAdvancement woodenAxeAdv = new BaseAdvancement("adv_wooden_axe", woodenAxeAdvDisplay, craftTableAdv, 1);

        
        AdvancementDisplay woodenComponentAdvDisplay = new AdvancementDisplay(TCItems.WOODEN_COMPONENT.get().getItemStack(), "色々集めた", AdvancementFrameType.TASK, true, true, 4, 0, "wikiを見ながら木の強化資材の材料を集めた");
        MultiTasksAdvancement woodenComponentAdv = new MultiTasksAdvancement("adv_wooden_component", woodenComponentAdvDisplay, woodenAxeAdv, 3);

        TaskAdvancement woodpulpTask = new TaskAdvancement("task_woodpulp", woodenComponentAdv);
        TaskAdvancement dustTask = new TaskAdvancement("task_dust", woodenComponentAdv);
        TaskAdvancement mossTask = new TaskAdvancement("task_moss", woodenComponentAdv);
        
        woodenComponentAdv.registerTasks(woodpulpTask, dustTask, mossTask);
        

        AdvancementDisplay stoneAxeAdvDisplay = new AdvancementDisplay(TCItems.STONE_AXE.get().getItemStack(), "湧き出る丸太", AdvancementFrameType.TASK, true, true, 5, 0, "石の斧で伐採した");
        BaseAdvancement stoneAxeAdv = new BaseAdvancement("adv_stone_axe", stoneAxeAdvDisplay, woodenComponentAdv, 1);

        
        AdvancementDisplay stoneComponentAdvDisplay = new AdvancementDisplay(TCItems.STONE_COMPONENT.get().getItemStack(), "さらに色々集めた", AdvancementFrameType.TASK, true, true, 6, 0, "wikiを見ながら石の強化資材の材料を集めた");
        MultiTasksAdvancement stoneComponentAdv = new MultiTasksAdvancement("adv_stone_component", stoneComponentAdvDisplay, stoneAxeAdv, 4);

        TaskAdvancement andesiteStoneTask = new TaskAdvancement("task_andestitestone", stoneComponentAdv);
        TaskAdvancement graniteStoneTask = new TaskAdvancement("task_granitestone", stoneComponentAdv);
        TaskAdvancement dioriteStoneTask = new TaskAdvancement("task_dioritestone", stoneComponentAdv);
        TaskAdvancement roundStoneTask = new TaskAdvancement("task_roundstone", stoneComponentAdv);
        
        stoneComponentAdv.registerTasks(andesiteStoneTask, graniteStoneTask, dioriteStoneTask, roundStoneTask);
        
        
        AdvancementDisplay ironAxeAdvDisplay = new AdvancementDisplay(TCItems.IRON_AXE.get().getItemStack(), "鉄金器時代", AdvancementFrameType.GOAL, true, true, 7, 0, "鉄の斧/金の斧で木を伐った");
        BaseAdvancement ironAxeAdv = new BaseAdvancement("adv_iron_axe", ironAxeAdvDisplay, stoneComponentAdv, 1);
        
        primitiveAgeTab.registerAdvancements(gatherPebbleAdv, craftHatchetAdv, craftTableAdv, woodenAxeAdv, woodenComponentAdv, stoneAxeAdv, stoneComponentAdv, ironAxeAdv);
        primitiveAgeTab.automaticallyShowToPlayers();
        
        //TODO:プレイヤー単位で実績を管理。ロード時にプレイヤーが未取得の実績を洗い出してそこにだけイベントが飛ぶようにする
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, PlayerAttemptPickupItemEvent.class, playerPickItemEvent -> {
            ITCItem itcItem = TCItems.toTCItem(playerPickItemEvent.getItem().getItemStack());
            if(itcItem == null)
                return;
            
            Player player = playerPickItemEvent.getPlayer();
            check(itcItem, TCItems.PEBBLE, gatherPebbleAdv, player);
            check(itcItem, TCItems.WOODPULP, woodpulpTask, player);
            check(itcItem, TCItems.DUST, dustTask, player);
            check(itcItem, TCItems.MOSS, mossTask, player);
            check(itcItem, TCItems.ANDESITE_STONE, andesiteStoneTask, player);
            check(itcItem, TCItems.GRANITE_STONE, graniteStoneTask, player);
            check(itcItem, TCItems.DIORITE_STONE, dioriteStoneTask, player);
            check(itcItem, TCItems.ROUND_STONE, roundStoneTask, player);
            
        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, CraftItemEvent.class, event -> {
            if(!(event.getWhoClicked() instanceof Player player))
                return;
            
            if(event.getRecipe().getResult().getType() == Material.CRAFTING_TABLE){
                craftTableAdv.incrementProgression(player);
            }
            
            ITCItem itcItem = TCItems.toTCItem(event.getRecipe().getResult());
            if(itcItem == null)
                return;

            check(itcItem, TCItems.HATCHET, craftHatchetAdv, player);
        });
        
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, BlockBreakEvent.class, event -> {
            ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
            if(itcItem == null)
                return;
            
            Player player = event.getPlayer();
            check(itcItem, TCItems.WOODEN_AXE, woodenAxeAdv, player);
            check(itcItem, TCItems.STONE_AXE, stoneAxeAdv, player);
            check(itcItem, TCItems.IRON_AXE, ironAxeAdv, player);
        });

        Bukkit.getOnlinePlayers().forEach(player -> primitiveAgeTab.showTab(player));
    }
    
    private static void check(ITCItem item, TCItems type, Advancement adv, Player player){
        if(item.equals(type.get())){
            adv.incrementProgression(player);
        }
    }
}
