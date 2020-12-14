package ir.ghararemaghzha.game.classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.pm.PackageInfoCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.activities.SplashActivity;
import ir.ghararemaghzha.game.activities.SupportActivity;
import ir.ghararemaghzha.game.data.RemoteDataSource;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.GetDataDialog;
import ir.ghararemaghzha.game.dialogs.NoInternetDialog;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.MessageModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ALARM_SERVICE;
import static ir.ghararemaghzha.game.classes.Const.FCM_TOPIC;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_MESSAGE;
import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH;

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

    public static String currentDate() {
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(d);
    }

    public static boolean isTimeAcceptable(String serverDate) {
        boolean res = false;
        Date d = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String systemDate = dateFormat.format(d);
        try {
            d = dateFormat.parse(serverDate);
            long server = d != null ? d.getTime() : 0;
            d = dateFormat.parse(systemDate);
            long system = d == null ? 0 : d.getTime();
            long diff = server - system;
            if (diff >= -60000 && diff <= 60000) {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getFbToken(Context context) {
        if (MySharedPreference.getInstance(context).getFbToken().isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    MySharedPreference.getInstance(context).setFbToken(token);
                }
            });

        }
        return MySharedPreference.getInstance(context).getFbToken();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String moneySeparator(String text) {
        if(text==null) return "";
        int len = text.length();
        if (len > 3) {
            StringBuilder res = new StringBuilder();
            while (len > 3) {
                res.insert(0, "," + text.substring(len - 3, len));

                len -= 3;
            }

            res.insert(0, text.substring(0, len));
            return res.toString();
        } else {
            return text;
        }
    }

    public static String convertToTimeFormat(long millisecond) {
        Date date = new Date(millisecond);
        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static void removeNotification(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Const.NOTIFICATION_ID);
    }

    public static TimeDialog showTimeError(Context context) {
        TimeDialog dialog = new TimeDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static GetDataDialog showGetDataLoading(Context context) {
        GetDataDialog dialog = new GetDataDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static void showInternetError(Context context, RetryInterface retry) {
        NoInternetDialog dialog = new NoInternetDialog(context, retry);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static int getVersionCode(Context context) {
        int version = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = (int) PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static void logout(Activity activity, boolean showMessage) {
        if (showMessage)
            Toast.makeText(activity, activity.getString(R.string.access_error), Toast.LENGTH_LONG).show();
        MySharedPreference.getInstance(activity).clear();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FCM_TOPIC);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        activity.startActivity(new Intent(activity, SplashActivity.class));
        activity.finish();
    }

    public static void updateServerQuestions(Activity context, String questionCount) {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            logout(context, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .sendQuestionCount("Bearer " + token, number, questionCount)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {

                    }
                });

    }

    public static void updateScoreBooster(Context context, int count) {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            if (context instanceof Activity)
                logout((Activity) context, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .scoreBooster("Bearer " + token, number, count)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {

                    }
                });

    }

    public static int getNextKey(Realm db) {
        try {
            Number number = db.where(MessageModel.class).max("messageId");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        boolean res;
        Matcher matcher;
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        if (matcher.matches()) {
            email = email.toLowerCase();
            res = email.contains("@yahoo.") || email.contains("@gmail.") || email.contains("@aol.") || email.contains("@hotmail.") || email.contains("@ymail.") || email.contains("@live.");
        } else {
            res = false;
        }
        return res;
    }

    public static void shareCode(Context context, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, title);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.general_share)));

    }

    public static void createNotification(Context context, String title, String message, String clickAction) {
        Intent intent;
        if (clickAction.equals("ir.ghararemaghzha.game.TARGET_NOTIFICATION")) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(GHARAREHMAGHZHA_BROADCAST_MESSAGE,"new");
        }else {
            intent = new Intent(context, SupportActivity.class);
            intent.putExtra(GHARAREHMAGHZHA_BROADCAST_MESSAGE,"chat");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Const.CHANNEL_CODE);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setContentText(message);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setContentIntent(pendingIntent);
        Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.intro);
        builder.setSound(alarmSound, AudioManager.STREAM_NOTIFICATION);
        builder.setVibrate(new long[]{2000, 1000, 2000});
        builder.setLights(Color.YELLOW, 1000, 1000);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(Const.NOTIFICATION_ID, builder.build());


    }


    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Ghararemaghzha Message Notifications";
            String description = "Using this channel to display notification for Ghararemaghzha game";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(Const.CHANNEL_CODE, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
