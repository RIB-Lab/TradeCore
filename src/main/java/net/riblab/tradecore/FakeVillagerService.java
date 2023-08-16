package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * ダミーの村人をクライアントに出現させるためのパケット管理システム
 */
public class FakeVillagerService {

    /**
     * プレイヤーとそのプレイヤーの元に送られたダミーの村人のMap
     */
    private static Map<Player, Integer> idMap = new HashMap<>();

    /**
     * プレイヤーの視線の先のブロックの上に村人を召喚する
     *
     * @param player
     */
    public static void spawnFakeVillager(Player player, String name, Location spawnLocation) {
        if (idMap.containsKey(player))
            tryDeSpawnFakeVillager(player);

        PacketContainer spawnPacket = TradeCore.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        int entityID = new Random().nextInt();
        idMap.put(player, entityID);
        spawnPacket.getIntegers().write(0, entityID);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.VILLAGER);

        spawnPacket.getDoubles()
                .write(0, spawnLocation.getX())
                .write(1, spawnLocation.getY())
                .write(2, spawnLocation.getZ());

        float yaw = player.getEyeLocation().getYaw() + 180;
        if ((yaw > 360))
            yaw -= 360;

        spawnPacket.getBytes()
                .write(0, (byte) 0)
                .write(1, (byte) (player.getEyeLocation().getPitch() * (256.0F / 360.0F)))
                .write(2, (byte) (yaw * (256.0F / 360.0F)));

        TradeCore.getInstance().getProtocolManager().sendServerPacket(player, spawnPacket);

        //ここからメタデータ
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        final WrappedDataWatcher.Serializer chatSerializer =
                WrappedDataWatcher.Registry.getChatComponentSerializer(true);

        final Optional<Object> optChatField =
                Optional.of(WrappedChatComponent.fromChatMessage(name)[0].getHandle());

        final List<WrappedDataValue> dataValues = List.of(
                new WrappedDataValue(2, chatSerializer, optChatField),
                new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true),//hascustomname
                new WrappedDataValue(5, WrappedDataWatcher.Registry.get(Boolean.class), true) //hasgravity
        );
        packet.getIntegers().write(0, entityID);
        packet.getDataValueCollectionModifier().write(0, dataValues);

        TradeCore.getInstance().getProtocolManager().sendServerPacket(player, packet);
    }

    /**
     * 村人を削除する。呼ばないと村人がクライアントに永遠に残り続ける
     *
     * @param player
     */
    public static void tryDeSpawnFakeVillager(Player player) {
        Integer entityID = idMap.remove(player);
        if (entityID == null)
            return;

        List<Integer> entityIDList = new ArrayList<>();
        entityIDList.add(entityID);

        PacketContainer deSpawnPacket = TradeCore.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        deSpawnPacket.getIntLists().write(0, entityIDList);

        TradeCore.getInstance().getProtocolManager().sendServerPacket(player, deSpawnPacket);
    }

    /**
     * 現在プレイヤーが召喚した村人のID取得
     * @param player
     * @return
     */
    public static Integer getCurrentID(Player player) {
        return idMap.get(player);
    }
}
