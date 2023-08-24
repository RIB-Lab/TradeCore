package net.riblab.tradecore;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.attribute.ITCItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

/**
 * 進捗管理クラス
 */
public class AdvancementService {

    private AdvancementTab primitiveAgeTab;
    private UltimateAdvancementAPI getAPI(){
        return TradeCore.getInstance().getAdvancementAPI();
    };
    
    public AdvancementService(){
        getAPI().disableVanillaAdvancements();
        
        primitiveAgeTab = getAPI().createAdvancementTab("primitive_age");

        AdvancementDisplay gatherPebbleAdvDisplay = new AdvancementDisplay(TCItems.PEBBLE.get().getItemStack(), "ようこそ", AdvancementFrameType.TASK, true, true, 0, 0, "素手で草を壊して小石を集めた");
        RootAdvancement gatherPebbleAdv = new RootAdvancement(primitiveAgeTab, "adv_gather_pebble", gatherPebbleAdvDisplay, "textures/block/stone.png", 1);
        
        AdvancementDisplay craftHatchetAdvDisplay = new AdvancementDisplay(TCItems.HATCHET.get().getItemStack(), "素手で木は殴れない", AdvancementFrameType.TASK, true, true, 1, 0, "小石で葉ブロックを壊して入手した棒と小石をクラフトして、最初のツールを入手した");
        BaseAdvancement craftHatchetAdv = new BaseAdvancement("adv_craft_hatchet", craftHatchetAdvDisplay, gatherPebbleAdv, 1);
        
        
        primitiveAgeTab.registerAdvancements(gatherPebbleAdv, craftHatchetAdv);
        primitiveAgeTab.automaticallyShowToPlayers();
        
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, PlayerAttemptPickupItemEvent.class, playerPickItemEvent -> {
            ITCItem itcItem = TCItems.toTCItem(playerPickItemEvent.getItem().getItemStack());
            if(itcItem == null)
                return;
            
            if(itcItem.equals(TCItems.PEBBLE.get())){
                gatherPebbleAdv.incrementProgression(playerPickItemEvent.getPlayer());
            }
        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, CraftItemEvent.class, event -> {
            ITCItem itcItem = TCItems.toTCItem(event.getRecipe().getResult());
            if(itcItem == null || !(event.getWhoClicked() instanceof Player player))
                return;

            if(itcItem.equals(TCItems.HATCHET.get())){
                craftHatchetAdv.incrementProgression(player);
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> primitiveAgeTab.showTab(player));
    }
}
