/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItem;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ShortHandItemModNames;
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
import java.lang.reflect.Type;
import java.util.*;

/**
 * カスタムアイテムを読み書きするためのクラス
 */
public final class ItemIO implements InterfaceIO<Map<String, ITCItem>> {

    private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    private final Yaml yaml;

    public ItemIO() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        yaml = new Yaml(options);
    }

    /**
     * アイテムファイルをアイテムの実体に変換する
     */
    @Override
    public Map<String, ITCItem> deserialize(File file) {
        Map<String, ITCItem> deserializedItems = new HashMap<>();
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
                    if (valueNode2 instanceof MappingNode valueNode2Map) {
                        parseItemParams(tcItem, defaultMods, valueNode2Map);
                    }
                    tcItem.setDefaultMods(defaultMods);
                    deserializedItems.put(internalNameNode.getValue(), tcItem);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(ErrorMessages.FAILED_TO_PARSE_FILE.get() + file);
            e.printStackTrace();
        }

        return deserializedItems;
    }

    /**
     * マッピングノードからアイテムの全てのパラメータをパースする
     */
    private void parseItemParams(TCItem tcItem, List<IItemMod<?>> defaultMods, MappingNode valueNode2Map) {
        Iterator<NodeTuple> iterator3 = valueNode2Map.getValue().iterator();
        while (iterator3.hasNext()) {
            NodeTuple nodeTuple3 = iterator3.next();

            ScalarNode itemPropertiesNode = (ScalarNode) nodeTuple3.getKeyNode();//プロパティ達のノード

            if (itemPropertiesNode.getValue().equals(ItemIOTags.NAME.get())) {
                parseDisplayName(tcItem, nodeTuple3);
            } else if (itemPropertiesNode.getValue().equals(ItemIOTags.MATERIAL.get())) {
                parseMaterial(tcItem, nodeTuple3);
            } else if (itemPropertiesNode.getValue().equals(ItemIOTags.CUSTOMMODELDATA.get())) {
                parseCustomModelData(tcItem, nodeTuple3);
            } else if (itemPropertiesNode.getValue().equals(ItemIOTags.DEFAULTMODS.get())) {
                parseDefaultMods(tcItem, defaultMods, nodeTuple3);
            }
        }
    }

    /**
     * アイテムの表示名をノードからパースする
     */
    private void parseDisplayName(TCItem tcItem, NodeTuple nodeTuple3) {
        TextComponent name = Component.text(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        if (!name.content().isEmpty())
            tcItem.setName(name);
        else {
            throw new IllegalArgumentException(ErrorMessages.FAILED_TO_PARSE_ITEM_NAME.get() + tcItem.getInternalName());
        }
    }

    /**
     * アイテムのマテリアルをノードからパースする
     */
    private void parseMaterial(TCItem tcItem, NodeTuple nodeTuple3) {
        Material material = Material.getMaterial(((ScalarNode) nodeTuple3.getValueNode()).getValue());
        if (Objects.nonNull(material))
            tcItem.setMaterial(material);
        else {
            throw new IllegalArgumentException(ErrorMessages.FAILED_TO_PARSE_ITEM_MATERIAL.get() + tcItem.getInternalName() + "の" + ((ScalarNode) nodeTuple3.getValueNode()).getValue());
        }
    }

    /**
     * アイテムのカスタムモデルデータをノードからパースする
     */
    private void parseCustomModelData(TCItem tcItem, NodeTuple nodeTuple3) {
        tcItem.setCustomModelData(Integer.parseInt(((ScalarNode) nodeTuple3.getValueNode()).getValue()));
    }

    /**
     * アイテムのdefaultmodsをノードからパースする
     */
    private void parseDefaultMods(TCItem tcItem, List<IItemMod<?>> defaultMods, NodeTuple nodeTuple3) {
        Node valueNode3 = nodeTuple3.getValueNode();
        if (valueNode3 instanceof MappingNode valueNode3Map) {
            Iterator<NodeTuple> iterator4 = valueNode3Map.getValue().iterator();
            while (iterator4.hasNext()) {
                NodeTuple nodeTuple4 = iterator4.next();

                Node modsNameNode = nodeTuple4.getKeyNode(); //アイテムmodの名前ノード
                Node modsContentNode = nodeTuple4.getValueNode();//modの内容のノード
                Class<? extends IItemMod> modsClass = ShortHandItemModNames.getClassFromShortHandName(((ScalarNode) modsNameNode).getValue());
                if (Objects.isNull(modsClass)) {
                    throw new IllegalArgumentException(ErrorMessages.ILLEGAL_ITEM_MOD_NAME.get() + tcItem.getInternalName() + "の" + ((ScalarNode) modsNameNode).getValue());
                }

                //Jsonを元の型に還元する
                Constructor<?> constructor = modsClass.getConstructors()[0];
                Type[] parameterTypes = constructor.getGenericParameterTypes();
                String json = ((ScalarNode) modsContentNode).getValue();
                try {
                    Object arg = gson.fromJson(json, parameterTypes[0]);
                    IItemMod<?> mod = (IItemMod<?>) constructor.newInstance(arg);
                    defaultMods.add(mod);
                } catch (Exception e) {
                    Bukkit.getLogger().severe(ErrorMessages.FAILED_TO_PARSE_ITEM_MOD + tcItem.getInternalName() + "の" + ((ScalarNode) modsNameNode).getValue());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * アイテム達のデータを指定されたyamlファイルに書きこむ
     */
    @Override
    public void serialize(Map<String, ITCItem> items, File file) {
        Map<String, Object> itemRoot = new HashMap<>();

        for (ITCItem item : items.values()) {
            Map<String, Object> defaultModsMap = new HashMap<>();
            saveItemMods(item, defaultModsMap);

            Map<String, Object> itemInfo = new HashMap<>();
            saveItemBasicParams(item, defaultModsMap, itemInfo);

            //アイテムのinternalnameをキーとしてセーブ
            itemRoot.put(item.getInternalName(), itemInfo);
        }

        serializeToYaml(file, itemRoot);
    }

    /**
     * アイテムの基本的な情報をセーブ
     */
    private void saveItemBasicParams(ITCItem item, Map<String, Object> defaultModsMap, Map<String, Object> itemInfo) {
        itemInfo.put(ItemIOTags.NAME.get(), item.getName().content());
        itemInfo.put(ItemIOTags.MATERIAL.get(), item.getMaterial().toString());
        itemInfo.put(ItemIOTags.CUSTOMMODELDATA.get(), item.getCustomModelData());
        itemInfo.put(ItemIOTags.DEFAULTMODS.get(), defaultModsMap);
    }

    /**
     * アイテムのmodをシリアライズしてマップに書きこむ
     */
    private void saveItemMods(ITCItem item, Map<String, Object> defaultModsMap) {
        for (IItemMod<?> defaultMod : item.getDefaultMods()) {
            String key = ShortHandItemModNames.getShortHandNameFromClass((Class<? extends IItemMod<?>>) defaultMod.getClass());
            String value = gson.toJson(defaultMod.getParam());
            defaultModsMap.put(key, value);
        }
    }

    /**
     * Yamlとしてファイルにデータを書きこむ
     */
    private void serializeToYaml(File file, Map<String, Object> itemRoot) {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        FileUtils.getFile(file.toString());
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(itemRoot, writer); // マップのデータをYAMLファイルに書き込む
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
