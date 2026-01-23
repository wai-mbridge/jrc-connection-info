package utils;

public class ConstantUtil {
    public static int getTrainType(String label) {
        return switch (label) {
        case "各駅停車" -> 1;
        case "快速" -> 2;
        case "急行" -> 3;
        case "特別急行" -> 4;
        default -> 1;
        };
    }
}
