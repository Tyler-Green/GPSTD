package green.tyler.gpstd;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fire_Tower extends Tower {
    Fire_Tower() {
        super();
    }
    MarkerOptions createOptions(Resources res, LatLng latLng) {
        MarkerOptions options = super.createOptions(res, latLng);
        options.title("Fire Tower");
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(res, R.mipmap.ic_fire_tower)));
        return options;
    }
}
