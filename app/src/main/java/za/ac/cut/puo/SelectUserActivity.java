package za.ac.cut.puo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.services.messaging.MessageStatus;
import com.backendless.services.messaging.PublishStatusEnum;

import java.util.ArrayList;
import java.util.List;

public class SelectUserActivity extends Activity {
    private Button previousPageButton, nextPageButton;
    private ListView usersList;

    private ArrayAdapter adapter;

    private BackendlessCollection<ChatUser> users;
    private ChatUser companion;
    private int totalPages;
    private int currentPageNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_user);

        currentPageNumber = 1;

        initUI();

        Backendless.Persistence.of(ChatUser.class).find(new DefaultCallback<BackendlessCollection<ChatUser>>(this, "Getting chat users") {
            @Override
            public void handleResponse(BackendlessCollection<ChatUser> response) {
                super.handleResponse(response);
                totalPages = (int) Math.ceil((double) response.getTotalObjects() / response.getCurrentPage().size());
                initList(response);
                initButtons();
            }
        });
    }

    private void initUI() {
        usersList = (ListView) findViewById(R.id.usersList);
        previousPageButton = (Button) findViewById(R.id.previousButton);
        nextPageButton = (Button) findViewById(R.id.nextButton);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.previousPage(new DefaultCallback<BackendlessCollection<ChatUser>>(SelectUserActivity.this) {
                    @Override
                    public void handleResponse(BackendlessCollection<ChatUser> response) {
                        super.handleResponse(response);
                        initList(response);
                        currentPageNumber--;
                        initButtons();
                    }
                });
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.nextPage(new DefaultCallback<BackendlessCollection<ChatUser>>(SelectUserActivity.this) {
                    @Override
                    public void handleResponse(BackendlessCollection<ChatUser> response) {
                        super.handleResponse(response);
                        initList(response);
                        currentPageNumber++;
                        initButtons();
                    }
                });
            }
        });
    }

    private void onListItemClick(int position) {
        String companionNickname = usersList.getItemAtPosition(position).toString();
        String whereClause = "nickname = '".concat(companionNickname).concat("'");

        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        backendlessDataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(ChatUser.class).find(backendlessDataQuery, new DefaultCallback<BackendlessCollection<ChatUser>>(this) {
            @Override
            public void handleResponse(BackendlessCollection<ChatUser> response) {
                super.handleResponse(response);
                companion = response.getCurrentPage().iterator().next();
                onCompanionFound();
            }
        });
    }

    private void onCompanionFound() {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(PublishOptions.ANDROID_TICKER_TEXT_TAG, String.format(DefaultMessages.CONNECT_DEMAND, ChatUser.currentUser().getNickname()));
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TITLE_TAG, getResources().getString(R.string.app_name));
        publishOptions.putHeader(PublishOptions.ANDROID_CONTENT_TEXT_TAG, String.format(DefaultMessages.CONNECT_DEMAND, ChatUser.currentUser().getNickname()));
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
        deliveryOptions.addPushSinglecast(companion.getDeviceId());

        final String message_subtopic = ChatUser.currentUser().getNickname().concat("_with_").concat(companion.getNickname());

        Backendless.Messaging.publish(Defaults.DEFAULT_CHANNEL, message_subtopic, publishOptions, deliveryOptions, new DefaultCallback<MessageStatus>(this, "Sending push message") {
            @Override
            public void handleResponse(MessageStatus response) {
                super.handleResponse(response);

                PublishStatusEnum messageStatus = response.getStatus();

                if (messageStatus == PublishStatusEnum.SCHEDULED) {
                    Intent chatIntent = new Intent(SelectUserActivity.this, ChatActivity.class);
                    chatIntent.putExtra("owner", true);
                    chatIntent.putExtra("subtopic", message_subtopic);
                    chatIntent.putExtra("companionNickname", companion.getNickname());
                    startActivity(chatIntent);
                    finish();
                } else {
                    Toast.makeText(SelectUserActivity.this, "Message status: " + messageStatus.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initList(BackendlessCollection<ChatUser> response) {
        users = response;

        String[] usersArray = removeNulls(response);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_with_arrow, R.id.itemName, usersArray);
        usersList.setAdapter(adapter);
    }

    private void initButtons() {
        previousPageButton.setEnabled(currentPageNumber != 1);
        nextPageButton.setEnabled(currentPageNumber != totalPages);
    }

    private String[] removeNulls(BackendlessCollection<ChatUser> users) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < users.getCurrentPage().size(); i++) {
            ChatUser chatUser = users.getCurrentPage().get(i);
            if (chatUser.getNickname() != null && chatUser.getDeviceId() != null && !chatUser.getDeviceId().isEmpty() && !chatUser.getNickname().isEmpty()) {
                result.add(chatUser.getNickname());
            }
        }

        return result.toArray(new String[result.size()]);
    }
}

                                            