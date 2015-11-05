package com.basgeekball.screenshotsnanny;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.basgeekball.screenshotsnanny.activityassistant.ActivityCounter;
import com.basgeekball.screenshotsnanny.core.Constants;
import com.basgeekball.screenshotsnanny.demo.R;
import com.basgeekball.screenshotsnanny.demo.activities.MainActivity;
import com.basgeekball.screenshotsnanny.demo.activities.MapsActivity;
import com.basgeekball.screenshotsnanny.demo.activities.NetworkActivity;
import com.basgeekball.screenshotsnanny.demo.activities.SecondActivity;
import com.basgeekball.screenshotsnanny.demo.network.GithubService;
import com.basgeekball.screenshotsnanny.helper.Callback;
import com.basgeekball.screenshotsnanny.helper.LanguageSwitcher;
import com.basgeekball.screenshotsnanny.helper.ParameterizedCallback;
import com.basgeekball.screenshotsnanny.helper.PowerChanger;
import com.basgeekball.screenshotsnanny.helper.ResourceReader;
import com.basgeekball.screenshotsnanny.mockserver.MockServerWrapper;

import static com.basgeekball.screenshotsnanny.activityassistant.ActivityLauncher.startActivityAndTakeScreenshot;
import static com.basgeekball.screenshotsnanny.activityassistant.ActivityLauncher.startActivityContainsMapAndTakeScreenshot;

public class ScreenshotsPrimeActivity extends AppCompatActivity {

    private MockServerWrapper mServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots_prime);

        mServer = new MockServerWrapper();
        String response = ResourceReader.readFromRawResource(ScreenshotsPrimeActivity.this, R.raw.github_user);
        ParameterizedCallback changeUrlCallback = new ParameterizedCallback() {
            @Override
            public void execute(String value) {
                PowerChanger.changeFinalString(GithubService.class, "API_URL", value);
            }
        };
        mServer.start(changeUrlCallback, response);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if you want to have screenshots in different language
        // LanguageSwitcher.change(this, "de");

        startActivityAndTakeScreenshot(MainActivity.class, new Callback() {
            @Override
            public void execute() {
                startActivity(new Intent(ScreenshotsPrimeActivity.this, MainActivity.class));
            }
        });

        startActivityAndTakeScreenshot(SecondActivity.class, new Callback() {
            @Override
            public void execute() {
                startActivity(SecondActivity.createIntent(ScreenshotsPrimeActivity.this, "London bridge is falling down"));
            }
        });

        startActivityAndTakeScreenshot(NetworkActivity.class, new Callback() {
            @Override
            public void execute() {
                startActivity(new Intent(ScreenshotsPrimeActivity.this, NetworkActivity.class));
            }
        });

        startActivityContainsMapAndTakeScreenshot(MapsActivity.class, new Callback() {
            @Override
            public void execute() {
                startActivity(new Intent(ScreenshotsPrimeActivity.this, MapsActivity.class));
            }
        }, R.id.map);

        if (!ActivityCounter.isAnyActivityRunning) {
            Log.i(Constants.LOG_TAG, "⚙ Done.");
            mServer.stop();
            finish();
        }
    }
}
