package green.tyler.gpstd;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {


    private GoogleApiClient mGoogleApiClient;   //The API Client
    private final Semaphore mapSem = new Semaphore(1 , true);   //Semaphore Dealing With Concurrency Issues Between Connection and Map Initialization
    private double zoomconv[] = {21282,16355,10064,5540,2909,1485,752,378,190,95,48,24,12,6,3,1.48,0.74,0.37,0.19}; //Conversion From Zoom Level To Pixel Array
    private float zoom = 16f;   //Current Zoom Level

    /*
     * onStart
     * Runs When The Fragment Has Been Started
     * Acquires Semaphore And Starts Connection To API Client
     */
    @Override
    public void onStart() {
        super.onStart();
        /*Aquire The Semaphore To State Map Has Not Been Initialized*/
        try {
            mapSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*Start The API Client Connection*/
        mGoogleApiClient.connect();
    }

    /*
    * onStop
    * Runs When The Fragment Has Been Stopped
    * Disconnects From The API Client
    */
    @Override
    public void onStop() {
        super.onStop();
        /*Disconnect From The API Client*/
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*
    * onConnected
    * Runs When A Connection To The API Client Has Been Made
    * Acquires Current Location Then Runs The Camera Initializing Function
    */
    @Override
    public void onConnected(Bundle bundle) {
        /*Acquire Current Location*/
        Location mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        /*Run Camera Initialization*/
        initCamera(mCurrentLocation);
    }

    /*
    * onConnectionSuspended
    * Runs When The Connection To The API Client Has Been Suspended
    * Does Nothing
    */
    @Override
    public void onConnectionSuspended(int i) {
        //Todo save state upon connection suspension
    }

    /*
    * onConnectionFailed
    * Runs When The Connection To The API Client Has Failed
    * Does Nothing
    */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //todo add a try again popup with a quit option
    }

    /*
    * onInfoWindowClick
    * Runs When The Info Window Is Clicked
    * Unsure What The Info Window Is
    * Does Nothing
    */
    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    /*
    * onMapClick
    * Runs When There Has Been A Short Press On The Map
    * Places A Marker On The Map Where The Click Has Occurred
    */
    @Override
    public void onMapClick(LatLng latLng) {
        /*Create A New Marker Options*/
        MarkerOptions options = new MarkerOptions().position(latLng);
        /*Set The Title Based On The Location*/
        options.title(getAddressFromLatLng(latLng));
        /*Set The Icon To Default*/
        options.icon(BitmapDescriptorFactory.defaultMarker());
        /*Add Marker To The Map*/
        getMap().addMarker(options);
    }

    /*
    * onMapLongClick
    * Runs When There Has Been A Long Press On The Map
    * Places A Marker On The Map Where The Click Has Occurred
    */
    @Override
    public void onMapLongClick(LatLng latLng) {
        /*Create A new Marker*/
        MarkerOptions options = new MarkerOptions().position(latLng);
        /*Set The Title Based On Location*/
        options.title( getAddressFromLatLng(latLng));
        /*Set The Icon To The App Icon*/
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        /*Add The Marker To The Map*/
        getMap().addMarker(options);
    }

    /*
    * getAddressFromLatLng
    * Runs When There Has Been Any Type Of Click On The Map
    * Takes A latlng And Converts It Into A String Based On Where It Is Located Using A geocoder
    */
    private String getAddressFromLatLng(LatLng latLng) {
        /*Create  A geocoder*/
        Geocoder geocoder = new Geocoder(getActivity());
        /*Create The Sting and Initialize It*/
        String address = "";
        /*Attempt To Get The Loaction Name From The latlng*/
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Return The Name That Was Supplied*/
        return address;
    }

    /*
     * onMarkerClick
     * Runs When A Marker Is Clicked
     * Displays Information On The Marker
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        /*Display Information On The Marker*/
        marker.showInfoWindow();
        return true;
    }

    /*
     * onViewCreated
     * Runs When The View Has Been Created
     * Creates The API Client and Initialises Listeners
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*Allow For An Options Menu*/
        setHasOptionsMenu(true);
        /*Create A New API Client*/
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        /*Initialize Listeners*/
        initListeners();
    }

    /*
     * intiListeners
     * Runs After The View Has Been Created
     * Initializes The Listeners And Releases The Semaphore
     */
    private void initListeners() {
        /*Initilaize Map Listeners*/
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);
        /*Release The Semaphore*/
        mapSem.release();
    }

    /*
     * intiCamera
     * Runs After A Connection Is Made And The API Client Has Been Created
     * Creates An Initial View For The Map
     */
    private void initCamera(Location location) {
        /*Wait On The Map Semaphore*/
        try {
            mapSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*Create A New Position Based On Location*/
        //CameraPosition position = CameraPosition.builder()
        //        .target(new LatLng(/*location.getLatitude()*/32, /*location.getLongitude()*/58))
        //        .zoom(zoom)
        //        .bearing(0.0f)
        //        .tilt(0.0f)
        //        .build();
        /*Position The Camera To The Location*/
        //getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32, 58), zoom));
        /*Set The Map Options To Desired Options*/
        getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getMap().setBuildingsEnabled(true);
        getMap().setMyLocationEnabled(true);
        getMap().setTrafficEnabled(false);
        //TODO set max and mon zoom levels
        getMap().getUiSettings().setZoomControlsEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);
        getMap().getUiSettings().setAllGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMapToolbarEnabled(false);
        /*Create A Spawn Radius Cicrle*/
        CircleOptions spawnRad = new CircleOptions();
        /*Set The Center To The User*/
        //options.center(new LatLng(location.getLatitude(), location.getLongitude()));
        spawnRad.center(new LatLng(32, 58));
        /*Set The Radius To The Screen Width*/
        //todo fix
        //options.radius(Resources.getSystem().getDisplayMetrics().widthPixels/2*zoomconv[Math.round(zoom)]);
        spawnRad.radius(100);
        /*Set The Basic Options Of The Spawn Radius Circle*/
        spawnRad.fillColor(Color.TRANSPARENT);
        spawnRad.strokeColor(Color.LTGRAY);
        spawnRad.strokeWidth(5);
        /*Add The Circle To The Map*/
        getMap().addCircle(spawnRad);
    }
}