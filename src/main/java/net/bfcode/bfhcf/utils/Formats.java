package net.bfcode.bfhcf.utils;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.time.FastDateFormat;

import com.google.common.base.Preconditions;

public class Formats {

    private static AtomicBoolean loaded = new AtomicBoolean(false);

    public static FastDateFormat DAY_MTH_HR_MIN_SECS;
    public static FastDateFormat DAY_MTH_YR_HR_MIN_AMPM;
    public static FastDateFormat DAY_MTH_HR_MIN_AMPM;
    public static FastDateFormat HR_MIN_AMPM;
    public static FastDateFormat MNT_DAY_HR_MIN_AMPH;
    public static FastDateFormat HR_MIN_AMPM_TIMEZONE;
    public static FastDateFormat HR_MIN;
    public static FastDateFormat KOTH_FORMAT;

    private Formats() {
    }

    public static void reload(TimeZone timeZone) throws IllegalStateException {
        Preconditions.checkArgument(!loaded.getAndSet(true), "Already loaded");

        DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", timeZone, Locale.US);
        MNT_DAY_HR_MIN_AMPH = FastDateFormat.getInstance("MM/dd HH:mm:ss", timeZone, Locale.US);
        DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", timeZone, Locale.US);
        DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", timeZone, Locale.US);
        HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", timeZone, Locale.US);
        HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", timeZone, Locale.US);
        HR_MIN = FastDateFormat.getInstance("hh:mm", timeZone, Locale.US);
        KOTH_FORMAT = FastDateFormat.getInstance("m:ss", timeZone, Locale.US);
    }

    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ThreadLocal<DecimalFormat> SECONDS = new ThreadLocal() {
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0");
		}
	};
    

	// The format used to show one decimal without a trailing zero.
	public static ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.#");
		}
	};

	public static ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.0");
		}
	};

	public static TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
	public static ZoneId SERVER_ZONE_ID = SERVER_TIME_ZONE.toZoneId();
}
