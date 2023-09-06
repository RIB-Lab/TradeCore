package net.riblab.tradecore;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.riblab.tradecore.config.DataService;
import org.junit.jupiter.api.*;

public class TradeCoreTest {

    private static TradeCore plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(TradeCore.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("空のコンフィグが存在するか確認")
    public void testConfigs() {
        Assertions.assertNotNull(DataService.getImpl().getJobDatas());
        Assertions.assertNotNull(DataService.getImpl().getCurrencyData());
    }
}
