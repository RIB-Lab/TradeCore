package net.riblab.tradecore.entity.mob;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface FakeVillagerService {
    static FakeVillagerService getImpl() {
        return FakeVillagerServiceImpl.INSTANCE;
    }

    /**
     * プレイヤーの視線の先のブロックの上に村人を召喚する
     */
    @ParametersAreNonnullByDefault
    void spawnFakeVillager(Player player, String name, Location spawnLocation);

    /**
     * 村人を削除する。呼ばないと村人がクライアントに永遠に残り続ける
     */
    @ParametersAreNonnullByDefault
    void tryDeSpawnFakeVillager(Player player);

    /**
     * 現在プレイヤーが召喚した村人のID取得
     */
    @Nullable
    Integer getCurrentID(@Nullable Player player);
}