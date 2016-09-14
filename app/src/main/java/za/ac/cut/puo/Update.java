package za.ac.cut.puo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import dmax.dialog.SpotsDialog;

public class Update extends AppCompatActivity {
    Spinner spRoles,spLocation;
    EditText etName, etSurname, etEmail, etCellPhone, etPassword, etRePassword;
    TextView tvAddPic;
    ImageView ivProfilePic;
    TextInputLayout nameInput, surnameInput, emailInput, passwordInput, rePasswordInput, cellInput;
    SpotsDialog progressDialog;
    Toolbar update_toolBar;
    Button btn_update_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        initViews();
        errorMsg();
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            if (user.getProperty("isUpdated").equals(false)) {
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
                etRePassword.setText(user.getPassword());
            } else {
                etName.setText(user.getProperty("name").toString().trim());
                etSurname.setText(user.getProperty("surname").toString().trim());
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
                etRePassword.setText(user.getPassword());
                etCellPhone.setText(user.getProperty("cell").toString().trim());
                //spRoles.setSelection(getIndex(spRoles,user.getProperty("roles").toString().trim()));
                //spLocation.setSelection(getIndex(spLocation,user.getProperty("location").toString().trim()));
            }
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
        setSupportActionBar(update_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
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

    //returns spinner index
    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
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

    public void btnUpdateSubmit(View v) {
        if (connectionAvailable()) {
            if (etName.getText().toString().trim().isEmpty() && etSurname.getText().toString().trim().isEmpty() &&
                    etEmail.getText().toString().trim().isEmpty() &&
                    etPassword.getText().toString().trim().isEmpty() && etRePassword.getText().toString().trim().isEmpty()) {
                nameInput.setError(getString(R.string.txt_input_layout));
                surnameInput.setError(getString(R.string.txt_input_layout));
                emailInput.setError(getString(R.string.txt_input_layout));
                passwordInput.setError(getString(R.string.txt_input_layout));
                rePasswordInput.setError(getString(R.string.txt_input_layout));
                cellInput.setError(getString(R.string.txt_input_layout));
            } else {
                if (etPassword.getText().toString().trim().equals(etRePassword.getText().toString().trim())) {
                    progressDialog = new SpotsDialog(Update.this, R.style.Custom);
                    progressDialog.show();
                    BackendlessUser user = Backendless.UserService.CurrentUser();
                    user.setProperty("objectId", getIntent().getStringExtra("objectId"));
                    user.setPassword(etPassword.getText().toString().trim());
                    user.setProperty("name", etName.getText().toString().trim());
                    user.setProperty("surname", etSurname.getText().toString().trim());
                    user.setProperty("cell", etCellPhone.getText().toString().trim());
                    user.setProperty("role", spRoles.getSelectedItem().toString().trim());
                    user.setProperty("location", spLocation.getSelectedItem().toString().trim());
                    user.setProperty("isUpdated", true);
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            Toast.makeText(Update.this, etName.getText().toString().trim() + " " +
                                    etSurname.getText().toString().trim() +
                                    " successfully updated!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(Update.this, HomeMenu.class));
                            BackendlessUser user = Backendless.UserService.CurrentUser();
                            Intent intent = new Intent(Update.this, HomeMenu.class);
                            intent.putExtra("user", user.getEmail());
                            intent.putExtra("objectId", user.getObjectId());
                            intent.putExtra("password", user.getPassword());
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
                } else {
                    Toast.makeText(Update.this, "Please make sure passwords match!", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(Update.this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
    }


}

