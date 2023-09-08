/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config.io;

import net.riblab.tradecore.general.ErrorMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * マテリアルセットを読み書きするためのクラス
 */
public class MaterialSetIO {

    private static final Yaml yaml;

    static {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        yaml = new Yaml(options);
    }
    
    public Map<String, Set<Material>> deserialize(File materialSetFile) {

        Map<String, Set<Material>> deserializedMaterials = new HashMap<>();
        try (FileReader reader = new FileReader(materialSetFile)) {
            // YAMLデータを読み込み、ルートノードを取得
            Node rootNode = yaml.compose(reader);

            if (rootNode instanceof MappingNode mappingNode) {
                // マップ内のアイテムを1個ずつ取得
                Iterator<NodeTuple> iterator2 = mappingNode.getValue().iterator();
                while (iterator2.hasNext()) {
                    NodeTuple nodeTuple2 = iterator2.next();
                    ScalarNode materialSetNameNode = (ScalarNode) nodeTuple2.getKeyNode();

                    Set<Material> deserializedSet = new HashSet<>();

                    Node valueNode2 = nodeTuple2.getValueNode();
                    if (valueNode2 instanceof SequenceNode valueNode2Seq) {
                        Iterator<Node> iterator3 = valueNode2Seq.getValue().iterator();
                        while (iterator3.hasNext()) {
                            Node nodeTuple3 = iterator3.next();

                            ScalarNode materialNode = ((ScalarNode) nodeTuple3);//STONE, GRASS, ...
                            Optional.ofNullable(Material.getMaterial(materialNode.getValue()))
                                    .ifPresentOrElse(deserializedSet::add, 
                                    () -> {throw new IllegalArgumentException(ErrorMessages.FAILED_TO_PARSE_ITEM_MATERIAL.get() + materialSetNameNode.getValue());});
                        }
                    }
                    deserializedMaterials.put(materialSetNameNode.getValue(), deserializedSet);
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(ErrorMessages.FAILED_TO_PARSE_FILE.get() + materialSetFile);
            e.printStackTrace();
        }

        return deserializedMaterials;
    }
    
    public void serialize(Map<String, Set<Material>> materialSetToSave, File fileToSave){
        if (!fileToSave.getParentFile().exists())
            fileToSave.getParentFile().mkdirs();
        FileUtils.getFile(fileToSave.toString());
        try (FileWriter writer = new FileWriter(fileToSave)) {
            yaml.dump(materialSetToSave, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
