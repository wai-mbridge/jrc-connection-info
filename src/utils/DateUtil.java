package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String changeFormat(String input) {
        if (input == null || input.isEmpty())
            return input;

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
}
