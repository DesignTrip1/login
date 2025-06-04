package com.example.design;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RouletteActivity extends AppCompatActivity {
    RouletteView rouletteView;
    Button spinButton;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        rouletteView = findViewById(R.id.roulette);
        spinButton = findViewById(R.id.spin_button);
        resultText = findViewById(R.id.result_text);
        spinButton.setOnClickListener(v -> rouletteView.spin());

        rouletteView.setOnRouletteResultListener(result -> runOnUiThread(() -> showResultText(result)));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("룰렛");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showResultText(String result) {
        resultText.setText("결과: " + result);
        resultText.setVisibility(View.VISIBLE);
        resultText.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction(() -> resultText.postDelayed(() ->
                                resultText.animate()
                                        .alpha(0f)
                                        .setDuration(300)
                                        .withEndAction(() -> resultText.setVisibility(View.GONE))
                        , 2000))
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
