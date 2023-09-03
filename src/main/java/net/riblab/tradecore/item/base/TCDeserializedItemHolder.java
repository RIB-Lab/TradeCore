package net.riblab.tradecore.item.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ShortHandModNames;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * yamlで書かれたアイテムをデシリアライズして保持するクラス
 */
public enum TCDeserializedItemHolder {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private final List<ITCItem> deserializedItems = new ArrayList<>();
    
    private static final String nameTag = "name";
    private static final String materialTag = "material";
    private static final String customModelDataTag = "customModelData";

    private static final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    public void clear(){
        deserializedItems.clear();
    }
    
    /**
     * アイテムファイルをアイテムの実体に変換する
     */
    public void deserialize(File file){
        
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
                            tcItem.setInternalName(((ScalarNode)nodeTuple2.getKeyNode()).getValue());
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
                                    
                                    Node valueNode3 = nodeTuple3.getValueNode();
                                    if(valueNode3 instanceof MappingNode){
                                        MappingNode valueNode3Map = ((MappingNode) valueNode3);
                                        Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
                                        while (iterator4.hasNext()){
                                            NodeTuple nodeTuple4 = iterator4.next();

                                            Node modsNameNode = nodeTuple4.getKeyNode(); //アイテムmodの名前ノード
                                            Node modsContentNode = nodeTuple4.getValueNode();//modの内容のノード
                                            Class<? extends IItemMod> modsClass =  ShortHandModNames.getClassFromShortHandName(((ScalarNode)modsNameNode).getValue());
                                            
                                            //Jsonを元の型に還元する
                                            Constructor<?> constructor = modsClass.getConstructors()[0];
                                            Type[] parameterTypes = constructor.getGenericParameterTypes();
                                            String json = ((ScalarNode) modsContentNode).getValue();
                                            Object arg =  gson.fromJson(json, parameterTypes[0]);
                                            IItemMod<?> mod = (IItemMod<?>) constructor.newInstance(arg);
                                            defaultMods.add(mod);
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
            e.printStackTrace();
        }
    }
    
    public void saveItem(ITCItem item, File file){
        Map<String, Object> defaultModsMap = new HashMap<>();
        for (IItemMod<?> defaultMod : item.getDefaultMods()) {
            String key = ShortHandModNames.getShortHandNameFromClass((Class<? extends IItemMod<?>>) defaultMod.getClass());
            String value = gson.toJson(defaultMod.getParam());
            defaultModsMap.put(key, value);
        }
        
        Map<String, Object> itemInfo = new HashMap<>();
        itemInfo.put("name", item.getName().content());
        itemInfo.put("material", item.getMaterial().toString());
        itemInfo.put("customModelData", item.getCustomModelData());
        itemInfo.put("defaultMods", defaultModsMap);

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
    @Configuration
    public static class SerializedTCItems {
        @Comment("シリアライズしたいアイテムの定義たち")
        @Getter
        public Map<String, TCItem> map = new HashMap<>();
    }
}
