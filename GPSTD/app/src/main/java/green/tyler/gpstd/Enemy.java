package green.tyler.gpstd;

import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Enemy {

    protected Marker marker;
    protected int health;
    protected double range;
    protected int attack;

    Enemy() {
        health = 1;
        range = 1;
        attack = 1;
    }

    void update() {
        marker.setPosition(new LatLng(marker.getPosition().latitude+0.25, marker.getPosition().longitude));
    }

    void setMarker(Marker _MARKER) {
        marker = _MARKER;
    }

    MarkerOptions createOptions(Resources res, LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.title("Enemy");
        options.draggable(false);
        options.flat(true);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        options.position(latLng);
        return options;
    }
}
