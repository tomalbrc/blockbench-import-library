package de.tomalbrc.bil.util;

public class VersionCheck {

    public static boolean isAtLeastVersion(String currentVersion, String minimumVersion) {
        return compareVersions(currentVersion, minimumVersion) >= 0;
    }

    public static int compareVersions(String v1, String v2) {
        String[] v1Main = v1.split("-", 2);
        String[] v2Main = v2.split("-", 2);

        return compareMain(v1Main[0], v2Main[0]);
    }

    private static int compareMain(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) return Integer.compare(num1, num2);
        }
        return 0;
    }
}