package net.bfcode.bfhcf.utils;

import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.base.Optional;

public class GuavaCompat {
	
    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull(value);
        try {
            return (Optional<T>)Optional.of(Enum.valueOf(enumClass, value));
        }
        catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }
    
    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return (T)((first != null) ? first : Preconditions.checkNotNull(second));
    }
}
