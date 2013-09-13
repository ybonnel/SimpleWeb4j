package fr.ybonnel.simpleweb4j.util;

/**
 */
public class StringUtils {

    public static int getIndexOfBlank(String value) {
        if (value == null) {
            return -1;
        }
        int firstSpaceIndex = value.indexOf(' ');
        if (firstSpaceIndex < 0) {
            firstSpaceIndex = Integer.MAX_VALUE;
        }
        int firstTabIndex = value.indexOf('\t');
        if (firstTabIndex < 0) {
            firstTabIndex = Integer.MAX_VALUE;
        }
        int firstBlankCharacterIndex = Math.min(firstSpaceIndex, firstTabIndex);
        if (firstBlankCharacterIndex == Integer.MAX_VALUE) {
            firstBlankCharacterIndex = -1;
        }
        return firstBlankCharacterIndex;
    }
}
