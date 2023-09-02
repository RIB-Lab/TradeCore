package net.riblab.tradecore.item.base;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.config.TextComponentSerializer;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum TCDeserializedItemHolder {
    INSTANCE;

    /**
     * デシリアライズしたアイテム
     */
    @Getter
    private final List<ITCItem> deserializedItems = new ArrayList<>();

    /**
     * アイテムをロードするためにymlに渡すプロパティ
     */
    public static final YamlConfigurationProperties customProperties = YamlConfigurationProperties.newBuilder()
            .addSerializer(TextComponent.class, new TextComponentSerializer())
            .build();

    /**
     * アイテムファイルをアイテムの実体に変換する
     */
    public void deserialize(File file){
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String type = yaml.getString("type");
        if(type == null){
            Bukkit.getLogger().severe(file + "のアイテムのタイプが設定されていません。アイテムは無視されます...");
            return;
        }
        
        //ymlconfigがワイルドカードに対応していないのでちまちまクラスを書くしかない
        if(type.equals(TCItem.class.getSimpleName())){
            deserializedItems.add(YamlConfigurations.load(file.toPath(), TCItem.class, customProperties));
            return;
        }

        Bukkit.getLogger().severe(file + "のアイテムのタイプ:" + type + "が不正です。アイテムは無視されます...");
    }
}
