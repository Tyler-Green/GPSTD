package green.tyler.gpstd;

import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Tower {
    protected Marker marker;
    protected double rof;
    protected double range;
    protected int attack;

    Tower() {
        rof = 1;
        range = 1;
        attack = 1;
    }

    void setMarker(Marker _MARKER) {
        marker = _MARKER;
    }

    MarkerOptions createOptions(Resources res, LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.title("Tower");
        options.draggable(false);
        options.flat(true);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        options.position(latLng);
        return options;
    }
}
