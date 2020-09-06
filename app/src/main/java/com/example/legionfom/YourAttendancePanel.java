package com.example.legionfom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.legionfom.helper.CustomUtility;
import com.example.legionfom.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class YourAttendancePanel extends AppCompatActivity {


    public static String presentLat = "", presentLon = "", presentAcc = "";
    private static final int MY_PERMISSIONS_REQUEST = 0;
    public LocationManager locationManager;
    public GPSLocationListener listener;
    public Location previousBestLocation = null;
    private static final int TWO_MINUTES = 1000 * 60 * 1;
    public static final String BROADCAST_ACTION = "gps_data";
    Intent intent;
    static Bitmap bitmap;
    SweetAlertDialog pDialog;
    JSONObject jsonObject;
    JSONArray jsonArray;
    Bundle IDbundle;

    String photoName = "";
    Boolean photoFlag = false,networkAvailable =false;

    Uri photoURI;
    static final int REQUEST_IMAGE_CAPTURE = 99;
    String currentPhotoPath = "";
    TextView location;

    ImageButton gpsTextBtn,cameraBtn;
    TextView gpsText, takeSelfieText;
    Button submitBtn;

    String imageString = "";
    String isActive = ""; // for checking need to submit in or out
    String userId = "", deviceDate = "";

    SharedPreferences sharedPreferences;
    TextView empName,empCode, empArea;

    ImageButton homeBtn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_attendance);

        empName = findViewById(R.id.tvName);
        empCode = findViewById(R.id.tvId);
        empArea = findViewById(R.id.tvArea);

        gpsTextBtn = findViewById(R.id.gpsBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        submitBtn = findViewById(R.id.btnSubmit);
        homeBtn = findViewById(R.id.homeBtn);

        location = findViewById(R.id.gpsAccuracy);
        takeSelfieText = findViewById(R.id.imageStatus);


        // user data retrieving from sharedpreference
        sharedPreferences = getSharedPreferences("fom_user",MODE_PRIVATE);
        empName.setText(sharedPreferences.getString("name",null));
        empCode.setText(sharedPreferences.getString("code",null));
        empArea.setText(sharedPreferences.getString("territoryName",null));

        userId = sharedPreferences.getString("id",null);
        deviceDate = CustomUtility.getDeviceDate();

        gpsTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presentAcc.equals(""))
                {
                    CustomUtility.showWarning(YourAttendancePanel.this, " ","Wait for some time getting gps value");
                }
                else
                {
                    location.setText(presentAcc);
                }
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourAttendancePanel.this, ActivityDockPanel.class));
                finish();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                photoName = deviceDate+"image.jpeg";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        CustomUtility.showAlert(YourAttendancePanel.this, ex.getMessage(), "Creating Image");
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.legoinfom.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkAvailable = CustomUtility.haveNetworkConnection(YourAttendancePanel.this);

                int f = checkfields();
                if(f==1)
                {
                    if (ContextCompat.checkSelfPermission(YourAttendancePanel.this, Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted
                        Log.e("DXXXXXXXXXX", "Not Granted");
                        CustomUtility.showAlert(YourAttendancePanel.this, "Permission not granted", "Permission");
                    } else {
                        SweetAlertDialog d = new SweetAlertDialog(YourAttendancePanel.this, SweetAlertDialog.WARNING_TYPE);
                        d.setTitleText("Confirm Submission?");
                        d.setConfirmText("Yes");
                        d.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                pDialog = new SweetAlertDialog(YourAttendancePanel.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Loading");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                upload();
                            }
                        });
                        d.setCancelText("No");
                        d.show();
                    }
                }
            }
        });

        // for getting gps value
        intent = new Intent(BROADCAST_ACTION);
        GPS_Start();

    }


    //after finishing camera intent whether the picture was save or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFlag = true;
            takeSelfieText.setText(R.string.image_done);
        }
    }

    private int checkfields()
    {
        if(!networkAvailable)
        {
            CustomUtility.showWarning(YourAttendancePanel.this, "Please turn on your internet connection","No Internet!");
            return -1;
        }
        else if(!photoFlag)
        {
            CustomUtility.showWarning(YourAttendancePanel.this,"Please take a selfie","Required fields");
            return -1;
        }
        else if(presentAcc.equals(""))
        {
            CustomUtility.showWarning(YourAttendancePanel.this,"Please wait for the gps","Required fields");
            return -1;
        }
        return 1;
    }


    private void upload()
    {

        Uri uri = Uri.fromFile(new File(currentPhotoPath));
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            pDialog.dismiss();
            String err = e.getMessage() + " May be storage full please uninstall then install the app again";
            CustomUtility.showAlert(this, e.getMessage(), "Problem Creating Bitmap at Submit");
            return;
        }
        imageString = CustomUtility.imageToString(bitmap);
        String upLoadServerUri = "https://sec.imslpro.com/api/android/insert_attendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upLoadServerUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.e("response",response);
                        try {
                            jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if(code.equals("true"))
                            {
                                code = "Successful";
                                new SweetAlertDialog(YourAttendancePanel.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Successful")
                                        .setContentText("")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                startActivity(new Intent(YourAttendancePanel.this, ActivityDockPanel.class));
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                            else
                            {
                                code = "Failed";
                                CustomUtility.showError(YourAttendancePanel.this,message,code);
                                //CustomUtility.showError(AttendanceActivity.this,"You allready submitted in",code);
                            }


                        } catch (JSONException e) {
                            CustomUtility.showError(YourAttendancePanel.this, e.getMessage(), "Failed");
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("response","onerrorResponse");
                CustomUtility.showError(YourAttendancePanel.this, "Network slow, try again", "Failed");

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UserId",userId);
                params.put("InTimeLat",presentLat);
                params.put("InTimeLon",presentLon);
                params.put("InTimeAccuracy",presentAcc);
                params.put("InTimePictureName",photoName);
                params.put("InPictureData",imageString);
                params.put("DeviceDateTime",deviceDate);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(YourAttendancePanel.this).addToRequestQue(stringRequest);
    }

    private File createImageFile() throws IOException {

        File storageDir = getExternalFilesDir("LegionFOM/Photos");

        File image = new File(storageDir.getAbsolutePath() + File.separator + photoName);
        try {
            image.createNewFile();
        } catch (IOException e) {
            CustomUtility.showAlert(this, "Image Creation Failed. Please contact administrator", "Error");
        }
        currentPhotoPath = image.getAbsolutePath();
        Log.e("image path",currentPhotoPath);
        return image;
    }

    private void GPS_Start() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new GPSLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        } catch (Exception ex) {

        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public class GPSLocationListener implements LocationListener {
        public void onLocationChanged(final Location loc) {
            Log.i("**********", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {


                loc.getAccuracy();
                location.setText(" " + loc.getAccuracy());

                presentLat = String.valueOf(loc.getLatitude());
                presentLon = String.valueOf(loc.getLongitude());
                presentAcc = String.valueOf(loc.getAccuracy());


//                Toast.makeText(context, "Latitude" + loc.getLatitude() + "\nLongitude" + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(getApplicationContext(), "Status Changed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(YourAttendancePanel.this, ActivityAttendancePanel.class));
        finish();
    }

}
