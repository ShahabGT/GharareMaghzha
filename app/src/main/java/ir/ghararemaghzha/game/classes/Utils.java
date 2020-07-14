package ir.ghararemaghzha.game.classes;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static boolean checkInternet(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
            return activeInfo != null && activeInfo.isConnected();

        } catch (NullPointerException e) {
            return false;
        }

    }

    public static boolean isTimeAcceptable(String serverDate){
        boolean res=false;
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String systemDate = dateFormat.format(d);
        System.out.println(systemDate);
        try {
            d=dateFormat.parse(serverDate);
            long server=d.getTime();
            d=dateFormat.parse(systemDate);
            long system = d.getTime();
            long diff = server-system;
            if(diff>=-600000 && diff<=600000){
                res=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getFbToken(Context context){
        AtomicReference<String> token= new AtomicReference<>("");
        if(!MySharedPreference.getInstance(context).getFbToken().isEmpty()){
            token.set(MySharedPreference.getInstance(context).getFbToken());
        }else {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            assert task.getResult()!=null;
                            token.set(task.getResult().getToken());
                        }
                    });
        }
        return token.get();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String moneySeparator(String text) {

        int len = text.length();
        if(len>3) {
            StringBuilder res= new StringBuilder();
            while(len>3) {
                res.insert(0, "," + text.substring(len - 3, len));

                len-=3;
            }

            res.insert(0, text.substring(0, len));
            return res.toString();
        }else {
            return text;
        }
    }

    public static String convertToTimeFormat(long millisecond){
        Date date = new Date(millisecond);
        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }


    public static void removeNotification(Context context){
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Const.NOTIFICATION_ID);
    }

}
