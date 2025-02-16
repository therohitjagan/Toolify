package com.therohitjagan.toolify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.therohitjagan.toolify.R;

public class OnboardingAdapter extends PagerAdapter {
    private final Context context;
    private LayoutInflater layoutInflater;

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;  // Number of onboarding screens
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboarding_page, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        // Set content for each page with new styles
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.onboarding_welcome);
                textView.setText("Welcome to our app! Explore amazing features.");
                break;
            case 1:
                imageView.setImageResource(R.drawable.onboarding_qr);
                textView.setText("Track your progress with ease and stay updated.");
                break;
            case 2:
                imageView.setImageResource(R.drawable.onboarding_tools);
                textView.setText("Join us today and get started right away!");
                break;
        }

        container.addView(view);

        // Apply fade-in animation for text and zoom-in effect for images
        imageView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, R.anim.zoom_in));
        textView.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

