package com.pixelcraft.whisperlink.Utils;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatterHelp {
    public static String FormatTime(Timestamp timestamp, String format){
        if (timestamp!=null){
            Date date = timestamp.toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return dateFormat.format(date);
        }else
            return "hata";
    }
}
