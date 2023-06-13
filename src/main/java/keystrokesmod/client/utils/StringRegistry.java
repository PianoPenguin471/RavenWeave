package keystrokesmod.client.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringRegistry
{
    private static List<String> strings = new ArrayList<String>();
    private static List<String> memelist = new ArrayList<String>();

    public static String register(final String string) {
        if (!StringRegistry.strings.contains(string)) {
            StringRegistry.strings.add(string);
        }
        return string;
    }

    public static void registerObject(final Object object) {
        if (object instanceof String) {
            register((String)object);
            return;
        }
        for (final Field f : object.getClass().getDeclaredFields()) {
            try {
                if (f.getType().equals(String.class)) {
                    register(getField(object, f));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> T getField(final Object object, final Field f) {
        try {
            final boolean modified = !f.isAccessible();
            if (modified) {
                f.setAccessible(true);
            }
            final T o = (T)f.get(object);
            if (modified) {
                f.setAccessible(false);
            }
            return o;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void cleanup() {
        try {
            final Field field = String.class.getDeclaredField(new String(new char[] { 'v', 'a', 'l', 'u', 'e' }));
            field.setAccessible(true);
            for (String s : StringRegistry.strings) {
                if (s != null) {
                    final char[] c = (char[])field.get(s);
                    for (int i = 0; i < s.length(); ++i) {
                        c[i] = '\0';
                    }
                }
                s = null;
            }
            for (String meme : StringRegistry.memelist) {
                if (meme != null) {
                    final char[] xd = (char[])field.get(meme);
                    for (int men = 0; men < meme.length(); ++men) {
                        xd[men] = '\0';
                    }
                }
                meme = null;
            }
            StringRegistry.strings.clear();
            StringRegistry.memelist.clear();
        }
        catch (Exception ex) {}
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();
    }

    static {
        StringRegistry.strings = new ArrayList<String>();
        StringRegistry.memelist = new ArrayList<String>();
    }
}
