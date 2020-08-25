package ir.ghararemaghzha.game.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.shahabazimi.instagrampicker.InstagramPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageView avatar;
    private TextInputEditText name, number, email, bday, invite;
    private TextInputLayout inviteLayout;
    private View bdayView;
    private MaterialTextView avatarChange, avatarRemove;
    private boolean canGoBack;
    private MaterialButton save;
    private CheckBox female, male;
    private ConstraintLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        init();
    }


    private void init() {
        canGoBack = true;
        findViewById(R.id.profile_close).setOnClickListener(v -> onBackPressed());

        name = findViewById(R.id.profile_name);
        invite = findViewById(R.id.profile_invite);
        inviteLayout = findViewById(R.id.profile_invite_layout);
        number = findViewById(R.id.profile_number);
        email = findViewById(R.id.profile_email);
        bday = findViewById(R.id.profile_bday);
        bdayView = findViewById(R.id.profile_bday_view);
        avatar = findViewById(R.id.profile_avatar);
        avatarChange = findViewById(R.id.profile_avatar_change);
        avatarRemove = findViewById(R.id.profile_avatar_remove);

        save = findViewById(R.id.profile_save);
        male = findViewById(R.id.profile_male);
        female = findViewById(R.id.profile_female);

        loading = findViewById(R.id.profile_loading);

        name.setText(MySharedPreference.getInstance(this).getUsername());
        number.setText(MySharedPreference.getInstance(this).getNumber());
        bday.setText(MySharedPreference.getInstance(this).getUserBday());
        email.setText(MySharedPreference.getInstance(this).getEmail());
        String sex = MySharedPreference.getInstance(this).getUserSex();
        if (sex.equals("male")) {
            male.setChecked(true);
            female.setChecked(false);
        } else if (sex.equals("female")) {
            male.setChecked(false);
            female.setChecked(true);
        }

        if (!MySharedPreference.getInstance(this).getUserInvite().isEmpty()) {
            invite.setText(MySharedPreference.getInstance(this).getUserInvite());
            invite.setEnabled(false);
            inviteLayout.setEnabled(false);
        }


        Glide.with(this)
                .load(getString(R.string.avatar_url, MySharedPreference.getInstance(this).getUserId()))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder)
                .into(avatar);

        onClicks();
    }

    private void onClicks() {
        avatarChange.setOnClickListener(v -> {
            InstagramPicker in = new InstagramPicker(this);
            in.show('1', '1', address -> {
                try {
                    if (Utils.checkInternet(ProfileActivity.this))
                        changeAvatar(Uri.parse(address));
                    else
                        Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        });
        avatarRemove.setOnClickListener(v -> {
            if (Utils.checkInternet(ProfileActivity.this))
                removeAvatar();
            else
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        });

        bdayView.setOnClickListener(v -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            PersianCalendar maxDate = new PersianCalendar();
            maxDate.setPersianDate(1390, 1, 1);

            PersianCalendar minDate = new PersianCalendar();
            minDate.setPersianDate(1300, 1, 1);
            datePickerDialog.setMaxDate(maxDate);
            datePickerDialog.setMinDate(minDate);
            datePickerDialog.setFirstDayOfWeek(Calendar.SATURDAY);
            datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
        });

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
            if (invite.isEnabled() && !invite.getText().toString().trim().isEmpty())
                i = invite.getText().toString();
            String n = name.getText().toString();
            String e = email.getText().toString();
            String b = bday.getText().toString();
            String s = "";
            if (female.isChecked() && !male.isChecked())
                s = "female";
            else if (male.isChecked() && !female.isChecked())
                s = "male";
            if (n.isEmpty() || e.isEmpty() || b.isEmpty() || s.isEmpty() || n.length() < 6 || !Utils.isEmailValid(e))
                Toast.makeText(this, getString(R.string.general_form_error), Toast.LENGTH_SHORT).show();
            else if (Utils.checkInternet(ProfileActivity.this))
                updateProfile(n, e, b, s, i);
            else
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();

        });


    }

    private String toBase64(Uri path) throws Exception {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void changeAvatar(Uri image) {
        canGoBack = false;
        save.setEnabled(false);
        loading.setVisibility(View.VISIBLE);
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true);
            return;
        }
        String pic;

        try {
            pic = toBase64(image);
        } catch (Exception e) {
            save.setEnabled(true);
            canGoBack = true;
            loading.setVisibility(View.GONE);
            Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApi()
                .alterAvatar("Bearer " + token, number, "change", pic).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                    save.setEnabled(true);
                    canGoBack = true;
                    loading.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                    avatar.setImageURI(image);
                } else if (response.code() == 401) {
                    Utils.logout(ProfileActivity.this, true);
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                save.setEnabled(true);
                canGoBack = true;
                loading.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void removeAvatar() {
        save.setEnabled(false);
        canGoBack = false;
        loading.setVisibility(View.VISIBLE);
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true);
            return;
        }

        RetrofitClient.getInstance().getApi()
                .alterAvatar("Bearer " + token, number, "remove", "").enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                save.setEnabled(true);
                canGoBack = true;
                loading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                    Toast.makeText(ProfileActivity.this, getString(R.string.general_save), Toast.LENGTH_SHORT).show();

                } else if (response.code() == 401) {
                    Utils.logout(ProfileActivity.this, true);
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                save.setEnabled(true);
                canGoBack = true;
                loading.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateProfile(String name, String email, String bday, String sex, String inviteCode) {
        save.setEnabled(false);
        canGoBack = false;
        loading.setVisibility(View.VISIBLE);
        save.setText("...");
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this, true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .updateProfile("Bearer " + token, number, name, email, bday, sex, inviteCode)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call, @NonNull Response<GeneralResponse> response) {
                        save.setEnabled(true);
                        save.setText(getString(R.string.profile_save));
                        canGoBack = true;
                        loading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            Toast.makeText(ProfileActivity.this, getString(R.string.general_save), Toast.LENGTH_SHORT).show();
                            MySharedPreference.getInstance(ProfileActivity.this).setUsername(name);
                            MySharedPreference.getInstance(ProfileActivity.this).setUserEmail(email);
                            MySharedPreference.getInstance(ProfileActivity.this).setUserSex(sex);
                            MySharedPreference.getInstance(ProfileActivity.this).setUserBday(bday);

                            switch (response.body().getMessage()) {
                                case "invite not found":
                                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_invite_notfound), Toast.LENGTH_SHORT).show();
                                    break;
                                case "invite failed":
                                    Toast.makeText(ProfileActivity.this, getString(R.string.profile_invite_failed), Toast.LENGTH_SHORT).show();
                                    break;
                                case "invite ok":
                                    MySharedPreference.getInstance(ProfileActivity.this).setUserInvite(inviteCode);
                                    invite.setEnabled(false);
                                    inviteLayout.setEnabled(false);

                                    break;
                            }

                        } else if (response.code() == 401) {
                            Utils.logout(ProfileActivity.this, true);
                        } else {
                            Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call, @NonNull Throwable t) {
                        save.setEnabled(true);
                        save.setText(getString(R.string.profile_save));
                        canGoBack = true;
                        loading.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
        bday.setText(date);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack)
            super.onBackPressed();
    }
}