package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FakeVillagerService {
    
    private static Map<Player, Integer> idMap = new HashMap<>();

    /**
     * プレイヤーの視線の先のブロックの上に村人を召喚する
     * @param player
     */
    public static void spawnFakeVillager(Player player){
        Set<Material> transparentBlocks = new HashSet<>();
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.LAVA);
        transparentBlocks.add(Material.AIR);
        Location spawnLocation = player.getTargetBlock(transparentBlocks, 5).getRelative(0,1,0).getLocation().add(new Vector(0.5d, 0d, 0.5d));

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

        try {
            TradeCore.getInstance().getProtocolManager().sendServerPacket(player, spawnPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 村人を削除する。呼ばないと村人がクライアントに永遠に残り続ける
     * @param player
     */
    public static void tryDeSpawnFakeVillager(Player player){
        Integer entityID = idMap.remove(player);
        if(entityID == null)
            return;

        List<Integer> entityIDList = new ArrayList<>();
        entityIDList.add(entityID);

        PacketContainer deSpawnPacket = TradeCore.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        deSpawnPacket.getIntLists().write(0, entityIDList);

        try {
            TradeCore.getInstance().getProtocolManager().sendServerPacket(player, deSpawnPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
