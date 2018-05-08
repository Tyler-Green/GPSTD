package green.tyler.gpstd;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /*
     * onCreate
     * Runs When The Application Has Been Opened
     * Sets Orientation and Starts The Apps Basic Setup
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Set The Screen Orientation*/
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*Request Permission For Location Services*/
        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        /*Set The Content View*/
        setContentView(R.layout.activity_main);
    }
}
/*
Google Maps
Google Places
Google Map Directions
Google Maps Roads
*/