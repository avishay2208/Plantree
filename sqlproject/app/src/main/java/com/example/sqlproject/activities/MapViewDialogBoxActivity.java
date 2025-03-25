package com.example.sqlproject.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Locations;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.Objects;


public class MapViewDialogBoxActivity extends Dialog implements View.OnClickListener {

    private final Activity context;
    private Button btnDialogClose;

    public MapViewDialogBoxActivity(@NonNull Activity context) {
        super(context);
        this.context = context;

        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_dialog_box);
        initButtons();
        initMap();
    }

    void initMap() {
        Context ctx = context;
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(15.0);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET
        });
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(ctx, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        GeoPoint point = new GeoPoint(Locations.chosenLocation.getLatitude(), Locations.chosenLocation.getLongitude());

        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);

        map.getController().setCenter(point);
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
            ActivityCompat.requestPermissions(
                    context,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    void initButtons() {
        btnDialogClose = findViewById(R.id.btnClose);
        btnDialogClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btnDialogClose.isPressed())
            dismiss();
    }
}