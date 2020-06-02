package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.model.Addresses;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;

import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddnewAddressActivity extends AppCompatActivity {

    LocationManager locationManager;

    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private int permissionCode = 1;
    private String city = null, country = null, address = null, postalCode = null, state = null;
    private ImageButton locationId;
    Location location;
    private EditText countryEt, stateEt, cityEt, postalEt, addressEt;
    private AlertDialog alertDialog;
    IPetManiaAPI mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew_address);
        mServices = Common.getAPI();

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.getLocation)
                .build();

        countryEt = findViewById(R.id.country_et);
        stateEt = findViewById(R.id.state_et);
        cityEt = findViewById(R.id.city_et);
        postalEt = findViewById(R.id.postal_et);
        addressEt = findViewById(R.id.address_et);

        locationId = findViewById(R.id.location_id);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.show();
                if (location == null) {
                    fetchLocation();
                } else {
                    setLocation();
                }
            }
        });
    }


    private void fetchLocation() {

        if (ContextCompat.checkSelfPermission(AddnewAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(AddnewAddressActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddnewAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this).setTitle("Permission Needed")
                        .setMessage("You have to give the permission to access the feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(AddnewAddressActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionCode);
                                setLocation();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            } else {
                ActivityCompat.requestPermissions(AddnewAddressActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionCode);
            }
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                alertDialog.dismiss();
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            } else {
                alertDialog.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddnewAddressActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("GPS is settings");
                // Setting Dialog Message
                alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
                // On pressing Settings button
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

// on pressing cancel button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
// Showing Alert Message
                alertDialog.show();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == permissionCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                setLocation();

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    private void setLocation() {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() >= 0) {
                alertDialog.dismiss();
                address = addresses.get(0).getAddressLine(0);
                addressEt.setText(address);
                city = addresses.get(0).getLocality();
                cityEt.setText(city);
                state = addresses.get(0).getAdminArea();
                stateEt.setText(state);
                country = addresses.get(0).getCountryName();
                countryEt.setText(country);
                postalCode = addresses.get(0).getPostalCode();
                postalEt.setText(postalCode);
                String knownName = addresses.get(0).getFeatureName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitLocation(View view) {

        AlertDialog alertDialog1 = new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.updateLocation)
                .build();
        if (countryEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Country", Toast.LENGTH_SHORT).show();
        } else if (stateEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter state", Toast.LENGTH_SHORT).show();
        } else if (cityEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter city ", Toast.LENGTH_SHORT).show();
        } else if (postalEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter postal code", Toast.LENGTH_SHORT).show();
        } else if (addressEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog1.show();
            mServices.updateLocation(Common.currentUser.getUser_id(), countryEt.getText().toString(),
                    stateEt.getText().toString(), cityEt.getText().toString(), postalEt.getText().toString(),
                    addressEt.getText().toString()).enqueue(new Callback<Addresses>() {
                @Override
                public void onResponse(Call<Addresses> call, Response<Addresses> response) {
                    alertDialog1.dismiss();
                    Addresses addresses = response.body();
                    if (TextUtils.isEmpty(addresses.getError_msg())) {
                        Toast.makeText(AddnewAddressActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddnewAddressActivity.this,AddressActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AddnewAddressActivity.this, "No Updated " + addresses.getError_msg(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Addresses> call, Throwable t) {
                    alertDialog1.dismiss();
                    Toast.makeText(AddnewAddressActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
