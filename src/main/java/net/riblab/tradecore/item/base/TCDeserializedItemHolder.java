package net.riblab.tradecore.item.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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

/**
 * yamlで書かれたアイテムをデシリアライズしたものを保持するクラス
 */
public enum TCDeserializedItemHolder {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private final List<ITCItem> deserializedItems = new ArrayList<>();//TODO:ゲッターを削除して読み書きをメソッド経由で行うように
}
