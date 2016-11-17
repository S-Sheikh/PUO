package za.ac.cut.puo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;

public class ChooseNicknameActivity extends Activity {
    private Button startChatButton;
    private EditText nicknameField;
    private boolean owner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_nickname);

        initUI();

        owner = getIntent().getBooleanExtra("owner", true);

        if (owner) {
            Backendless.setUrl(Defaults.SERVER_URL);
            Backendless.initApp(this, Defaults.APP_ID, Defaults.SECRET_KEY, Defaults.APP_VERSION);
        }

        Backendless.Messaging.registerDevice(Defaults.GOOGLE_PROJECT_ID, Defaults.DEFAULT_CHANNEL, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Toast.makeText(ChooseNicknameActivity.this, "Registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ChooseNicknameActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        startChatButton = (Button) findViewById(R.id.startButton);
        nicknameField = (EditText) findViewById(R.id.nameBox);

        SharedPreferences settings = getSharedPreferences("com.backendless.settings", Context.MODE_PRIVATE);
        String lastNickname = settings.getString("nickname", "");
        nicknameField.setText(lastNickname);

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartButtonClicked();
            }
        });
    }

    private void afterChatUserInit() {
        if (!owner) {
            final String subtopic = getIntent().getStringExtra("subtopic");
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.setSubtopic(subtopic);
            Backendless.Messaging.publish(Defaults.DEFAULT_CHANNEL, DefaultMessages.ACCEPT_CHAT_MESSAGE, publishOptions, new DefaultCallback<MessageStatus>(ChooseNicknameActivity.this) {
                @Override
                public void handleResponse(MessageStatus response) {
                    super.handleResponse(response);
                    if (response.getStatus() == PublishStatusEnum.SCHEDULED) {
                        Intent chatIntent = new Intent(ChooseNicknameActivity.this, ChatActivity.class);
                        chatIntent.putExtra("owner", owner);
                        chatIntent.putExtra("subtopic", subtopic);
                        startActivity(chatIntent);
                        finish();
                    } else {
                        Toast.makeText(ChooseNicknameActivity.this, response.getStatus().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            startActivity(new Intent(ChooseNicknameActivity.this, SelectUserActivity.class));
            finish();
        }
    }

    private void onStartButtonClicked() {
        String nickname = nicknameField.getText().toString();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "Nickname cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = Build.SERIAL;

        if (deviceId.isEmpty()) {
            Toast.makeText(this, "Could not retrieve DEVICE ID", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences settings = getSharedPreferences("com.backendless.settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("nickname", nickname);
        editor.commit();

        ChatUser.currentUser().setNickname(nickname);
        ChatUser.currentUser().setDeviceId(deviceId);

        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        backendlessDataQuery.setWhereClause("nickname='" + ChatUser.currentUser().getNickname() + "'");
        Backendless.Persistence.of(ChatUser.class).find(backendlessDataQuery, new DefaultCallback<BackendlessCollection<ChatUser>>(this, "Retrieving user data") {
            @Override
            public void handleResponse(BackendlessCollection<ChatUser> response) {
                super.handleResponse(response);
                if (response.getCurrentPage().isEmpty()) {
                    Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new DefaultCallback<ChatUser>(ChooseNicknameActivity.this) {
                        @Override
                        public void handleResponse(ChatUser response) {
                            super.handleResponse(response);
                            ChatUser.currentUser().setObjectId(response.getObjectId());
                            afterChatUserInit();
                        }
                    });
                } else {
                    ChatUser foundUser = response.getCurrentPage().iterator().next();
                    ChatUser.currentUser().setObjectId(foundUser.getObjectId());
                    if (!ChatUser.currentUser().getDeviceId().equals(foundUser.getDeviceId())) {
                        Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new DefaultCallback<ChatUser>(ChooseNicknameActivity.this, "Saving user") {
                            @Override
                            public void handleResponse(ChatUser response) {
                                super.handleResponse(response);
                                afterChatUserInit();
                            }
                        });
                    } else {
                        afterChatUserInit();
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                String notExistingTableErrorCode = "1009";
                if (fault.getCode().equals(notExistingTableErrorCode)) {
                    super.handleResponse(new BackendlessCollection<ChatUser>());
                    Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new DefaultCallback<ChatUser>(ChooseNicknameActivity.this) {
                        @Override
                        public void handleResponse(ChatUser response) {
                            super.handleResponse(response);
                            ChatUser.currentUser().setObjectId(response.getObjectId());
                            afterChatUserInit();
                        }
                    });
                } else {
                    super.handleFault(fault);
                }
            }
        });
    }
}
                                            