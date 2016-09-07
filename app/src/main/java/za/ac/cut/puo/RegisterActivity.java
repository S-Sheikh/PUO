package za.ac.cut.puo;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_submit;
    EditText edt_register__name, edt_register__surname, edt_register__username, edt_register__email, edt_register__password, edt_register__rePassword;
    TextInputLayout txt_input, surname_txt_input, username_txt_input, email_txt_input, pass_txt_input, repass_txt_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialiseViews();
        errorMsg();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (!(edt_register__name.getText().toString().isEmpty() && edt_register__surname.getText().toString().isEmpty()
                        && edt_register__username.getText().toString().isEmpty() && edt_register__email.getText().toString().isEmpty()
                        && edt_register__password.getText().toString().isEmpty() && edt_register__rePassword.getText().toString().isEmpty())) {

                } else {
                    /**
                     txt_input.setError("Please fill in all fields");
                     surname_txt_input.setError("Please fill in all fields");
                     username_txt_input.setError("Please fill in all fields");
                     email_txt_input.setError("Please fill in all fields");
                     pass_txt_input.setError("Please fill in all fields");
                     repass_txt_input.setError("Please fill in all fields");**/
                }

        }
    }

    private void validateEditText(Editable s, View v) {
        if (!TextUtils.isEmpty(s)) {
            if (v.getId() == R.id.edt_register_name)
                txt_input.setError(null);
            else if (v.getId() == R.id.edt_register_surname)
                surname_txt_input.setError(null);
            else if (v.getId() == R.id.edt_register_username)
                username_txt_input.setError(null);
            else if (v.getId() == R.id.edt_register_email)
                email_txt_input.setError(null);
            else if (v.getId() == R.id.edt_register_password)
                pass_txt_input.setError(null);
            else if (v.getId() == R.id.edt_register_rePassword)
                repass_txt_input.setError(null);
        } else {
            if (v.getId() == R.id.edt_register_name)
                txt_input.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_register_surname)
                surname_txt_input.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_register_username)
                username_txt_input.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_register_email)
                email_txt_input.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_register_password)
                pass_txt_input.setError(getString(R.string.txt_input_layout));
            else if (v.getId() == R.id.edt_register_rePassword)
                repass_txt_input.setError(getString(R.string.txt_input_layout));
        }
    }

    private void initialiseViews() {
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        edt_register__name = (EditText) findViewById(R.id.edt_register_name);
        edt_register__surname = (EditText) findViewById(R.id.edt_register_surname);
        edt_register__username = (EditText) findViewById(R.id.edt_register_username);
        edt_register__email = (EditText) findViewById(R.id.edt_register_email);
        edt_register__password = (EditText) findViewById(R.id.edt_register_password);
        edt_register__rePassword = (EditText) findViewById(R.id.edt_register_rePassword);
        txt_input = (TextInputLayout) findViewById(R.id.name_txt_input);
        surname_txt_input = (TextInputLayout) findViewById(R.id.surname_txt_input);
        username_txt_input = (TextInputLayout) findViewById(R.id.usermame_txt_input);
        email_txt_input = (TextInputLayout) findViewById(R.id.email_txt_input);
        pass_txt_input = (TextInputLayout) findViewById(R.id.password_txt_input);
        repass_txt_input = (TextInputLayout) findViewById(R.id.repassword_txt_input);
    }

    private void errorMsg() {
        edt_register__name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, edt_register__name);
            }
        });
        edt_register__name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__name);
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
                validateEditText(s, edt_register__surname);
            }
        });
        edt_register__surname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__surname);
                }
            }
        });
        edt_register__username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEditText(s, edt_register__username);
            }
        });
        edt_register__username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__username);
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
                validateEditText(s, edt_register__email);
            }
        });
        edt_register__email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__email);
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
                validateEditText(s, edt_register__password);
            }
        });
        edt_register__password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__password);
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
                validateEditText(s, edt_register__rePassword);
            }
        });
        edt_register__rePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText(((EditText) v).getText(), edt_register__rePassword);
                }
            }
        });
    }
}










