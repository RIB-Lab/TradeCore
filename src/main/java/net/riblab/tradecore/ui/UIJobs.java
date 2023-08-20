package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.ItemCreator;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.job.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * ジョブスキルの習得、確認UI
 */
public class UIJobs {

    private static final JobHandler jobHandler = TradeCore.getInstance().getJobHandler();
    private static final JobSkillHandler skillHandler = TradeCore.getInstance().getJobSkillHandler();

    public static PaginatedGui open(Player player){
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("スキル選択"))
                .rows(3)
                .disableAllInteractions()
                .create();

        for (JobData.JobType type : JobData.JobType.values()) {
            JobData jobData = jobHandler.getJobData(player, type);
            ItemStack jobIcon = new ItemCreator(type.getUiMaterial()).setName(Component.text(type.getName()).decoration(TextDecoration.ITALIC, false))
                    .hideAttributes()
                    .setLore(Component.text("現在のレベル:" + jobData.getLevel()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))//TODO:次のレベルまでの経験値を表示
                    .create();
            GuiItem jobButton = new GuiItem(jobIcon,
                    event -> open(player, type));
            gui.addItem(jobButton);
        }

        gui.open(player);

        return gui;
    }
    
    public static PaginatedGui open(Player player, JobData.JobType type) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(type.getName() + " 未使用スキルポイント：" + skillHandler.getUnSpentSkillPoints(player, type)))
                .rows(3)
                .disableAllInteractions()
                .create();

        List<Class<? extends JobSkill>> availableSkills = JobSkills.getAvailableSkills(type);
        for (Class<? extends JobSkill> availableSkill : availableSkills) {
            GuiItem skillButton = new GuiItem(new ItemCreator(Material.DIRT).setName(Component.text(JobSkills.getSkillName(availableSkill)).decoration(TextDecoration.ITALIC, false))
                    .setLore(Component.text("現在のレベル:" + skillHandler.getSkillLevel(player, type, availableSkill)).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE)).create(),
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
        if(skillHandler.getUnSpentSkillPoints((Player)event.getWhoClicked(), type) <= 0){
            event.getWhoClicked().sendMessage(Component.text("スキルポイントが足りません！"));
            return;
        }
        
        skillHandler.learnSkill((Player)event.getWhoClicked(), type, skillType);
        //画面を更新
        open((Player)event.getWhoClicked(), type);
    }
}
