package net.bfcode.bfhcf.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

public class ParserUtil {

	private static CharMatcher CHAR_MATCHER_ASCII;
	
	static {
		CHAR_MATCHER_ASCII = CharMatcher.inRange('0', '9').or(CharMatcher.inRange('a', 'z')).or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.WHITESPACE).precomputed();
	}
	
	public static boolean isAlphanumeric(String string) {
        return CHAR_MATCHER_ASCII.matchesAllOf(string);
    }
	
	public static long parseTime(String input) {
        if (input == null || input.isEmpty()) {
            return -1L;
        }
        long result = 0L;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            }
            else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }
	
	public static Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
	
	public static Long tryParseLong(String string) {
		try {
			return Long.parseLong(string);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static Double tryParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static Float tryParseFloat(String string) {
    	try {
			return Float.parseFloat(string);
		} catch (IllegalArgumentException e) {
			return null;
		}
    }
    
    public static Short tryParseShort(String string) {
    	try {
			return Short.parseShort(string);
		} catch (IllegalArgumentException e) {
			return null;
		}
    }
    
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isLong(String string) {
        try {
            Long.parseLong(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isShort(String string) {
    	try {
			Short.parseShort(string);
		} catch (NumberFormatException e) {
			return false;
		}
    	return true;
    }
    
    /**
     * <p>Source: http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g</p>
     */
    private static long convert(int value, char unit) {
        switch (unit) {
            case 'y' | 'Y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd' | 'D':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h' | 'H':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's' | 'S':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return -1L;
        }
    }
    
    /**
     * Parses a string describing measures of time (e.g. "1d 1m 1s") to milliseconds
     * <p>Source: http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g</p>
     *
     * @param input the string to parse
     * @return the parsed time in milliseconds or -1 if could not
     */
    public static long parse(String input) {
        if (input == null || input.isEmpty()) {
            return -1L;
        }

        long result = 0L;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
                continue;
            }

            String str;
            if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                result += convert(Integer.parseInt(str), c);
                number = new StringBuilder();
            }
        }

        return result;
    }
    
    /**
     * Formats a Number with {@link JavaUtils#DEFAULT_NUMBER_FORMAT_DECIMAL_PLACES} amount of decimal
     * places using {@link RoundingMode#HALF_DOWN} for calculating.
     *
     * @param number the {@link Number} to format
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number) {
        return format(number, 5);
    }

    /**
     * Formats a Number with a given amount of decimal places using {@link RoundingMode#HALF_DOWN}
     * for calculating.
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the decimal places to format to
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    /**
     * Formats a Number with a given amount of decimal places and a RoundingMode
     * to use for calculating.	
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the decimal places to format to
     * @param roundingMode  the {@link RoundingMode} for calculating
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Preconditions.checkNotNull(number, "The number cannot be null");
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }
}
