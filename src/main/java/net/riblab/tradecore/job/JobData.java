package net.riblab.tradecore.job;

import de.exlll.configlib.Configuration;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * プレイヤーが配列で持つジョブ一つ分のデータ
 */
@Data
@Configuration
public class JobData {

    /**
     * このデータが保存する職業の種類
     */
    JobType jobType;

    /**
     * 現在のレベル
     */
    int level;

    /**
     * 現在の経験値
     */
    int exp;
    
    public enum JobType{
        Miner("鉱夫"),
        Digger("整地師"),
        Woodcutter("木こり"),
        Mower("草刈り機"),
        Crafter("クラフター");

        private final String name;

        JobType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        // Function that returns our custom argument
        public static Argument<JobType> customJobTypeArgument(String nodeName) {
            
            return new CustomArgument<JobType, String>(new StringArgument(nodeName), info -> {
                JobType type = commandToJOBType(info.input());

                if (type == null) {
                    throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown job: ").appendArgInput());
                } else {
                    return type;
                }
            }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                    Arrays.stream(values()).map(Enum::toString).toArray(String[]::new))
            );
        }

        /**
         * コマンド文字列をジョブに変換する
         */
        @Nullable
        public static JobType commandToJOBType(String command) {
            JobType type = Arrays.stream(JobType.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
            return type == null ? null : type;
        }
    }
    
    /**
     * 次レベルまでに必要な経験値（300レベル分） (excelで (LEVEL / 5)^2を実行しただけ)
     */
    public static List<Double> requiredExp = List.of(1.0 ,1.2 ,1.4 ,1.6 ,2.0 ,2.4 ,3.0 ,3.6 ,4.2 ,5.0 ,5.8 ,6.8 ,7.8 ,8.8 ,10.0 ,11.2 ,12.6 ,14.0 ,15.4 ,17.0 ,18.6 ,20.4 ,22.2 ,24.0 ,26.0 ,28.0 ,30.2 ,32.4 ,34.6 ,37.0 ,39.4 ,42.0 ,44.6 ,47.2 ,50.0 ,52.8 ,55.8 ,58.8 ,61.8 ,65.0 ,68.2 ,71.6 ,75.0 ,78.4 ,82.0 ,85.6 ,89.4 ,93.2 ,97.0 ,101.0 ,105.0 ,109.2 ,113.4 ,117.6 ,122.0 ,126.4 ,131.0 ,135.6 ,140.2 ,145.0 ,149.8 ,154.8 ,159.8 ,164.8 ,170.0 ,175.2 ,180.6 ,186.0 ,191.4 ,197.0 ,202.6 ,208.4 ,214.2 ,220.0 ,226.0 ,232.0 ,238.2 ,244.4 ,250.6 ,257.0 ,263.4 ,270.0 ,276.6 ,283.2 ,290.0 ,296.8 ,303.8 ,310.8 ,317.8 ,325.0 ,332.2 ,339.6 ,347.0 ,354.4 ,362.0 ,369.6 ,377.4 ,385.2 ,393.0 ,401.0 ,409.0 ,417.2 ,425.4 ,433.6 ,442.0 ,450.4 ,459.0 ,467.6 ,476.2 ,485.0 ,493.8 ,502.8 ,511.8 ,520.8 ,530.0 ,539.2 ,548.6 ,558.0 ,567.4 ,577.0 ,586.6 ,596.4 ,606.2 ,616.0 ,626.0 ,636.0 ,646.2 ,656.4 ,666.6 ,677.0 ,687.4 ,698.0 ,708.6 ,719.2 ,730.0 ,740.8 ,751.8 ,762.8 ,773.8 ,785.0 ,796.2 ,807.6 ,819.0 ,830.4 ,842.0 ,853.6 ,865.4 ,877.2 ,889.0 ,901.0 ,913.0 ,925.2 ,937.4 ,949.6 ,962.0 ,974.4 ,987.0 ,999.6 ,1012.2 ,1025.0 ,1037.8 ,1050.8 ,1063.8 ,1076.8 ,1090.0 ,1103.2 ,1116.6 ,1130.0 ,1143.4 ,1157.0 ,1170.6 ,1184.4 ,1198.2 ,1212.0 ,1226.0 ,1240.0 ,1254.2 ,1268.4 ,1282.6 ,1297.0 ,1311.4 ,1326.0 ,1340.6 ,1355.2 ,1370.0 ,1384.8 ,1399.8 ,1414.8 ,1429.8 ,1445.0 ,1460.2 ,1475.6 ,1491.0 ,1506.4 ,1522.0 ,1537.6 ,1553.4 ,1569.2 ,1585.0 ,1601.0 ,1617.0 ,1633.2 ,1649.4 ,1665.6 ,1682.0 ,1698.4 ,1715.0 ,1731.6 ,1748.2 ,1765.0 ,1781.8 ,1798.8 ,1815.8 ,1832.8 ,1850.0 ,1867.2 ,1884.6 ,1902.0 ,1919.4 ,1937.0 ,1954.6 ,1972.4 ,1990.2 ,2008.0 ,2026.0 ,2044.0 ,2062.2 ,2080.4 ,2098.6 ,2117.0 ,2135.4 ,2154.0 ,2172.6 ,2191.2 ,2210.0 ,2228.8 ,2247.8 ,2266.8 ,2285.8 ,2305.0 ,2324.2 ,2343.6 ,2363.0 ,2382.4 ,2402.0 ,2421.6 ,2441.4 ,2461.2 ,2481.0 ,2501.0 ,2521.0 ,2541.2 ,2561.4 ,2581.6 ,2602.0 ,2622.4 ,2643.0 ,2663.6 ,2684.2 ,2705.0 ,2725.8 ,2746.8 ,2767.8 ,2788.8 ,2810.0 ,2831.2 ,2852.6 ,2874.0 ,2895.4 ,2917.0 ,2938.6 ,2960.4 ,2982.2 ,3004.0 ,3026.0 ,3048.0 ,3070.2 ,3092.4 ,3114.6 ,3137.0 ,3159.4 ,3182.0 ,3204.6 ,3227.2 ,3250.0 ,3272.8 ,3295.8 ,3318.8 ,3341.8 ,3365.0 ,3388.2 ,3411.6 ,3435.0 ,3458.4 ,3482.0 ,3505.6 ,3529.4 ,3553.2 ,3577.0 ,3601.0);
}