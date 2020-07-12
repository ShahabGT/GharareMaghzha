package ir.ghararemaghzha.game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.fragments.LoginFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_container,new LoginFragment())
                .commit();

    }
}