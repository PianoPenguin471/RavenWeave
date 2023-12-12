package ravenweave.client.module.modules.aycy.optimalaim.f;

public class StringUtils {
    public StringUtils() {
    }

    public static String unshuffleString(String a) {
        StringBuilder b = new StringBuilder();

        for(int c = 0; c < a.length(); ++c) {
            b.append((char)(a.charAt(c) - c - 1));
        }

        return b.toString();
    }
}
