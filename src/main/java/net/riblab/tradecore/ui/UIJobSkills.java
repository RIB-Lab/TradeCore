package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.ItemCreator;
import net.riblab.tradecore.Materials;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;
import net.riblab.tradecore.job.JobSkillHandler;
import net.riblab.tradecore.job.JobSkills;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * ジョブスキルの習得、確認UI
 */
public class UIJobSkills {

    private static final JobSkillHandler handler = TradeCore.getInstance().getJobSkillHandler();
    
    public static PaginatedGui open(Player player, JobData.JobType type) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(type.getName() + " 未使用スキルポイント：" + handler.getUnSpentSkillPoints(player, type)))
                .rows(3)
                .disableAllInteractions()
                .create();

        List<Class<? extends JobSkill>> availableSkills = JobSkills.getAvailableSkills(type);
        for (Class<? extends JobSkill> availableSkill : availableSkills) {
            GuiItem skillButton = new GuiItem(new ItemCreator(Material.DIRT).setName(Component.text(JobSkills.getSkillName(availableSkill)))
                    .setLore(Component.text("現在のレベル:" + handler.getSkillLevel(player, type, availableSkill))).create(),
                    event -> tryLearnSkill(event, availableSkill, type));
            gui.addItem(skillButton);
        }

        gui.open(player);

        return gui;
    }

    /**
     * スキルを習得しようとする
     */
    public static void tryLearnSkill(InventoryClickEvent event, Class<? extends JobSkill> skillType, JobData.JobType type){
        if(handler.getUnSpentSkillPoints((Player)event.getWhoClicked(), type) <= 0){
            event.getWhoClicked().sendMessage(Component.text("スキルポイントが足りません！"));
            return;
        }
        
        handler.learnSkill((Player)event.getWhoClicked(), type, skillType);
        //画面を更新
        open((Player)event.getWhoClicked(), type);
    }
}
