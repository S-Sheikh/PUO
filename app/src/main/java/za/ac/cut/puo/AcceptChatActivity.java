package za.ac.cut.puo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.messaging.PublishOptions;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;

public class AcceptChatActivity extends Activity {
    private Button acceptButton, declineButton;
    private TextView chatInvitationTextView;

    private PublishOptions publishOptions;
    private String subtopic;
    private String companionNickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_chat);

        Intent currentIntent = getIntent();
        subtopic = currentIntent.getStringExtra("subtopic");
        companionNickname = subtopic.split("_")[0];

        initUI();

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APP_ID, Defaults.SECRET_KEY, Defaults.APP_VERSION);

        publishOptions = new PublishOptions();
        publishOptions.setSubtopic(subtopic);
    }

    private void initUI() {
        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton = (Button) findViewById(R.id.declineButton);
        chatInvitationTextView = (TextView) findViewById(R.id.chatInvitationText);

        chatInvitationTextView.setText(String.format(getResources().getString(R.string.chat_invitation_message), companionNickname));

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAccept();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDecline();
            }
        });
    }

    private void onAccept() {
        Intent nextIntent = new Intent(this, ChooseNicknameActivity.class);
        nextIntent.putExtra("owner", false);
        nextIntent.putExtra("subtopic", subtopic);
        nextIntent.putExtra("companionNickname", companionNickname);
        startActivity(nextIntent);
        finish();
    }

    private void onDecline() {
        Backendless.Messaging.publish((Object) DefaultMessages.DECLINE_CHAT_MESSAGE, publishOptions, new DefaultCallback<MessageStatus>(this) {
            @Override
            public void handleResponse(MessageStatus response) {
                super.handleResponse(response);
                if (response.getStatus() == PublishStatusEnum.SCHEDULED) {
                    finish();
                } else {
                    Toast.makeText(AcceptChatActivity.this, response.getStatus().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
                                            