package green.tyler.gpstd;

import com.google.android.gms.maps.model.MarkerOptions;

public class Zombie extends Enemy {
    @Override
    MarkerOptions createOptions() {
        MarkerOptions options = super.createOptions();
        options.title("Zombie");
        //options.icon();
        return options;
    }
}
