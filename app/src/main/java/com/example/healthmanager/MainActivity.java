package com.example.healthmanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPagerTips;

    Handler sliderHandler = new Handler(Looper.getMainLooper());

    Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPagerTips = findViewById(R.id.viewPagerTips);

        List<TipModel> tipsList = new ArrayList<>();

        tipsList.add(new TipModel(
                "Recuerda dormir 8h",
                R.drawable.tip_sleep
        ));

        tipsList.add(new TipModel(
                "Consume alimentos naturales",
                R.drawable.tip_food
        ));

        tipsList.add(new TipModel(
                "Reduce el sedentarismo",
                R.drawable.tip_walk
        ));

        TipsAdapter adapter = new TipsAdapter(tipsList);

        viewPagerTips.setAdapter(adapter);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {

                int nextItem = viewPagerTips.getCurrentItem() + 1;

                if (nextItem >= tipsList.size()) {
                    nextItem = 0;
                }

                viewPagerTips.setCurrentItem(nextItem, true);

                sliderHandler.postDelayed(this, 3000);
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 3000);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sliderHandler.removeCallbacks(sliderRunnable);
    }

}