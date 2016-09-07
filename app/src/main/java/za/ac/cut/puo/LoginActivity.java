package za.ac.cut.puo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_login;
    EditText edt_login_email, edt_login_password;
    TextInputLayout email_txt_input_login, pass_txt_input_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    private void initialiseViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        edt_login_email = (EditText) findViewById(R.id.edt_email);
        edt_login_password = (EditText) findViewById(R.id.edt_password);
        email_txt_input_login = (TextInputLayout) findViewById(R.id.email_txt_input_login);
        pass_txt_input_login = (TextInputLayout) findViewById(R.id.pass_txt_input_login);
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
            if (v.getId() == R.id.edt_email)
                email_txt_input_login.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_password)
                pass_txt_input_login.setError(getString(R.string.txt_input_layout));
        }
    }
}
