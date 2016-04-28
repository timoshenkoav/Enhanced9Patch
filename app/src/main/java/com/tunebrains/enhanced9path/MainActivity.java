package com.tunebrains.enhanced9path;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tunebrains.loggerlib.AndroidLogStrategy;
import com.tunebrains.loggerlib.LogLevels;
import com.tunebrains.loggerlib.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.clearStrategies();
        Logger.registerStrategy(new AndroidLogStrategy(), LogLevels.LEVEL.VERBOSE);
        LinearLayout lImageView = (LinearLayout) findViewById(R.id.image_view);

        Enhanced9Patch lEnhanced9Patch = new Enhanced9Patch(BitmapFactory.decodeResource(getResources(), R.drawable.highlight));

        lEnhanced9Patch.stretchWidthRegionTo(1, 200);
        lEnhanced9Patch.stretchWidthRegionTo(3, 200);
        lEnhanced9Patch.stretchHeightRegionTo(1, 200);

        lImageView.setBackgroundDrawable(lEnhanced9Patch);
    }
}
