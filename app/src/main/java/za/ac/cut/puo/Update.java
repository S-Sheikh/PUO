package za.ac.cut.puo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED;

public class Update extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final String TODO = "";
    Spinner spRoles;
    EditText etName, etSurname, etEmail, etCellPhone, etPassword, etRePassword, etNewPass, etRePass, etUsername, etLocation;
    TextView tvAddPic;
    ImageView ivProfilePic;
    TextInputLayout nameInput, surnameInput, emailInput, passwordInput, rePasswordInput, cellInput, usernameInput, Location_input;
    SpotsDialog progressDialog;
    Toolbar update_toolBar;
    Button btn_update_submit, btnResetPass, button01;
    Menu update_edit__menu_item;
    Bitmap bitmap = null;
    LocationManager locationManager;
    LocationListener locationListener;
    Double Latitude = 0.0;
    Double Longitude = 0.0;
    Geocoder geocoder;
    List<Address> addresses;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        initViews();
        errorMsg();
        update_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
        BackendlessUser user = Backendless.UserService.CurrentUser();
        getLocation();
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    etLocation.setText(Location());
                } catch (IOException e) {
                    Toast.makeText(Update.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (user != null) {
            if (user.getProperty("isUpdated").equals(false)) {
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                etUsername.setText(user.getProperty("username").toString().trim());
                etEmail.setText(user.getEmail());
                passwordInput.setVisibility(View.GONE);
                rePasswordInput.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etRePassword.setVisibility(View.GONE);
                btnResetPass.setVisibility(View.GONE);
                PUOHelper.readImage((ivProfilePic));
            } else {
                etName.setEnabled(false);
                etSurname.setEnabled(false);
                etUsername.setEnabled(false);
                etEmail.setEnabled(false);
                etPassword.setEnabled(false);
                etLocation.setEnabled(false);
                ivProfilePic.setEnabled(false);
                btnResetPass.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                passwordInput.setVisibility(View.GONE);
                rePasswordInput.setVisibility(View.GONE);
                etRePassword.setVisibility(View.GONE);
                etCellPhone.setEnabled(false);
                spRoles.setEnabled(false);
                btn_update_submit.setVisibility(View.GONE);
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                etUsername.setText(user.getProperty("username").toString().trim());
                String arr[] = new String[]{"Collector", "Specialist", "Administrator"};
                for (int i = 0; i < arr.length; i++) {
                    if (user.getProperty("role").toString() == arr[i]) {
                        spRoles.setSelection(i);
                    }
                }
                PUOHelper.readImage((ivProfilePic));
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
                etRePassword.setText(user.getPassword());
                etCellPhone.setText(user.getProperty("cell").toString().trim());
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_edit__menu_item:
                btnEdit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(Update.this.getContentResolver(), uri);
                    ivProfilePic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(Update.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void initViews() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
//                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
        spRoles = (Spinner) findViewById(R.id.spRoles);
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etCellPhone = (EditText) findViewById(R.id.etCellPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText) findViewById(R.id.etRePassword);
        tvAddPic = (TextView) findViewById(R.id.tvAddPic);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        nameInput = (TextInputLayout) findViewById(R.id.name_input);
        surnameInput = (TextInputLayout) findViewById(R.id.surname_input);
        usernameInput = (TextInputLayout) findViewById(R.id.username_input);
        emailInput = (TextInputLayout) findViewById(R.id.email_input);
        passwordInput = (TextInputLayout) findViewById(R.id.password_input);
        rePasswordInput = (TextInputLayout) findViewById(R.id.repassword_input);
        cellInput = (TextInputLayout) findViewById(R.id.cell_input);
        update_toolBar = (Toolbar) findViewById(R.id.update_toolBar);
        btn_update_submit = (Button) findViewById(R.id.btn_update_submit);
        btnResetPass = (Button) findViewById(R.id.btnResetPass);
        update_edit__menu_item = (Menu) findViewById(R.id.update_edit__menu_item);
        etLocation = (EditText) findViewById(R.id.etLocation);
        Location_input = (TextInputLayout) findViewById(R.id.Location_input);
        button01 = (Button) findViewById(R.id.button01);
        setSupportActionBar(update_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        }


    }

    public void AddImage() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType("image/*");
        if (choosePhotoIntent.resolveActivity(Update.this.getPackageManager()) != null) {
            choosePhotoIntent.putExtra("imageUri", choosePhotoIntent.getData());
            startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
        }
    }

    private void errorMsg() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etEmail);
            }
        });
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etEmail);
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etPassword);
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etPassword);
                }
            }
        });
        etRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etRePassword);
            }
        });
        etRePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etRePassword);
                }
            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etName);
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etName);
                }
            }
        });
        etSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etSurname);
            }
        });
        etSurname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etSurname);
                }
            }
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etUsername);
            }
        });
        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etUsername);
                }
            }
        });

        etCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, etCellPhone);
            }
        });
        etCellPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), etCellPhone);
                }
            }
        });
    }

    private void validateEditText(Editable s, View v) {
        if (!TextUtils.isEmpty(s)) {
            if (v.getId() == R.id.etEmail)
                emailInput.setError(null);
            else if (v.getId() == R.id.etPassword)
                passwordInput.setError(null);
            else if (v.getId() == R.id.etRePassword)
                rePasswordInput.setError(null);
            else if (v.getId() == R.id.etName)
                nameInput.setError(null);
            else if (v.getId() == R.id.etSurname)
                surnameInput.setError(null);
            else if (v.getId() == R.id.etCellPhone)
                cellInput.setError(null);
            else if (v.getId() == R.id.etUsername)
                usernameInput.setError(null);
        } else {
            if (v.getId() == R.id.etEmail)
                emailInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etPassword)
                passwordInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etRePassword)
                rePasswordInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etName)
                nameInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etSurname)
                surnameInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etCellPhone)
                cellInput.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.etUsername)
                usernameInput.setError(getString(R.string.txt_input_layout));
        }
    }

    public void ivProfilePickClicked(View v) throws IOException {
        switch (v.getId()) {
            case R.id.ivProfilePic: {
                AddImage();
            }
        }
    }

    public void btnUpdateSubmit(View v) {
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        String filename = user.getEmail() + "_.png";
        if (PUOHelper.connectionAvailable(this)) {
            if (etName.getText().toString().trim().isEmpty() || etSurname.getText().toString().trim().isEmpty() ||
                    etEmail.getText().toString().trim().isEmpty()
                    || etCellPhone.getText().toString().trim().isEmpty()) {
                Toast.makeText(Update.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = new SpotsDialog(Update.this, R.style.Custom);
                progressDialog.show();
                user.setProperty("objectId", getIntent().getStringExtra("objectId"));
                user.setProperty("name", etName.getText().toString().trim());
                user.setProperty("surname", etSurname.getText().toString().trim());
                user.setProperty("username", etUsername.getText().toString().trim());
                user.setProperty("cell", etCellPhone.getText().toString().trim());
                user.setProperty("role", spRoles.getSelectedItem().toString().trim());
                user.setProperty("isUpdated", true);
                final UserProfilePictures userProfilePictures = new UserProfilePictures();
                userProfilePictures.setUserMail(user.getEmail());
                userProfilePictures.setImageLocation("UserProfilePics/" + filename);
                Backendless.Files.Android.upload(bitmap,
                        Bitmap.CompressFormat.PNG,
                        100,
                        filename,
                        "UserProfilePics",
                        true,
                        new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(BackendlessFile backendlessFile) {
                                Backendless.Persistence.save(userProfilePictures, new AsyncCallback<UserProfilePictures>() {
                                    @Override
                                    public void handleResponse(UserProfilePictures userProfilePictures) {
                                        PUOHelper.readImage(ivProfilePic);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(Update.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        Toast.makeText(Update.this, etName.getText().toString().trim() + " " +
                                etSurname.getText().toString().trim() +
                                " successfully updated :)", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(Update.this, HomeMenu.class));
                        Update.this.finish();

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(Update.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        } else {
            Toast.makeText(Update.this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnEdit() {
        etName.setEnabled(true);
        etSurname.setEnabled(true);
        etEmail.setEnabled(true);
        etUsername.setEnabled(true);
        passwordInput.setVisibility(View.GONE);
        rePasswordInput.setVisibility(View.GONE);
        etRePassword.setVisibility(View.GONE);
        etCellPhone.setEnabled(true);
        etLocation.setEnabled(true);
        spRoles.setEnabled(false);
        //spRoles.setVisibility(View.GONE);
        //update_edit__menu_item.setGroupVisible(0,false);
        etPassword.setHint(getString(R.string.change_password));
        //etPassword.setVisibility(View.VISIBLE);
        //etPassword.setEnabled(true);
        btnResetPass.setVisibility(View.VISIBLE);
        btn_update_submit.setVisibility(View.VISIBLE);
        ivProfilePic.setEnabled(true);

    }

    public void ChangePassword(View v) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.change_password, null);
        etNewPass = (EditText) view.findViewById(R.id.etNewPass);
        etRePass = (EditText) view.findViewById(R.id.etRePass);
        progressDialog = new SpotsDialog(Update.this, R.style.Custom);
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("Change Password");
        dlg.setView(view);
        dlg.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BackendlessUser user = Backendless.UserService.CurrentUser();
                progressDialog.show();
                if (etNewPass.getText().toString().trim().equals(etRePass.getText().toString().trim())) {
                    //user.setProperty("objectId", getIntent().getStringExtra("objectId"));
                    user.setPassword(etNewPass.getText().toString().trim());
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            Toast.makeText(Update.this, "Password changed successfully! \n \t " +
                                    "Please login to apply changes :)", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
//                            startActivity(new Intent(Update.this, Login.class));
                            Update.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(Update.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(Update.this, "Please make sure passwords match!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.show();
    }

    public String Location() throws IOException {
        String location = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        location = address + "," + city + "," + state + "," + country + "," + postalCode;
        return location;
    }

    protected void onStart() {
        super.onStart();
        // Connect the client.
//        mGoogleApiClient.connect();
    }

    protected void onStop() {
        // Disconnecting the client invalidates it.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onConnected(Bundle dataBundle) {
        // Get last known recent location.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            // Print current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        // Begin polling for new location updates.
        startLocationUpdates();
    }


    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }
}


