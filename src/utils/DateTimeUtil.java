package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String changeFormat(String input) {
        if (input == null || input.isEmpty())
            return input;

        input = input.replace("改正", "");
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '０' && c <= '９') {
                sb.append((char) (c - '０' + '0'));
            } else {
                sb.append(c);
            }
        }
        input = sb.toString();

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy年 M月d日");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy/M/d");

        return LocalDate.parse(input.trim(), inFmt).format(outFmt);
    }

    public static String changeTimeFormat(String input) {
        // parse input (hours:minutes.seconds)
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm.ss");
        LocalTime time = LocalTime.parse(input, inputFormatter);

        // format to HH:mm:ss (with leading zero)
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formatted = time.format(outputFormatter);

        return formatted;
    }
}
