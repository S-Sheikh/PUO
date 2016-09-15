package za.ac.cut.puo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity {
    Button btn_login;
    EditText edt_login_email, edt_login_password, etResetEmail;
    TextInputLayout email_txt_input_login, pass_txt_input_login;
    SpotsDialog progressDialog;
    Toolbar login_toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialiseViews();
        errorMsg();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login_register_option:
                startActivity(new Intent(Login.this, Register.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnLogin(View v) {
        String etEmail = edt_login_email.getText().toString().trim();
        String etPassword = edt_login_password.getText().toString().trim();
        if (etEmail.isEmpty() && etPassword.isEmpty()) {
            email_txt_input_login.setError(getString(R.string.txt_input_layout));
            pass_txt_input_login.setError(getString(R.string.txt_input_layout));
        } else {
            if (connectionAvailable()) {
                progressDialog = new SpotsDialog(Login.this, R.style.Custom);
                progressDialog.show();
                Backendless.UserService.login(etEmail, etPassword, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        BackendlessUser user = Backendless.UserService.CurrentUser();
                        if (user != null) {
                            if (backendlessUser.getProperty("isUpdated").equals(false)) {
                                Intent intent = new Intent(Login.this, Update.class);
                                intent.putExtra("user", backendlessUser.getEmail());
                                intent.putExtra("password", backendlessUser.getPassword());
                                intent.putExtra("name", backendlessUser.getProperty("name").toString().trim());
                                intent.putExtra("surname", backendlessUser.getProperty("surname").toString().trim());
                                intent.putExtra("objectId", backendlessUser.getObjectId());
                                startActivity(intent);
                            } else {
                                backendlessUser.setProperty("isUpdated", true);
                                Intent intent1 = new Intent(Login.this, HomeMenu.class);
                                intent1.putExtra("objectId", backendlessUser.getObjectId());
                                intent1.putExtra("password", backendlessUser.getPassword());
                                intent1.putExtra("user", user.getEmail());
                                intent1.putExtra("name", backendlessUser.getProperty("name").toString().trim());
                                intent1.putExtra("surname", backendlessUser.getProperty("surname").toString().trim());
                                intent1.putExtra("role", backendlessUser.getProperty("role").toString().trim());
                                intent1.putExtra("cell", backendlessUser.getProperty("cell").toString().trim());
                                intent1.putExtra("isUpdated", backendlessUser.getProperty("isUpdated").toString().trim());
                                startActivity(intent1);
                            }
                        }

                        Toast.makeText(Login.this, backendlessUser.getEmail() + " successfully logged in!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(Login.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }, true);
            } else {
                Toast.makeText(Login.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btnResetPassword(View v) {
        if (connectionAvailable()) {
            LayoutInflater inflater = getLayoutInflater();
            final View view = inflater.inflate(R.layout.login_reset_password, null);
            etResetEmail = (EditText) view.findViewById(R.id.etResetEmail);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.btn_reset_password));
            dialog.setView(view);
            dialog.setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Login.this.progressDialog = new SpotsDialog(Login.this, R.style.Custom);
                    Login.this.progressDialog.setTitle(getString(R.string.please_wait));
                    Login.this.progressDialog.show();
                    Backendless.UserService.restorePassword(etResetEmail.getText().toString().trim(), new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void aVoid) {
                            Toast.makeText(Login.this, "Reset link has been sent to " + etResetEmail.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                            Login.this.progressDialog.dismiss();
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(Login.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                            Login.this.progressDialog.dismiss();
                        }
                    });
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        } else {
            Toast.makeText(Login.this, "Please connect to the internet!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initialiseViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_login_email = (EditText) findViewById(R.id.edt_email);
        edt_login_password = (EditText) findViewById(R.id.edt_password);
        email_txt_input_login = (TextInputLayout) findViewById(R.id.email_txt_input_login);
        pass_txt_input_login = (TextInputLayout) findViewById(R.id.pass_txt_input_login);
        login_toolBar = (Toolbar) findViewById(R.id.login_toolBar);
        setSupportActionBar(login_toolBar);
        login_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
    }

    private void errorMsg() {
        edt_login_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, edt_login_email);
            }
        });
        edt_login_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_login_email);
                }
            }
        });
        edt_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, edt_login_password);
            }
        });
        edt_login_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_login_password);
                }
            }
        });
    }

    private void validateEditText(Editable s, View v) {
        if (!TextUtils.isEmpty(s)) {
            if (v.getId() == R.id.edt_email)
                email_txt_input_login.setError(null);
            else if (v.getId() == R.id.edt_password)
                pass_txt_input_login.setError(null);
        } else {
            if (v.getId() == R.id.edt_email) {
                email_txt_input_login.setErrorEnabled(true);
                email_txt_input_login.setError(getString(R.string.txt_input_layout));
            } else if (v.getId() == R.id.edt_password) {
                pass_txt_input_login.setErrorEnabled(true);
                pass_txt_input_login.setError(getString(R.string.txt_input_layout));
            }
        }
    }

    private boolean connectionAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {//if true,connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = true;//connected to using wifi
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                connected = true;//connected using mobile data
            }
        } else {
            connected = false;//no internet connection
        }
        return connected;
    }
}