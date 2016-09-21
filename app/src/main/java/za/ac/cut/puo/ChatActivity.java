package za.ac.cut.puo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.SubscriptionOptions;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;

import java.util.List;

public class ChatActivity extends Activity {
    private EditText history;
    private EditText messageField;
    private TextView chatWithSmbTitleTextView;

    private ProgressDialog progressDialog;
    private PublishOptions publishOptions;
    private SubscriptionOptions subscriptionOptions;
    private Subscription subscription;

    private String subtopic;
    private String companionNickname;
    private boolean isOwner;
    private boolean acceptationMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Intent currentIntent = getIntent();
        isOwner = currentIntent.getBooleanExtra("owner", false);
        subtopic = currentIntent.getStringExtra("subtopic");
        companionNickname = isOwner ? subtopic.split("_")[2] : subtopic.split("_")[0];

        initUI();

        acceptationMessage = isOwner;

        publishOptions = new PublishOptions();
        publishOptions.setPublisherId(ChatUser.currentUser().getNickname());
        publishOptions.setSubtopic(subtopic);

        subscriptionOptions = new SubscriptionOptions();
        subscriptionOptions.setSubtopic(subtopic);

        if (isOwner) {
            progressDialog = ProgressDialog.show(this, "", String.format(getResources().getString(R.string.waiting_for_template), companionNickname), true);
        }

        Backendless.Messaging.subscribe(Defaults.DEFAULT_CHANNEL, new AsyncCallback<List<Message>>() {
            @Override
            public void handleResponse(List<Message> response) {
                onReceiveMessage(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ChatActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, subscriptionOptions, new DefaultCallback<Subscription>(ChatActivity.this, "Retrieving subscription") {
            @Override
            public void handleResponse(Subscription response) {
                super.handleResponse(response);
                subscription = response;
            }
        });
    }

    private void initUI() {
        history = (EditText) findViewById(R.id.historyField);
        messageField = (EditText) findViewById(R.id.messageField);
        chatWithSmbTitleTextView = (TextView) findViewById(R.id.textChatWithSmbTitle);

        chatWithSmbTitleTextView.setText(String.format(getResources().getString(R.string.text_chat_with_smb_title), companionNickname));

        messageField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                return onSendMessage(keyCode, keyEvent);
            }
        });
    }

    private void onReceiveMessage(List<Message> messages) {
        if (isOwner) {
            progressDialog.cancel();
            isOwner = false;
        }
        if (acceptationMessage) {
            acceptationMessage = false;
            String message = messages.iterator().next().getData().toString();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (message.equals(DefaultMessages.DECLINE_CHAT_MESSAGE)) {
                startActivity(new Intent(this, SelectUserActivity.class));
                finish();
            } else {
                return;
            }
        }

        for (Message message : messages) {
            history.setText(history.getText() + "\n" + message.getPublisherId() + ": " + message.getData());
        }
    }

    private boolean onSendMessage(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
            String message = messageField.getText().toString();

            if (message == null || message.equals(""))
                return true;

            Backendless.Messaging.publish((Object) message, publishOptions, new DefaultCallback<MessageStatus>(ChatActivity.this, "Sending...") {
                @Override
                public void handleResponse(MessageStatus response) {
                    super.handleResponse(response);

                    PublishStatusEnum messageStatus = response.getStatus();

                    if (messageStatus == PublishStatusEnum.SCHEDULED) {
                        messageField.setText("");
                    } else {
                        Toast.makeText(ChatActivity.this, "Message status: " + messageStatus.toString(), Toast.LENGTH_SHORT);
                    }
                }
            });

            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.onStop();

        if (subscription != null)
            subscription.cancelSubscription();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subscription != null)
            subscription.resumeSubscription();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subscription != null)
            subscription.pauseSubscription();
    }
}
                                            