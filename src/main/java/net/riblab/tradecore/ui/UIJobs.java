package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.job.data.IJobData;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.job.skill.IJobSkill;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.job.skill.JobSkills;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * ジョブスキルの習得、確認UI
 */
final class UIJobs implements IUI{

    private static final JobDataService JOB_SERVICE = JobDataService.getImpl();
    private static final JobSkillService skillHandler = JobSkillService.getImpl();

    @Override
    public BaseGui open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("ジョブ選択"))
                .rows(3)
                .disableAllInteractions()
                .create();

        for (JobType type : JobType.values()) {
            IJobData IJobData = JOB_SERVICE.getJobData(player, type);
            ItemStack jobIcon = new ItemCreator(type.getUiMaterial()).setName(Component.text(type.getName()).decoration(TextDecoration.ITALIC, false))
                    .hideAttributes()
                    .setLore(Component.text("現在のレベル:" + IJobData.getLevel()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))//TODO:次のレベルまでの経験値を表示
                    .create();
            GuiItem jobButton = new GuiItem(jobIcon,
                    event -> open(player, type));
            gui.addItem(jobButton);
        }

        gui.open(player);

        return gui;
    }

    public static PaginatedGui open(Player player, JobType type) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(type.getName() + " 未使用スキルポイント：" + skillHandler.getUnSpentSkillPoints(player, type)))
                .rows(3)
                .disableAllInteractions()
                .create();

        List<Class<? extends IJobSkill>> availableSkills = JobSkills.getAvailableSkills(type);
        for (Class<? extends IJobSkill> availableSkill : availableSkills) {
            GuiItem skillButton = new GuiItem(new ItemCreator(Material.DIRT).setName(Component.text(JobSkills.getSkillName(availableSkill)).decoration(TextDecoration.ITALIC, false))
                    .setLore(Component.text("現在のレベル:" + skillHandler.getSkillLevel(player, type, availableSkill)).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))
                    .addLore(Component.text("最大レベル:" + JobSkills.getMaxLevel(availableSkill)).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE))
                    .addLores(JobSkills.getSkillLore(availableSkill)).create(),
                    event -> tryLearnSkill(event, availableSkill, type));
            gui.addItem(skillButton);
        }

        gui.open(player);

        return gui;
    }

    /**
     * スキルを習得しようとする
     */
    public static void tryLearnSkill(InventoryClickEvent event, Class<? extends IJobSkill> skillType, JobType type) {
        if (skillHandler.getUnSpentSkillPoints((Player) event.getWhoClicked(), type) <= 0) {
            event.getWhoClicked().sendMessage(Component.text("スキルポイントが足りません！"));
            return;
        }

        if (skillHandler.getSkillLevel((Player) event.getWhoClicked(), type, skillType) >= JobSkills.getMaxLevel(skillType)) {
            event.getWhoClicked().sendMessage(Component.text("既に最大レベルです！"));
            return;
        }

        skillHandler.learnSkill((Player) event.getWhoClicked(), type, skillType);
        //画面を更新
        open((Player) event.getWhoClicked(), type);
    }
}
