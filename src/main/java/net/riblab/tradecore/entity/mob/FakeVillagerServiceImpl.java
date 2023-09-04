package net.riblab.tradecore.entity.mob;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ダミーの村人をクライアントに出現させるためのパケット管理システム
 */
enum FakeVillagerServiceImpl implements FakeVillagerService {
    INSTANCE;

    /**
     * プレイヤーとそのプレイヤーの元に送られたダミーの村人のMap
     */
    private final Map<Player, Integer> idMap = new HashMap<>();

    @Override
    @ParametersAreNonnullByDefault
    public void spawnFakeVillager(Player player, String name, Location spawnLocation) {
        if (idMap.containsKey(player))
            tryDeSpawnFakeVillager(player);

        PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);

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

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnPacket);

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

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    public void tryDeSpawnFakeVillager(Player player) {
        Integer entityID = idMap.remove(player);
        if (Objects.isNull(entityID))
            return;

        List<Integer> entityIDList = new ArrayList<>();
        entityIDList.add(entityID);

        PacketContainer deSpawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        deSpawnPacket.getIntLists().write(0, entityIDList);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, deSpawnPacket);
    }

    @Override
    public Integer getCurrentID(Player player) {
        return idMap.get(player);
    }
}
