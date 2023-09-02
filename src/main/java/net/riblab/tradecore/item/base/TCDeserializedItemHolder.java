package net.riblab.tradecore.item.base;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.config.TextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String type = YamlConfiguration.loadConfiguration(file).getString("type");
        if(type == null){
            Bukkit.getLogger().severe(file + "のアイテムのタイプが設定されていません。ファイルは無視されます...");
            return;
        }
        
        //ymlconfigがワイルドカードに対応していないのでちまちまクラスを書くしかない
        if(type.equals(TCItem.class.getSimpleName())){
            SerializedTCItems items = YamlConfigurations.load(file.toPath(), SerializedTCItems.class, customProperties);
            for (Map.Entry<String, TCItem> stringTCItemEntry : items.getMap().entrySet()) {

                stringTCItemEntry.getValue().setInternalName(stringTCItemEntry.getKey());//yml上でのアイテム名をinternalnameとする
                deserializedItems.add(stringTCItemEntry.getValue());
            }
            return;
        }
        
        Bukkit.getLogger().severe(file + "のタイプ:" + type + "が不正です。ファイルは無視されます...");
    }

    /**
     * 複数アイテムをまとめてシリアライズするためのクラス
     */
    @Configuration
    public static class SerializedTCItems {
        @Comment("シリアライズしたいアイテムが属する種類")
        String type;
        @Comment("シリアライズしたいアイテムの定義たち")
        @Getter
        private Map<String, TCItem> map = new HashMap<>();
    }
}
