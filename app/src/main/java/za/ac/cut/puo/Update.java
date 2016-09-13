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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    EditText etName, etSurname, etUsername, etEmail, etCellPhone, etPassword, etRePassword;
    TextView tvAddPic;
    ImageView ivProfilePic;
    TextInputLayout nameInput, surnameInput, userNameInput, emailInput, passwordInput, rePasswordInput, cellInput;
    SpotsDialog progressDialog;
    Toolbar update_toolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_update_profile);
        initViews();
        etEmail.setText(getIntent().getStringExtra("user"));
        etName.setText(getIntent().getStringExtra("name"));
        etSurname.setText(getIntent().getStringExtra("surname"));
        etUsername.setText(getIntent().getStringExtra("username"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_update_profile_save:
                //update profile
                if(connectionAvailable()){
                    if(etName.getText().toString().trim().isEmpty()|| etSurname.getText().toString().trim().isEmpty() ||
                            etUsername.getText().toString().trim().isEmpty() || etEmail.getText().toString().trim().isEmpty() ||
                            etPassword.getText().toString().trim().isEmpty()){
                        nameInput.setError(getString(R.string.txt_input_layout));
                        surnameInput.setError(getString(R.string.txt_input_layout));
                        userNameInput.setError(getString(R.string.txt_input_layout));
                        emailInput.setError(getString(R.string.txt_input_layout));
                        passwordInput.setError(getString(R.string.txt_input_layout));
                        cellInput.setError(getString(R.string.txt_input_layout));
                    }else{
                        if (etPassword.getText().toString().trim().equals(etRePassword.getText().toString().trim())) {
                            progressDialog = new SpotsDialog(Update.this, R.style.Custom);
                            progressDialog.show();
                            BackendlessUser user = new BackendlessUser();
                            user.setProperty("objectId", getIntent().getStringExtra("objectId"));
                            user.setPassword(etPassword.getText().toString().trim());
                            user.setProperty("cell", etCellPhone.getText().toString().trim());
                            user.setProperty("role", spRoles.getSelectedItem().toString().trim());
                            user.setProperty("location", spLocation.getSelectedItem().toString().trim());
                            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    Toast.makeText(Update.this, etName.getText().toString().trim() + " " +
                                            etSurname.getText().toString().trim() +
                                            " successfully saved!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Update.this, HomeMenu.class);
                                    intent.putExtra("user", backendlessUser.getEmail());
                                    intent.putExtra("name", backendlessUser.getProperty("name").toString().trim());
                                    intent.putExtra("surname", backendlessUser.getProperty("surname").toString().trim());
                                    startActivity(intent);
                                    progressDialog.dismiss();
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
                }else {
                    Toast.makeText(Update.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews(){
        spRoles = (Spinner)findViewById(R.id.spRoles);
        spLocation = (Spinner)findViewById(R.id.spLocation);
        etName = (EditText)findViewById(R.id.etName);
        etSurname = (EditText)findViewById(R.id.etSurname);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etCellPhone = (EditText)findViewById(R.id.etCellPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText) findViewById(R.id.etRePassword);
        tvAddPic = (TextView)findViewById(R.id.tvAddPic);
        ivProfilePic = (ImageView)findViewById(R.id.ivProfilePic);
        nameInput = (TextInputLayout)findViewById(R.id.name_input);
        surnameInput = (TextInputLayout)findViewById(R.id.surname_input);
        userNameInput = (TextInputLayout)findViewById(R.id.username_input);
        emailInput = (TextInputLayout)findViewById(R.id.email_input);
        passwordInput = (TextInputLayout)findViewById(R.id.password_input);
        rePasswordInput = (TextInputLayout) findViewById(R.id.repassword_txt_input);
        cellInput = (TextInputLayout)findViewById(R.id.cell_input);
        update_toolBar = (Toolbar)findViewById(R.id.update_toolBar);
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


}

