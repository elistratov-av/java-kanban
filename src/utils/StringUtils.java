package utils;

public final class StringUtils {
    public static String trim(String s) {
        return s != null ? s.trim() : null;
    }

    public static String emptyIfNull(Object o) {
        return o != null ? String.valueOf(o) : "";
    }

    public static int compare(String s1, String s2, boolean nullAsEmpty) {
        if (nullAsEmpty) {
            if (s1 == null) s1 = "";
            if (s2 == null) s2 = "";
            return s1 == s2 ? 0 : s1.compareTo(s2);
        }
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }
}
