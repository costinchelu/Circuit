package ro.ase.costin.ecomback.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DelayedFormatter {

    public static Object format(String format, Object... args) {
        return new Object() {
            @Override
            public String toString() {
                return String.format(format, args);
            }
        };
    }
}
