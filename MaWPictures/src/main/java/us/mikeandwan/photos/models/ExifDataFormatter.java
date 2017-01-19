package us.mikeandwan.photos.models;

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


    public static String formatOneDecimal(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(val);
    }


    public static String formatFourDecimals(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("0.0000");

        return df.format(val);
    }


    public static String formatMillimeters(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(val) + " mm";
    }


    public static String formatMeters(Double val) {
        if (val == null) {
            return "--";
        }

        DecimalFormat df = new DecimalFormat("0.0");

        return df.format(val) + " m";
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


    public static String formatAltitude(Double val) {
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
