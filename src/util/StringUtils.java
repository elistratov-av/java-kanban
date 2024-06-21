package util;

public final class StringUtils {
    public static String emptyIfNull(Object o) {
        return o != null ? String.valueOf(o) : "";
    }

    public static boolean equals(String s1, String s2, boolean nullAsEmpty) {
        if (nullAsEmpty) {
            if (s1 == null) s1 = "";
            if (s2 == null) s2 = "";
            return s1 == s2 || s1.equals(s2);
        }
        return s1 == s2 || (s1 != null && s1.equals(s2));
    }

    public static boolean nullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
