package net.riblab.tradecore.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.job.skill.JobSkillService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

/**
 * スキルリセットUI
 */
public class UISkillRespec {

    public static PaginatedGui open(Player player) {
        //リスペック費用 ＝ 習得したスキルの数 * 100
        double fee = Arrays.stream(JobType.values()).mapToDouble(value -> JobSkillService.getImpl().getLearntSkillCount(player, value) * 100).sum();
        if (fee == 0) {
            player.sendMessage("スキルを何も習得していません！");
            return null;
        }

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(fee + "RIBを払ってスキルをリセットしますか？"))
                .rows(1)
                .disableAllInteractions()
                .create();

        GuiItem confirmButton = new GuiItem(new ItemCreator(Material.GREEN_STAINED_GLASS_PANE).setName(Component.text("はい")).create(),
                event -> {
                    doRespec(event, fee);
                    gui.close(player);
                });
        gui.setItem(3, confirmButton);
        GuiItem cancelButton = new GuiItem(new ItemCreator(Material.RED_STAINED_GLASS_PANE).setName(Component.text("いいえ")).create(),
                event -> gui.close(player));
        gui.setItem(5, cancelButton);


        gui.open(player);

        return gui;
    }

    private static void doRespec(InventoryClickEvent event, double fee) {
        double balance = TCEconomy.getImpl().getBalance((Player) event.getWhoClicked());
        if (balance < fee) {
            event.getWhoClicked().sendMessage("お金が足りません！");
            return;
        }

        TCEconomy.getImpl().withdrawPlayer(((Player) event.getWhoClicked()), fee);
        JobSkillService.getImpl().resetPlayerJobSkillData(((Player) event.getWhoClicked()));
    }
}
