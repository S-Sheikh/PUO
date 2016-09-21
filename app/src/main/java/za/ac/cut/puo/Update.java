package za.ac.cut.puo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dmax.dialog.SpotsDialog;

public class Update extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    Spinner spRoles,spLocation;
    EditText etName, etSurname, etEmail, etCellPhone, etPassword, etRePassword, etNewPass, etRePass;
    TextView tvAddPic;
    ImageView ivProfilePic;
    TextInputLayout nameInput, surnameInput, emailInput, passwordInput, rePasswordInput, cellInput;
    SpotsDialog progressDialog;
    Toolbar update_toolBar;
    Button btn_update_submit, btnResetPass;
    Menu update_edit__menu_item;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        initViews();
        errorMsg();
        update_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            if (user.getProperty("isUpdated").equals(false)) {
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                etEmail.setText(user.getEmail());
                passwordInput.setVisibility(View.GONE);
                rePasswordInput.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                etRePassword.setVisibility(View.GONE);
                btnResetPass.setVisibility(View.GONE);
                //readImage();
                PUOHelper.getImageOnline(new DownloadTask(ivProfilePic));
            } else {
                etName.setEnabled(false);
                etSurname.setEnabled(false);
                etEmail.setEnabled(false);
                etPassword.setEnabled(false);
                ivProfilePic.setEnabled(false);
                btnResetPass.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
                passwordInput.setVisibility(View.GONE);
                rePasswordInput.setVisibility(View.GONE);
                etRePassword.setVisibility(View.GONE);
                etCellPhone.setEnabled(false);
                spRoles.setEnabled(false);
                spLocation.setEnabled(false);
                btn_update_submit.setVisibility(View.GONE);
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                //PUOHelper.getImageOnline(new DownloadTask(ivProfilePic));
                String imageUri = "https://api.backendless.com/D200A885-7EED-CB51-FFAC-228F87E55D00/v1/files/UserProfilePics/" + user.getEmail() + "_.png";
                Picasso.with(Update.this).load(imageUri).into(ivProfilePic);
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
                etRePassword.setText(user.getPassword());
                etCellPhone.setText(user.getProperty("cell").toString().trim());
                //TODO: DO validation when spinner is sElected or not!!
                //spRoles.setSelection(getIndex(spRoles,user.getProperty("roles").toString().trim()));
                //spLocation.setSelection(getIndex(spLocation,user.getProperty("location").toString().trim()));
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

    public void writeImageToFile(Bitmap bitmap) throws IOException {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        String filename = user.getEmail();
        File file = new File(path, filename + ".png"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = new FileOutputStream(file);

        //Bitmap pictureBitmap = getImageBitmap(myurl); // obtaining the Bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a PNG with 100% compression rate
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream
        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
    }


    public void readImage() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        String filename = user.getEmail() + ".png";
        String filepath = Environment.getExternalStorageDirectory().toString() + "/" + filename;
        File imagefile = new File(filepath);
        if (imagefile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            ivProfilePic.setImageBitmap(bitmap);
        } else {
            ivProfilePic.setImageResource(R.drawable.logo_puo);
        }
    }


    public void initViews(){
        spRoles = (Spinner)findViewById(R.id.spRoles);
        spLocation = (Spinner)findViewById(R.id.spLocation);
        etName = (EditText)findViewById(R.id.etName);
        etSurname = (EditText)findViewById(R.id.etSurname);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etCellPhone = (EditText)findViewById(R.id.etCellPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText) findViewById(R.id.etRePassword);
        tvAddPic = (TextView)findViewById(R.id.tvAddPic);
        ivProfilePic = (ImageView)findViewById(R.id.ivProfilePic);
        nameInput = (TextInputLayout)findViewById(R.id.name_input);
        surnameInput = (TextInputLayout)findViewById(R.id.surname_input);
        emailInput = (TextInputLayout)findViewById(R.id.email_input);
        passwordInput = (TextInputLayout)findViewById(R.id.password_input);
        rePasswordInput = (TextInputLayout) findViewById(R.id.repassword_input);
        cellInput = (TextInputLayout)findViewById(R.id.cell_input);
        update_toolBar = (Toolbar)findViewById(R.id.update_toolBar);
        btn_update_submit = (Button) findViewById(R.id.btn_update_submit);
        btnResetPass = (Button) findViewById(R.id.btnResetPass);
        update_edit__menu_item = (Menu) findViewById(R.id.update_edit__menu_item);
        setSupportActionBar(update_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void AddImage() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType("image/*");
        if (choosePhotoIntent.resolveActivity(Update.this.getPackageManager()) != null) {
            choosePhotoIntent.putExtra("imageUri", choosePhotoIntent.getData());
            startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
        }
    }
    private boolean connectionAvailable(){
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){//if true,connected to the internet
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                connected = true;//connected to using wifi
            }else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                connected = true;//connected using mobile data
            }
        }else{
            connected = false;//no internet connection
        }
        return connected;
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
        if (connectionAvailable()) {
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
                    user.setProperty("cell", etCellPhone.getText().toString().trim());
                    user.setProperty("role", spRoles.getSelectedItem().toString().trim());
                    user.setProperty("location", spLocation.getSelectedItem().toString().trim());
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
                                        try {
                                            writeImageToFile(bitmap);
                                            readImage();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
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
                            BackendlessUser user = Backendless.UserService.CurrentUser();
                            Intent intent = new Intent(Update.this, HomeMenu.class);
                            intent.putExtra("user", user.getEmail());
                            intent.putExtra("objectId", user.getObjectId());
                            intent.putExtra("name", user.getProperty("name").toString().trim());
                            intent.putExtra("surname", user.getProperty("surname").toString().trim());
                            intent.putExtra("role", user.getProperty("role").toString().trim());
                            intent.putExtra("location", user.getProperty("location").toString().trim());
                            intent.putExtra("cell", user.getProperty("cell").toString().trim());
                            intent.putExtra("isUpdated", user.getProperty("isUpdated").toString().trim());
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
        passwordInput.setVisibility(View.GONE);
        rePasswordInput.setVisibility(View.GONE);
        etRePassword.setVisibility(View.GONE);
        etCellPhone.setEnabled(true);
        spRoles.setEnabled(false);
        //spRoles.setVisibility(View.GONE);
        spLocation.setEnabled(true);
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
                    user.setProperty("objectId", getIntent().getStringExtra("objectId"));
                    user.setPassword(etNewPass.getText().toString().trim());
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            Toast.makeText(Update.this, "Password changed successfully!" + " \n" + "\t" +
                                    "Please login to apply changes :)", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(Update.this, Login.class));
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
}

