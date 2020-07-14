package ir.ghararemaghzha.game.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.fragments.LoginFragment;

public class RegisterActivity extends AppCompatActivity {
    private static final int SMS_CONSENT_REQUEST = 325;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_container,new LoginFragment())
                .commit();

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == RESULT_OK && data!=null) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                if(message!=null && !message.isEmpty()) {
                    String oneTimeCode = message.substring(message.length() - 6);
                    Intent intent = new Intent("codeReceived");
                    intent.putExtra("code", oneTimeCode);
                    sendBroadcast(intent);
                }
            }
        }
    }

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction()) && intent.getExtras()!=null) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                if (smsRetrieverStatus!=null && smsRetrieverStatus.getStatusCode() == CommonStatusCodes.SUCCESS) {
                    Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    try {
                        startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}