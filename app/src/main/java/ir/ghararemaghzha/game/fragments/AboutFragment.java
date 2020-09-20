package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ir.ghararemaghzha.game.R;


public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView text,tradeMark, tradeMark2, tradeMark3;
    private ImageView telegram, instagram, website, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        context = getContext();
        activity = getActivity();
        init(v);
        return v;
    }

    private void init(View v) {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.about_title);

        text = v.findViewById(R.id.about_text);
        text.setText(getText());
        tradeMark = v.findViewById(R.id.about_trademark1);
        tradeMark2 = v.findViewById(R.id.about_trademark2);
        tradeMark3 = v.findViewById(R.id.about_trademark3);

        String tradeMarkText = context.getString(R.string.about_trademark1);
        SpannableString spannableString = new SpannableString(tradeMarkText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://tajrannoyan.com"));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.red));
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, 37, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tradeMark.setText(spannableString);
        tradeMark.setMovementMethod(LinkMovementMethod.getInstance());

        String tradeMarkText2 = context.getString(R.string.about_trademark2);
        SpannableString spannableString2 = new SpannableString(tradeMarkText2);
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://shahabazimi.ir"));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.red));
                ds.setUnderlineText(true);
            }
        };
        spannableString2.setSpan(clickableSpan2, 36, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tradeMark2.setText(spannableString2);
        tradeMark2.setMovementMethod(LinkMovementMethod.getInstance());


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            tradeMark3.setText(getString(R.string.about_trademark3, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        telegram = v.findViewById(R.id.about_telegram);
        instagram = v.findViewById(R.id.about_instagram);
        website = v.findViewById(R.id.about_website);
        email = v.findViewById(R.id.about_email);

        onClicks();
    }

    private String getText() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.about);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder text = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    private void onClicks() {
        email.setOnClickListener(View -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "support@ghararehmaghzha.ir", null));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ghararehmaghzha@radical-app.ir"});
            startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل از طریق"));
        });
        telegram.setOnClickListener(View ->
                intentAction("https://t.me/ghararehmaghzha")
        );

        instagram.setOnClickListener(View ->
                intentAction("https://instagram.com/ghararehmaghzha")
        );

        website.setOnClickListener(View ->
                intentAction("https://ghararehmaghzha.ir")
        );
    }

    private void intentAction(String id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(id));
        startActivity(intent);
    }
}