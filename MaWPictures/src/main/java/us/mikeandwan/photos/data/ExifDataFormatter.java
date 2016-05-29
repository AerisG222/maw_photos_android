package us.mikeandwan.photos.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ExifDataFormatter {
    public static String format(Date val) {
        if (val == null) {
            return "--";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);

        return sdf.format(val);
    }


    public static String format(String val) {
        if (val == null) {
            return "--";
        }

        return val;
    }


    public static String format(Double val) {
        if (val == null) {
            return "--";
        }

        return val.toString();
    }


    public static String format(Byte val) {
        if (val == null) {
            return "--";
        }

        return val.toString();
    }


    public static String format(Short val) {
        if (val == null) {
            return "--";
        }

        return val.toString();
    }


    public static String format(Integer val) {
        if (val == null) {
            return "--";
        }

        return val.toString();
    }


    public static String formatInvertTime(Double val) {
        if (val == null) {
            return "--";
        }

        if (val >= 1) {
            return val.toString();
        }

        return "1/" + Math.round(1.0 / val);
    }


    public static String formatFocalLengthOneDecimal(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("#.0");

        return df.format(val) + " mm";
    }


    public static String formatFocalLengthNoDecimals(Double val) {
        if (val == null) {
            return "--";
        }

        return val + " mm";
    }


    public static String formatOneDecimal(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("#.0");

        return df.format(val);
    }


    public static String formatOneDecimalMm(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("#.0");

        return df.format(val) + " mm";
    }


    public static String formatDistance(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("#.0");

        return df.format(val) + " mm";
    }


    public static String formatLatitude(Double val) {
        if (val == null) {
            return "--";
        }

        if (val >= 0) {
            return val + " (North)";
        } else {
            return val + " (South)";
        }
    }


    public static String formatLongitude(Double val) {
        if (val == null) {
            return "--";
        }

        if (val >= 0) {
            return val + " (East)";
        } else {
            return val + " (West)";
        }
    }


    public static String formatAltitude(Short val) {
        if (val == null) {
            return "--";
        }

        if (val >= 0) {
            return val + " m Above Sea Level";
        } else {
            return val + " m Below Sea Level";
        }
    }
}
