package ravenweave.client.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {
    public int nextInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}
