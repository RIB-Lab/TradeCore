package net.riblab.tradecore;

import net.kyori.adventure.key.Key;

public class TCResourcePackData {
    public TCResourcePackData(){
        
    }

    /**
     * バニラフォントの名前
     */
    public static final Key defaultFontName = Key.key("default");

    /**
     * iconFontのリソースパック上での名前
     */
    public static final Key iconsFontName = Key.key("icons");
    
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
        
        public String get_char(){
            return _char;
        }
    }
}
