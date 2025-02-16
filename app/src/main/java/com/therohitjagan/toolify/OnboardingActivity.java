package com.therohitjagan.toolify;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.therohitjagan.toolify.adapter.OnboardingAdapter;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private OnboardingAdapter onboardingAdapter;
    private SharedPreferences sharedPreferences;
    private LinearLayout dotsLayout;
    private Button btnFinish;
    private int[] layouts;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (isFirstTimeUser()) {
            viewPager = findViewById(R.id.viewPager);
            onboardingAdapter = new OnboardingAdapter(this);
            viewPager.setAdapter(onboardingAdapter);

            // Set up the dots for the ViewPager
            dotsLayout = findViewById(R.id.dotsLayout);
            setupDots();

            // Finish button click listener
            btnFinish = findViewById(R.id.btnFinish);
            btnFinish.setOnClickListener(v -> {
                markOnboardingCompleted();
                startMainActivity();
            });

            // Add page change listener to update dots
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        } else {
            startMainActivity();
        }
    }

    private boolean isFirstTimeUser() {
        return sharedPreferences.getBoolean("isFirstTimeUser", true);
    }

    private void markOnboardingCompleted() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTimeUser", false);
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupDots() {
        dots = new ImageView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], params);
        }
    }

    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < dots.length; i++) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));
            }

            dots[position].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };
}
