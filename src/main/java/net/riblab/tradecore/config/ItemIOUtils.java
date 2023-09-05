package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

            if (rootNode instanceof MappingNode mappingNode) {
                // マップ内のアイテムを1個ずつ取得
                Iterator<NodeTuple> iterator2 = mappingNode.getValue().iterator();
                while (iterator2.hasNext()) {
                    NodeTuple nodeTuple2 = iterator2.next();
                    ScalarNode internalNameNode = (ScalarNode) nodeTuple2.getKeyNode();

                    TCItem tcItem = new TCItem();
                    tcItem.setInternalName(internalNameNode.getValue());
                    List<IItemMod<?>> defaultMods = new ArrayList<>();

                    Node valueNode2 = nodeTuple2.getValueNode();
                    if(valueNode2 instanceof MappingNode valueNode2Map){
                        Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
                        while (iterator3.hasNext()){
                            NodeTuple nodeTuple3 = iterator3.next();

                            ScalarNode itemPropertiesNode = (ScalarNode) nodeTuple3.getKeyNode();//プロパティ達のノード

                            if(itemPropertiesNode.getValue().equals(ItemIOTags.NAME.get())){
                                TextComponent name = Component.text(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                if(!name.content().isEmpty())
                                    tcItem.setName(name);
                                else{
                                    throw new IllegalArgumentException("名前の解析に失敗しました：" + tcItem.getInternalName());
                                }
                            }
                            else if(itemPropertiesNode.getValue().equals(ItemIOTags.MATERIAL.get())){
                                Material material = Material.getMaterial(((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                if(Objects.nonNull(material))
                                    tcItem.setMaterial(material);
                                else{
                                    throw new IllegalArgumentException("マテリアルの解析に失敗しました：" + tcItem.getInternalName() + "の" + ((ScalarNode) nodeTuple3.getValueNode()).getValue());
                                }
                            }
                            else if(itemPropertiesNode.getValue().equals(ItemIOTags.CUSTOMMODELDATA.get())){
                                tcItem.setCustomModelData(Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue()));
                            }
                            else if(itemPropertiesNode.getValue().equals(ItemIOTags.DEFAULTMODS.get())){
                                Node valueNode3 = nodeTuple3.getValueNode();
                                if(valueNode3 instanceof MappingNode valueNode3Map){
                                    Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
                                    while (iterator4.hasNext()){
                                        NodeTuple nodeTuple4 = iterator4.next();

                                        Node modsNameNode = nodeTuple4.getKeyNode(); //アイテムmodの名前ノード
                                        Node modsContentNode = nodeTuple4.getValueNode();//modの内容のノード
                                        Class<? extends IItemMod> modsClass =  ShortHandModNames.getClassFromShortHandName(((ScalarNode)modsNameNode).getValue());
                                        if(Objects.isNull(modsClass)){
                                            throw  new IllegalArgumentException("不正なmod名が検出されました" + tcItem.getInternalName() + "の" + ((ScalarNode)modsNameNode).getValue());
                                        }

                                        //Jsonを元の型に還元する
                                        Constructor<?> constructor = modsClass.getConstructors()[0];
                                        Type[] parameterTypes = constructor.getGenericParameterTypes();
                                        String json = ((ScalarNode) modsContentNode).getValue();
                                        try{
                                            Object arg =  gson.fromJson(json, parameterTypes[0]);
                                            IItemMod<?> mod = (IItemMod<?>) constructor.newInstance(arg);
                                            defaultMods.add(mod);
                                        }
                                        catch (Exception e){
                                            Bukkit.getLogger().severe("アイテムのmodの内容の解析に失敗しました:" + tcItem.getInternalName() + "の" + ((ScalarNode)modsNameNode).getValue());
                                            e.printStackTrace();
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
        } catch (IOException e) {
            Bukkit.getLogger().severe("ファイルの解析に失敗しました: " + file);
            e.printStackTrace();
        }
        
        return deserializedItems;
    }

    public static void saveItem(List<ITCItem> items, File file){
        Map<String, Object> itemRoot = new HashMap<>();
        
        for (ITCItem item : items) {
            //modをセーブ
            Map<String, Object> defaultModsMap = new HashMap<>();
            for (IItemMod<?> defaultMod : item.getDefaultMods()) {
                String key = ShortHandModNames.getShortHandNameFromClass((Class<? extends IItemMod<?>>) defaultMod.getClass());
                String value = gson.toJson(defaultMod.getParam());
                defaultModsMap.put(key, value);
            }

            //アイテムの情報をセーブ
            Map<String, Object> itemInfo = new HashMap<>();
            itemInfo.put(ItemIOTags.NAME.get(), item.getName().content());
            itemInfo.put(ItemIOTags.MATERIAL.get(), item.getMaterial().toString());
            itemInfo.put(ItemIOTags.CUSTOMMODELDATA.get(), item.getCustomModelData());
            itemInfo.put(ItemIOTags.DEFAULTMODS.get(), defaultModsMap);

            //アイテムのinternalnameをキーとしてセーブ
            itemRoot.put(item.getInternalName(), itemInfo);
        }

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
}
