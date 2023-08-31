package net.riblab.tradecore.ui;

/**
 * このプラグインのカスタムUIたち
 */
public enum UIs {
    ADMINSHOP(new UIAdminShop()),
    CRAFTING(new UICraftingTable()),
    DUNGEONENTER(new UIDungeonEnter()),
    FURNACE(new UIFurnace()),
    JOBS(new UIJobs()),
    SELL(new UISell()),
    SKILLRESPEC(new UISkillRespec());
    
    private final IUI ui;

    UIs(IUI ui) {
        this.ui = ui;
    }
    
    public IUI get(){
        return ui;
    }
}
