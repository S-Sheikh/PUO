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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import dmax.dialog.SpotsDialog;

public class Register extends AppCompatActivity {
    Button btn_submit;
    EditText edt_register__name, edt_register__surname, edt_register__email, edt_register__password, edt_register__rePassword;
    TextInputLayout txt_input, surname_txt_input, email_txt_input, pass_txt_input, repass_txt_input;
    SpotsDialog progressDialog;
    Toolbar register_toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initialiseViews();
        errorMsg();
    }

    public void btnSubmit(View V){
        if(edt_register__name.getText().toString().trim().isEmpty() && edt_register__surname.getText().toString().trim().isEmpty()
                && edt_register__email.getText().toString().trim().isEmpty()
                && edt_register__password.getText().toString().trim().isEmpty() && edt_register__rePassword.getText().toString().trim().isEmpty()){
            txt_input.setError(getString(R.string.txt_input_layout));
            surname_txt_input.setError(getString(R.string.txt_input_layout));
            email_txt_input.setError(getString(R.string.txt_input_layout));
            pass_txt_input.setError(getString(R.string.txt_input_layout));
            repass_txt_input.setError(getString(R.string.txt_input_layout));
        }
        else if(!(edt_register__password.getText().toString().trim().equals(edt_register__rePassword.getText().toString().trim()))){
            Toast.makeText(Register.this, "Please make sure passwords match", Toast.LENGTH_SHORT).show();
        }else{
            if(connectionAvailable()){
                BackendlessUser user = new BackendlessUser();
                user.setProperty("email",edt_register__email.getText().toString().trim());
                user.setProperty("name",edt_register__name.getText().toString().trim());
                user.setProperty("surname",edt_register__surname.getText().toString().trim());
                user.setPassword(edt_register__password.getText().toString().trim());
                progressDialog = new SpotsDialog(Register.this, R.style.Custom);
                progressDialog.show();
                Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        Toast.makeText(Register.this, "Confirmation link has been sent to you!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, Login.class);
                        intent.putExtra("user",edt_register__email.getText().toString().trim());
                        intent.putExtra("name",edt_register__name.getText().toString().trim());
                        intent.putExtra("surname",edt_register__surname.getText().toString().trim());
                        intent.putExtra("objectId", backendlessUser.getObjectId());
                        startActivity(intent);
                        progressDialog.dismiss();
                        Register.this.finish();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(Register.this, backendlessFault.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }else {
                Toast.makeText(Register.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void validateEditText(Editable s,View v) {
        if (!TextUtils.isEmpty(s)) {
            if(v.getId() == R.id.edt_register_name)
            txt_input.setError(null);
            else if(v.getId() == R.id.edt_register_surname)
                surname_txt_input.setError(null);
            else if(v.getId() == R.id.edt_register_email)
                email_txt_input.setError(null);
            else if(v.getId() == R.id.edt_register_password)
                pass_txt_input.setError(null);
            else if(v.getId() == R.id.edt_register_rePassword)
                repass_txt_input.setError(null);
        }
        else{
            if(v.getId() == R.id.edt_register_name)
                txt_input.setError(getString(R.string.txt_input_layout));
            else if(v.getId() == R.id.edt_register_surname)
                surname_txt_input.setError(getString(R.string.txt_input_layout));
            else if(v.getId() == R.id.edt_register_email)
                email_txt_input.setError(getString(R.string.txt_input_layout));
            else if(v.getId() == R.id.edt_register_password)
                pass_txt_input.setError(getString(R.string.txt_input_layout));
            else if(v.getId() == R.id.edt_register_rePassword)
                repass_txt_input.setError(getString(R.string.txt_input_layout));
        }
    }
    private void initialiseViews(){
        btn_submit = (Button) findViewById(R.id.btn_submit);
        edt_register__name = (EditText) findViewById(R.id.edt_register_name);
        edt_register__surname = (EditText) findViewById(R.id.edt_register_surname);
        edt_register__email = (EditText) findViewById(R.id.edt_register_email);
        edt_register__password = (EditText) findViewById(R.id.edt_register_password);
        edt_register__rePassword = (EditText) findViewById(R.id.edt_register_rePassword);
        txt_input = (TextInputLayout)findViewById(R.id.name_txt_input);
        surname_txt_input = (TextInputLayout)findViewById(R.id.surname_txt_input);
        email_txt_input = (TextInputLayout)findViewById(R.id.email_txt_input);
        pass_txt_input = (TextInputLayout)findViewById(R.id.password_txt_input);
        repass_txt_input = (TextInputLayout)findViewById(R.id.repassword_txt_input);
        register_toolBar = (Toolbar)findViewById(R.id.register_toolBar);
        setSupportActionBar(register_toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        register_toolBar.setTitleTextColor(getResources().getColor(R.color.colorIcons));
    }
    private void errorMsg(){
        edt_register__name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s,edt_register__name);
            }
        });
        edt_register__name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(),edt_register__name);
                }
            }
        });
        edt_register__surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s,edt_register__surname);
            }
        });
        edt_register__surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(),edt_register__surname);
                }
            }
        });
        edt_register__email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s,edt_register__email);
            }
        });
        edt_register__email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(),edt_register__email);
                }
            }
        });
        edt_register__password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s,edt_register__password);
            }
        });
        edt_register__password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(),edt_register__password);
                }
            }
        });
        edt_register__rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s,edt_register__rePassword);
            }
        });
        edt_register__rePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(),edt_register__rePassword);
                }
            }
        });
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










