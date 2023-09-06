package net.riblab.tradecore.config.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class JsonIO {
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * あるデータファイル(.json)からあるデータタイプを読み込んで返す
     */
    public static  <T> T loadAsJson(File dataFile, Class<T> dataType){
        String str = null;
        try {
            str =  FileUtils.fileRead(dataFile);
        } catch (IOException ignored) {
        }

        if(!StringUtils.isEmpty(str))
            return gson.fromJson(str, dataType);
        else
            return null;
    }

    /**
     * あるクラスのインスタンスをあるファイルにjsonとして保存する<br>
     * 人間が編集することを期待する場合jsonではなくyamlシリアライザを定義すること
     */
    public static void saveWithJson(Object dataInstance, File file){
        String str = gson.toJson(dataInstance);
        try {
            FileUtils.forceMkdir(new File(file.getParent()));
            FileUtils.fileWrite(file, str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
