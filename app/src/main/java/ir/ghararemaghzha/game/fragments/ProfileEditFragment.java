package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.shahabazimi.instagrampicker.InstagramPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileEditFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private ImageView avatar;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText bday;
    private TextInputEditText invite;
    private TextInputLayout inviteLayout;
    private View bdayView;
    private MaterialTextView avatarChange, avatarRemove;
    private MaterialButton save;
    private CheckBox female, male;
    private ConstraintLayout loading;

    private boolean uploading;


    public ProfileEditFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        context = getContext();
        activity = getActivity();

        init(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.findViewById(R.id.toolbar_avatar).setEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.findViewById(R.id.toolbar_avatar).setEnabled(true);
    }

    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.profile_edit_subtitle);


        uploading = false;

        name = v.findViewById(R.id.profile_name);
        invite = v.findViewById(R.id.profile_invite);
        inviteLayout = v.findViewById(R.id.profile_invite_layout);
        TextInputEditText number = v.findViewById(R.id.profile_number);
        email = v.findViewById(R.id.profile_email);
        bday = v.findViewById(R.id.profile_bday);
        bdayView = v.findViewById(R.id.profile_bday_view);
        avatar = v.findViewById(R.id.profile_avatar);
        avatarChange = v.findViewById(R.id.profile_avatar_change);
        avatarRemove = v.findViewById(R.id.profile_avatar_remove);

        save = v.findViewById(R.id.profile_save);
        male = v.findViewById(R.id.profile_male);
        female = v.findViewById(R.id.profile_female);

        loading = v.findViewById(R.id.profile_loading);

        name.setText(MySharedPreference.Companion.getInstance(context).getUsername());
        number.setText(MySharedPreference.Companion.getInstance(context).getNumber());
        bday.setText(MySharedPreference.Companion.getInstance(context).getUserBirthday());
        email.setText(MySharedPreference.Companion.getInstance(context).getUserEmail());
        String sex = MySharedPreference.Companion.getInstance(context).getUserSex();
        if (sex.equals("male")) {
            male.setChecked(true);
            female.setChecked(false);
        } else if (sex.equals("female")) {
            male.setChecked(false);
            female.setChecked(true);
        }

        if (!MySharedPreference.Companion.getInstance(context).getUserInvite().isEmpty()) {
            invite.setText(MySharedPreference.Companion.getInstance(context).getUserInvite());
            invite.setEnabled(false);
            inviteLayout.setEnabled(false);
        }


        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.Companion.getInstance(context).getUserAvatar()))
                .circleCrop()
                .placeholder(R.drawable.placeholder)
                .into(avatar);

        if (MySharedPreference.Companion.getInstance(context).getUserAvatar().isEmpty())
            avatarRemove.setVisibility(View.GONE);

        onClicks();
    }

    private void onClicks() {
        avatarChange.setOnClickListener(v -> {
                    InstagramPicker in = new InstagramPicker(activity);

                    in.show('1', '1', address -> {
                        try {
                            if (Utils.checkInternet(activity)) {
                                if (!uploading) {
                                    uploading = true;
                                    changeAvatar(Uri.parse(address));
                                }
                            } else
                                Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
        );
        avatarRemove.setOnClickListener(v -> {
            if (Utils.checkInternet(activity)) {
                String avatarName = MySharedPreference.Companion.getInstance(context).getUserAvatar();
                if (!avatarName.isEmpty())
                    removeAvatar(avatarName);

            } else
                Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        });

        bdayView.setOnClickListener(v -> selectDate());

        male.setOnCheckedChangeListener((a, b) -> {
            if (b)
                female.setChecked(false);
        });
        female.setOnCheckedChangeListener((a, b) -> {
            if (b)
                male.setChecked(false);
        });

        save.setOnClickListener(v -> {
            String i = "";
            if (invite.isEnabled() && !Objects.requireNonNull(invite.getText()).toString().trim().isEmpty())
                i = invite.getText().toString();
            String n = Objects.requireNonNull(name.getText()).toString() + "";
            String e = Objects.requireNonNull(email.getText()).toString() + "";
            String b = Objects.requireNonNull(bday.getText()).toString() + "";
            String s = "";
            if (female.isChecked() && !male.isChecked())
                s = "female";
            else if (male.isChecked() && !female.isChecked())
                s = "male";
            if (n.isEmpty() || n.length() < 6)
                Toast.makeText(context, getString(R.string.general_name_form_error), Toast.LENGTH_SHORT).show();
            else if (!e.isEmpty() && !Utils.isEmailValid(e))
                Toast.makeText(context, getString(R.string.general_email_form_error), Toast.LENGTH_SHORT).show();
            else if (Utils.checkInternet(activity))
                updateProfile(n, e, b, s, i);
            else
                Toast.makeText(context, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();

        });


    }

    private void selectDate() {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            color = getResources().getColor(R.color.colorPrimary, null);
        else
            color = getResources().getColor(R.color.colorPrimary);

        new PersianDatePickerDialog(context)
                .setPositiveButtonString("انتخاب")
                .setNegativeButton("بستن")
                .setTodayButton("امروز")
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setActionTextColor(color)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(ir.hamsaa.persiandatepicker.util.PersianCalendar persianCalendar) {
                        String date = persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay();
                        bday.setText(date);
                    }

                    @Override
                    public void onDismissed() {
                    }
                }).show();
    }

    private String toBase64(Uri path) throws Exception {
        Bitmap bitmap = scale(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private int getOrientation(Uri photoUri) {
        Cursor cursor = activity.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }

            cursor.moveToFirst();
            return cursor.getInt(0);
        } else
            return -1;

    }

    private Bitmap scale(Uri photoUri) throws Exception {
        InputStream is = activity.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        if (is != null)
            is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        int MAX_IMAGE_DIMENSION = 500;
        Bitmap srcBitmap;
        is = activity.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        if (is != null)

            is.close();

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            if (srcBitmap != null)
                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    private void changeAvatar(Uri image) {
        save.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String token = MySharedPreference.Companion.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }
        String pic;

        try {
            pic = toBase64(image);
        } catch (Exception e) {
            save.setEnabled(true);
            loading.setVisibility(View.GONE);
            Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            return;
        }

        String avatarName = MySharedPreference.Companion.getInstance(context).getUserId() + Utils.currentDate().replace("-", "").replace(":", "").replace(" ", "");

        RetrofitClient.Companion.getInstance().getApi()
                .alterAvatar("Bearer " + token, number, "change", pic, avatarName).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                save.setEnabled(true);
                uploading = false;
                loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                    Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                    MySharedPreference.Companion.getInstance(context).setUserAvatar(avatarName);
                    Glide.with(context)
                            .load(getString(R.string.avatar_url, avatarName))
                            .circleCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(avatar);
                    avatarRemove.setVisibility(View.VISIBLE);

                    MainActivity.setAvatars(activity);
                } else if (response.code() == 401) {
                    Utils.logout(activity, true);
                } else {
                    Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                save.setEnabled(true);
                uploading = false;
                loading.setVisibility(View.GONE);
                Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void removeAvatar(String avatarName) {
        save.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String token = MySharedPreference.Companion.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .alterAvatar("Bearer " + token, number, "remove", "", avatarName).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                save.setEnabled(true);
                loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                    Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                    MySharedPreference.Companion.getInstance(context).setUserAvatar("");
                    Glide.with(context)
                            .load(getString(R.string.avatar_url, MySharedPreference.Companion.getInstance(context).getUserAvatar()))
                            .circleCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(avatar);
                    avatarRemove.setVisibility(View.GONE);
                    MainActivity.setAvatars(activity);
                } else if (response.code() == 401) {
                    Utils.logout(activity, true);
                } else {
                    Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                save.setEnabled(true);
                loading.setVisibility(View.GONE);
                Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile(String name, String email, String bday, String sex, String inviteCode) {
        save.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        save.setText("...");
        String number = MySharedPreference.Companion.getInstance(context).getNumber();
        String token = MySharedPreference.Companion.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity, true);
            return;
        }
        RetrofitClient.Companion.getInstance().getApi()
                .updateProfile("Bearer " + token, number, name, email, bday, sex, inviteCode)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        save.setEnabled(true);
                        save.setText(getString(R.string.profile_save));
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            Toast.makeText(context, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                            MySharedPreference.Companion.getInstance(context).setUsername(name);
                            MySharedPreference.Companion.getInstance(context).setUserEmail(email);
                            MySharedPreference.Companion.getInstance(context).setUserSex(sex);
                            MySharedPreference.Companion.getInstance(context).setUserBirthday(bday);

                            switch (response.body().getMessage()) {
                                case "invite not found":
                                    Toast.makeText(context, getString(R.string.profile_invite_notfound), Toast.LENGTH_SHORT).show();
                                    break;
                                case "invite failed":
                                    Toast.makeText(context, getString(R.string.profile_invite_failed), Toast.LENGTH_SHORT).show();
                                    break;
                                case "invite ok":
                                    MySharedPreference.Companion.getInstance(context).setUserInvite(inviteCode);
                                    invite.setEnabled(false);
                                    inviteLayout.setEnabled(false);

                                    break;
                            }

                        } else if (response.code() == 401) {
                            Utils.logout(activity, true);
                        } else {
                            Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        save.setEnabled(true);
                        save.setText(getString(R.string.profile_save));
                        loading.setVisibility(View.GONE);
                        Toast.makeText(context, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}