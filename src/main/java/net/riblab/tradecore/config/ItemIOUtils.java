package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItem;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ShortHandModNames;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

public final class ItemIOUtils {
    private ItemIOUtils(){
        
    }
    
    private static final String nameTag = "name";
    private static final String materialTag = "material";
    private static final String customModelDataTag = "model";

    private static final String defaultModsTag = "mods";

    private static final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    
    /**
     * アイテムファイルをアイテムの実体に変換する
     */
    public static List<ITCItem> deserialize(File file){
        List<ITCItem> deserializedItems = new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            Yaml yaml = new Yaml();

            // YAMLデータを読み込み、ルートノードを取得
            Node rootNode = yaml.compose(reader);

            if (rootNode instanceof MappingNode) {
                MappingNode mappingNode = (MappingNode) rootNode;

                // マップ内のアイテムを1個ずつ取得
                Iterator<NodeTuple> iterator = mappingNode.getValue().iterator();
                while (iterator.hasNext()) {
                    NodeTuple nodeTuple = iterator.next();

                    Node valueNode = nodeTuple.getValueNode();//一番上の中身
                    if (valueNode instanceof MappingNode) {
                        MappingNode valueNodeMap = (MappingNode) valueNode;
                        Iterator<NodeTuple> iterator2 = valueNodeMap.getValue().iterator();
                        while (iterator2.hasNext()) {
                            NodeTuple nodeTuple2 = iterator2.next();
                            ScalarNode internalNameNode = (ScalarNode) nodeTuple2.getKeyNode();

                            TCItem tcItem = new TCItem();
                            tcItem.setInternalName(internalNameNode.getValue());
                            List<IItemMod<?>> defaultMods = new ArrayList<>();

                            Node valueNode2 = nodeTuple2.getValueNode();
                            if(valueNode2 instanceof MappingNode){
                                MappingNode valueNode2Map = ((MappingNode) valueNode2);
                                Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
                                while (iterator3.hasNext()){
                                    NodeTuple nodeTuple3 = iterator3.next();

                                    ScalarNode itemPropertiesNode = (ScalarNode) nodeTuple3.getKeyNode();//プロパティ達のノード

                                    if(itemPropertiesNode.getValue().equals(nameTag)){
                                        tcItem.setName(Component.text(((ScalarNode) nodeTuple3.getValueNode()).getValue()));
                                    }
                                    else if(itemPropertiesNode.getValue().equals(materialTag)){
                                        tcItem.setMaterial(Material.getMaterial(((ScalarNode) nodeTuple3.getValueNode()).getValue()));
                                    }
                                    else if(itemPropertiesNode.getValue().equals(customModelDataTag)){
                                        tcItem.setCustomModelData(Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue()));
                                    }
                                    else if(itemPropertiesNode.getValue().equals(defaultModsTag)){
                                        Node valueNode3 = nodeTuple3.getValueNode();
                                        if(valueNode3 instanceof MappingNode){
                                            MappingNode valueNode3Map = ((MappingNode) valueNode3);
                                            Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
                                            while (iterator4.hasNext()){
                                                NodeTuple nodeTuple4 = iterator4.next();

                                                Node modsNameNode = nodeTuple4.getKeyNode(); //アイテムmodの名前ノード
                                                Node modsContentNode = nodeTuple4.getValueNode();//modの内容のノード
                                                Class<? extends IItemMod> modsClass =  ShortHandModNames.getClassFromShortHandName(((ScalarNode)modsNameNode).getValue());
                                                if(modsClass != null){
                                                    //Jsonを元の型に還元する
                                                    Constructor<?> constructor = modsClass.getConstructors()[0];
                                                    Type[] parameterTypes = constructor.getGenericParameterTypes();
                                                    String json = ((ScalarNode) modsContentNode).getValue();
                                                    Object arg =  gson.fromJson(json, parameterTypes[0]);
                                                    IItemMod<?> mod = (IItemMod<?>) constructor.newInstance(arg);
                                                    defaultMods.add(mod);
                                                }
                                                else{
                                                    Bukkit.getLogger().severe(((ScalarNode)modsNameNode).getValue() + "に対応するmodが見つかりません！");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            tcItem.setDefaultMods(defaultMods);
                            deserializedItems.add(tcItem);
                        }
                    }
                }
            }
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Bukkit.getLogger().severe("ファイルの解析に失敗しました: " + file);
            e.printStackTrace();
        }
        
        return deserializedItems;
    }

    public static void saveItem(ITCItem item, File file){
        //modをセーブ
        Map<String, Object> defaultModsMap = new HashMap<>();
        for (IItemMod<?> defaultMod : item.getDefaultMods()) {
            String key = ShortHandModNames.getShortHandNameFromClass((Class<? extends IItemMod<?>>) defaultMod.getClass());
            String value = gson.toJson(defaultMod.getParam());
            defaultModsMap.put(key, value);
        }

        //アイテムの情報をセーブ
        Map<String, Object> itemInfo = new HashMap<>();
        itemInfo.put(nameTag, item.getName().content());
        itemInfo.put(materialTag, item.getMaterial().toString());
        itemInfo.put(customModelDataTag, item.getCustomModelData());
        itemInfo.put(defaultModsTag, defaultModsMap);

        //アイテムのinternalnameをキーとしてセーブ
        Map<String, Object> itemRoot = new HashMap<>();
        itemRoot.put(item.getInternalName(), itemInfo);

        // SnakeYAMLの設定をカスタマイズ（オプション）
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        Yaml yaml = new Yaml(options);

        // YAMLファイルにデータを書き込む
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        FileUtils.getFile(file.toString());
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(itemRoot, writer); // マップのデータをYAMLファイルに書き込む
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 複数アイテムをまとめてシリアライズするためのクラス
     */
    public static class SerializedTCItems {
        @Getter
        public Map<String, TCItem> map = new HashMap<>();
    }
}
