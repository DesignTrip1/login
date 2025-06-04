package com.example.design.roulette;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

public class RouletteActivity extends AppCompatActivity {

    RouletteView rouletteView;
    Button spinButton;
    Button showSliderButton;
    TextView resultText;
    String finalResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        rouletteView = findViewById(R.id.roulette);
        spinButton = findViewById(R.id.spin_button);
        showSliderButton = findViewById(R.id.show_slider_button);
        resultText = findViewById(R.id.result_text);

        spinButton.setOnClickListener(v -> rouletteView.spin());

        rouletteView.setOnRouletteResultListener(result -> runOnUiThread(() -> {
            finalResult = result;
            showResultText(result);
            showSliderButton.setVisibility(View.VISIBLE);  // 결과 나오면 버튼 보이게
        }));

        showSliderButton.setOnClickListener(v -> {
            if (finalResult != null) {
                Intent intent = new Intent(RouletteActivity.this, RouletteSliderActivity.class);
                intent.putExtra("region", finalResult);
                startActivity(intent);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("룰렛");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showResultText(String result) {
        resultText.setText("결과: " + result);
        resultText.setVisibility(View.VISIBLE);
        resultText.setAlpha(0f);
        resultText.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }
}
