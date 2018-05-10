package green.tyler.gpstd;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Knight extends Enemy {
    Knight() {
        super();
    }
    MarkerOptions createOptions(Resources res, LatLng latLng) {
        MarkerOptions options = super.createOptions(res, latLng);
        options.title("Knight");
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(res, R.mipmap.ic_knight)));
        return options;
    }
}
