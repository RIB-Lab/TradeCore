package net.riblab.tradecore;

import net.kyori.adventure.key.Key;

public class TCResourcePackData {
    public TCResourcePackData() {

    }

    /**
     * バニラフォントの名前
     */
    public static final Key defaultFontName = Key.key("default");

    /**
     * iconFontのリソースパック上での名前
     */
    public static final Key iconsFontName = Key.key("icons");

    /**
     * uiFontのリソースパック上での名前
     */
    public static final Key uiFontName = Key.key("ui");

    /**
     * アクションバーのy+12の位置に表示される数字のフォントの名前
     */
    public static final Key yPlus12FontName = Key.key("asciiyplus12");

    /**
     * リソースパックのmenuFontのカスタムアイコン名とunicodeの対応表
     */
    public enum IconsFont {
        NEGATIVE_SPACE("«"),
        SPACE(" "),
        COIN("\uE000"),
        VOTE_TICKET("\uE001");

        private final String _char;

        IconsFont(String _char) {
            this._char = _char;
        }

        public String get_char() {
            return _char;
        }
    }

    public enum UIFont {
        NEGATIVE_SPACE("«"),
        SUPER_NEGATIVE_SPACE("<"),
        SPACE(" "),
        CRAFTING_TABLE_CATEGORY("\uE000"),
        CRAFTING_TABLE_ARMOR("\uE001"),
        CRAFTING_TABLE_CRAFTING("\uE002"),
        CRAFTING_TABLE_MISC("\uE003"),
        CRAFTING_TABLE_TOOL("\uE004"),
        CRAFTING_TABLE_WEAPON("\uE005"),
        FURNACE("\uE006");


        private final String _char;

        UIFont(String _char) {
            this._char = _char;
        }

        public String get_char() {
            return _char;
        }
    }
}
