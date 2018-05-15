package green.tyler.gpstd;

import android.content.res.Resources;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
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
    private float zoom = 16f;   //Current Zoom Level
    private Circle innerCircle;
    private Circle outerCirlce;
    private Manager manager;
    private Handler movementHandler;

    /*
     * onStart
     * Runs When The Fragment Has Been Started
     * Acquires Semaphore And Starts Connection To API Client
     */
    @Override
    public void onStart() {
        super.onStart();
        /*Acquire The Semaphore To State Map Has Not Been Initialized*/
        try {
            mapSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        movementHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                manager.movement();
            }
        };
        manager = new Manager(movementHandler);
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
        Tower tower = new Fire_Tower();
        MarkerOptions options = tower.createOptions(getResources(), latLng);
        /*Add Marker To The Map*/
        tower.setMarker(getMap().addMarker(options));
        manager.addTower(tower);
    }

    /*
    * onMapLongClick
    * Runs When There Has Been A Long Press On The Map
    * Places A Marker On The Map Where The Click Has Occurred
    */
    @Override
    public void onMapLongClick(LatLng latLng) {
        /*Create A New Marker Options*/
        Enemy enemy = new Knight();
        MarkerOptions options = enemy.createOptions(getResources(), latLng);
        /*Add Marker To The Map*/
        enemy.setMarker(getMap().addMarker(options));
        manager.addEnemy(enemy);
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
        //getMap().setOnMyLocationChangeListener();
        /*Release The Semaphore*/
        mapSem.release();
    }

    /*
     * initCamera
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
        /*Position The Camera To The Location*/
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
        /*Set The Map Options To Desired Options*/
        getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getMap().setBuildingsEnabled(true);
        getMap().setMyLocationEnabled(true);
        getMap().setTrafficEnabled(false);
        getMap().getUiSettings().setZoomControlsEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);
        getMap().getUiSettings().setAllGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMapToolbarEnabled(false);
        /*Create An Inner Spawn Radius Cicrle*/
        CircleOptions spawnRad = new CircleOptions();
        /*Set The Center To The User*/
        spawnRad.center(new LatLng(location.getLatitude(), location.getLongitude()));
        /*Set The Radius To Half The Screen Width*/
        double innerSpawnRad = Resources.getSystem().getDisplayMetrics().widthPixels/2*metersPerPixel(location.getLatitude(), zoom);
        spawnRad.radius(innerSpawnRad);
        /*Set The Basic Options Of The Spawn Radius Circle*/
        spawnRad.fillColor(Color.TRANSPARENT);
        spawnRad.strokeColor(Color.LTGRAY);
        spawnRad.strokeWidth(5);
        /*Add The Circle To The Map*/
        innerCircle = getMap().addCircle(spawnRad);
        /*Create An Outer Spawn Radius Circle*/
        spawnRad = new CircleOptions();
        /*Set The Center To The User*/
        spawnRad.center(new LatLng(location.getLatitude(), location.getLongitude()));
        /*Set The Radius To Half The Screen Width X 2.5*/
        double outerSpawnRad = innerSpawnRad*2.5;
        spawnRad.radius(outerSpawnRad);
        /*Set The Basic Options Of The Spawn Radius Circle*/
        spawnRad.fillColor(Color.TRANSPARENT);
        spawnRad.strokeColor(Color.LTGRAY);
        spawnRad.strokeWidth(5);
        /*Add The Circle To The Map*/
        outerCirlce = getMap().addCircle(spawnRad);
    }

    /*
     * metersPerPixel
     * Runs During The Camera Initialization
     * Converts At The Given Latitude How Many Meters Are In One Pixel
     */
    public double metersPerPixel(double lat, double zoom) {
        double pixelsPerTile = 256 * ((double)Resources.getSystem().getDisplayMetrics().densityDpi / 160);
        double numTiles = Math.pow(2,zoom);
        double metersPerTile = Math.cos(Math.toRadians(lat)) * 40070000 / numTiles;
        return metersPerTile/pixelsPerTile;
    }
}