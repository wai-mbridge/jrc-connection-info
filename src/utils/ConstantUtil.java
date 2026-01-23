package utils;

public class ConstantUtil {
    public static int getTrainType(String label) {
        return switch (label) {
        case "各駅停車", "電" -> 1;
        case "快速" -> 2;
        case "急行" -> 3;
        case "特別急行", "特急電", "臨特急電" -> 4;
        case "通勤快速" -> 5;
        case "回送" -> 6;
        default -> 1;
        };
    }

    public static int getBoundType(String label) {
        return switch (label) {
        case "上り" -> 1;
        case "下り" -> 2;
        default -> throw new IllegalArgumentException("Invalid label: " + label);
        };
    }

    public static String getDayCodeName(String day_code) {
        return switch (day_code) {
        case "01" -> "平";
        case "11" -> "平平";
        case "04" -> "休";
        case "44" -> "休休";
        default -> throw new IllegalArgumentException("Invalid label: " + day_code);
        };
    }
}
