package za.ac.cut.puo;


import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.PublishOptions;
import com.backendless.services.messaging.MessageStatus;

import java.io.IOException;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaPlayer.MEDIA_ERROR_MALFORMED;
import static android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordDetailDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordDetailDialogFragment extends AppCompatDialogFragment implements Toolbar.OnMenuItemClickListener {
    static final int REQUEST_SELECT_PHONE_NUMBER = 1;
    static final int REQUEST_SELECT_EMAIL = 2;
    static final int REQUEST_SELECT_WORD_MATE = 3;
    private static WordDetailDialogFragmentListener mListener;
    private static Word selectedWord;
    private static Drawable wordImage;
    private static int wordPosition;
    private View wordDetailView;
    private RatingBar wordRatings;
    private Toolbar wordDetailToolbar, wordActionsBar;
    private TextView wordStatus, supporters;
    private ViewGroup mContainer;
    private ImageView ivWordAudio, ivBgSupporters;
    private MediaPlayer mediaPlayer;
    private ProgressDialog audioDlg;


    public WordDetailDialogFragment() {
        // Required empty public constructor
    }

    public void setWordDetailDialogFragmentListener(WordDetailDialogFragmentListener listener) {
        mListener = listener;
    }

    /**
     * @return A new instance of fragment WordDetailDialogFragment.
     */
    public static WordDetailDialogFragment newInstance(Word word, Drawable image, int position) {
        selectedWord = word;
        wordImage = image;
        wordPosition = position;
        return new WordDetailDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = container;
        // Inflate the layout for this fragment
        wordDetailView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        ImageView descImage = (ImageView) wordDetailView.findViewById(R.id.word_desc_image);
        TextView wordText = (TextView) wordDetailView.findViewById(R.id.tv_word_text);
        wordStatus = (TextView) wordDetailView.findViewById(R.id.tv_word_status);
        supporters = (TextView) wordDetailView.findViewById(R.id.tv_supporters);
        TextView wordAuthor = (TextView) wordDetailView.findViewById(R.id.tv_word_author);
        TextView wordLexicon = (TextView) wordDetailView.findViewById(R.id.tv_word_lexicon);
        TextView wordDefinition = (TextView) wordDetailView.findViewById(R.id.tv_word_definition);
        TextView wordSentence = (TextView) wordDetailView.findViewById(R.id.tv_word_sentence);
        ivWordAudio = (ImageView) wordDetailView.findViewById(R.id.iv_ic_pronunciation);
        ivBgSupporters = (ImageView) wordDetailView.findViewById(R.id.iv_bg_supporters);
        wordRatings = (RatingBar) wordDetailView.findViewById(R.id.rtb_word_rating);
        wordActionsBar = (Toolbar) wordDetailView.findViewById(R.id.word_actions_toolbar);
        wordDetailToolbar = (Toolbar) wordDetailView.findViewById(R.id.word_detail_toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) wordDetailView.findViewById(R.id.collapsing_toolbar);

        descImage.setImageDrawable(wordImage);
        wordText.setText(selectedWord.getWord());
        wordStatus.setText(selectedWord.getStatus());
        wordAuthor.setText(selectedWord.getAuthor());
        wordLexicon.setText(selectedWord.getLexicon());
        wordDefinition.setText(selectedWord.getDefinition());
        wordSentence.setText(selectedWord.getSentence());
        wordRatings.setRating(selectedWord.getRating());
        wordActionsBar.inflateMenu(R.menu.menu_word_actions);
        collapsingToolbar.setTitle(selectedWord.getWord());

        /*change text color to green if word is supported.*/
        if (selectedWord.isSupported() && !selectedWord.isBlocked()) {
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.gLime));
            supporters.setVisibility(View.VISIBLE);
            ivBgSupporters.setVisibility(View.VISIBLE);
            supporters.setText(String.valueOf(selectedWord.getSupporters()));
        } else {
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.colorPrimaryText));
        }
        /*change text color to cayenne if word is blocked.*/
        if (selectedWord.isBlocked())
            wordStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.Cayenne));

        /**
         * Check if user is Collector and remove menu options accordingly.
         */
        if (Backendless.UserService.CurrentUser().getProperty("role").toString()
                .equalsIgnoreCase("Collector")) {
            wordActionsBar.getMenu().removeItem(R.id.support);
            wordActionsBar.getMenu().removeItem(R.id.block);
            wordActionsBar.getMenu().removeItem(R.id.edit_word);
        }

        wordActionsBar.setOnMenuItemClickListener(this);
        ivWordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedWord.getPronunciation() != null)
                    if (!selectedWord.getPronunciation().isEmpty())
                        streamWordAudio();

                Toast.makeText(getContext(), "No pronunciation set for this word!", Toast.LENGTH_SHORT).show();
            }
        });

        return wordDetailView;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_word:
                return true;
            case R.id.share:
                prepareShareOptions(wordActionsBar);
                return true;
            case R.id.add_to_word_chest:
                PUOHelper.SaveToWordChestTask.getTask(getContext()).execute(selectedWord);
                return true;
            case R.id.rate:
                rateWord();
                return true;
            case R.id.support:
                supportWord();
                return true;
            case R.id.block:
                blockWord();
                return true;
            default:
                return true;
        }
    }

    /**
     * Updates a word status to supported.
     */
    public void supportWord() {
        if (PUOHelper.connectionAvailable(getContext())) {

            if (!selectedWord.isBlocked()) {
                if (!selectedWord.isSupported()) {
                    selectedWord.setSupported(true);
                    wordStatus.setText(selectedWord.getStatus());
                    wordStatus.setTextColor(getContext().getResources()
                            .getColor(R.color.gLime));
                    supporters.setVisibility(View.VISIBLE);
                    ivBgSupporters.setVisibility(View.VISIBLE);
                }
                selectedWord.setSupporters(1);
                supporters.setText(String.valueOf(selectedWord.getSupporters()));
                ((WordListFragment) getTargetFragment())
                        .getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        Toast.makeText(getContext(), word.getWord() + ": supported!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), selectedWord.getWord() +
                        ": is blocked, cannot support!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Updates the word rating.
     */
    public void rateWord() {
        if (PUOHelper.connectionAvailable(getContext())) {
            final PopupWindow ratingPopup = PUOHelper.getPopup(getContext(), mContainer);
            final RatingBar ratingBar = (RatingBar) ratingPopup.getContentView().findViewById(R.id.rating_bar);
            Button rate = (Button) ratingPopup.getContentView().findViewById(R.id.btn_rate);
            ratingPopup.showAtLocation(wordDetailView, Gravity.CENTER, 0, 0);
            ratingPopup.update();
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ratingBar.getRating() > 0) {
                        selectedWord.setRating(ratingBar.getRating());
                        ((WordListFragment) getTargetFragment())
                                .getmAdapter().notifyItemChanged(wordPosition);
                        wordRatings.setRating(selectedWord.getRating());
                        Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                            @Override
                            public void handleResponse(Word word) {
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(getContext(), backendlessFault.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(getContext(), "No rating set!", Toast.LENGTH_SHORT).show();
                    ratingPopup.dismiss();
                }
            });
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Updates a word status to blocked and sets supported to false.
     */
    public void blockWord() {
        if (PUOHelper.connectionAvailable(getContext())) {
            if (!selectedWord.isBlocked()) {
                selectedWord.setBlocked(true);
                selectedWord.setSupported(false);
                wordStatus.setTextColor(getContext().getResources()
                        .getColor(R.color.Cayenne));
                ((WordListFragment) getTargetFragment())
                        .getmAdapter().notifyItemChanged(wordPosition);
                Backendless.Persistence.save(selectedWord, new AsyncCallback<Word>() {
                    @Override
                    public void handleResponse(Word word) {
                        Toast.makeText(getContext(), selectedWord.getWord() + ": is now blocked!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getContext(), backendlessFault.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getContext(), selectedWord.getWord() + ": is already blocked!",
                        Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Streams the word audio from Backendless.
     */
    public void streamWordAudio() {
        if (PUOHelper.connectionAvailable(getContext())) {
            mediaPlayer = new MediaPlayer();
            audioDlg = ProgressDialog.show(getContext(), "Getting pronunciation", "Please wait...");
            audioDlg.setCancelable(true);

            // Set type to streaming
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // Listen for if the audio file can't be prepared
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MEDIA_ERROR_UNKNOWN:
                            audioDlg.cancel();
                            Toast.makeText(getContext(), "MEDIA_ERROR_UNKNOWN: " + what, Toast.LENGTH_SHORT).show();
                            mp.reset();
                            return true;
                        case MEDIA_ERROR_MALFORMED:
                            audioDlg.cancel();
                            Toast.makeText(getContext(), "MEDIA_ERROR_MALFORMED: " + what, Toast.LENGTH_SHORT).show();
                            mp.reset();
                            return true;
                        default:
                            audioDlg.cancel();
                            Toast.makeText(getContext(), "GENERAL_ERROR", Toast.LENGTH_SHORT).show();
                            mp.reset();
                            return true;
                    }
                }
            });

            // Attach to when audio file is prepared for playing
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    audioDlg.cancel();
                    mediaPlayer.start();
                }
            });

            // Set the data source to the remote URL
            try {
                mediaPlayer.setDataSource(Defaults.AUDIO_BASE_URL + selectedWord.getPronunciation());
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            // Listen for when the audio has completed playing.
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            // Trigger an async preparation which will file listener when completed
            mediaPlayer.prepareAsync();
        } else
            Toast.makeText(getContext(), "No internet available! Please check connection.",
                    Toast.LENGTH_LONG).show();

    }

    /**
     * Prepare and display word share options popup menu.
     */
    public void prepareShareOptions(View v) {
        PopupMenu shareOptions = new PopupMenu(getContext(), v);
        shareOptions.inflate(R.menu.menu_word_share);
        shareOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.word_mate:
                        sendToWordMate();
                        return true;
                    case R.id.email:
                        selectContact(REQUEST_SELECT_EMAIL);
                        return true;
                    case R.id.sms:
                        selectContact(REQUEST_SELECT_PHONE_NUMBER);
                        return true;
                    default:
                        return false;
                }
            }
        });
        shareOptions.show();
    }

    interface WordDetailDialogFragmentListener {
        void onWordActionClicked(int Id, @Nullable Object data);
    }

    /**
     * Start an activity for the user to pick a contact from device.
     */
    public void selectContact(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        switch (requestCode) {
            case REQUEST_SELECT_PHONE_NUMBER:
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                break;
            case REQUEST_SELECT_EMAIL:
                intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
                break;
        }
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                sendSMS(number,prepareMessage());
            }
        } else if (requestCode == REQUEST_SELECT_EMAIL && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the email
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
            Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the email
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                String email = cursor.getString(numberIndex);
                sendEmail(email,prepareMessage());
            }
        }else if (requestCode == REQUEST_SELECT_WORD_MATE && resultCode == RESULT_OK) {
            Map<String, Object> userProperties = (Map<String, Object>) data.getSerializableExtra("user_properties");

            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addPushSinglecast(userProperties.get("deviceId").toString());

            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader( "android-ticker-text", "New Word Received!" );
            publishOptions.putHeader( "android-content-title", "PUO" );
            publishOptions.putHeader( "android-content-text",
                    Backendless.UserService.CurrentUser().getProperty("name") +
                            "Shared a word with you." );

            Backendless.Messaging.publish(prepareMessage(), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus messageStatus) {
                    Toast.makeText(getContext(), messageStatus.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
        }
    }

    /**
     * Send an sms message to another device.
     */
    private void sendSMS(String number, String message) {

        PendingIntent sentIntent = PendingIntent.getBroadcast(getContext(), 0,
                new Intent("SMS_SENT"), 0);

        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getContext(), 0,
                new Intent("SMS_DELIVERED"), 0);

        //SMS sent broadcast receiver
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));

        //SMS delivered broadcast receiver
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED"));

        SmsManager.getDefault().sendTextMessage(number, null, message, sentIntent, deliveredIntent);
    }

    /**
     * Send an email message to another device.
     */
    private void sendEmail(String address, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_word_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        if (emailIntent.resolveActivity(getContext().getPackageManager()) != null)
            startActivity(emailIntent);
    }

    /**
     * Start an activity for the user to pick a user from the app.
     */
    private void sendToWordMate(){
        Intent wordMateIntent = new Intent(getContext(),WordMates.class);
        wordMateIntent.putExtra("request_code", REQUEST_SELECT_WORD_MATE);
        startActivityForResult(wordMateIntent,REQUEST_SELECT_WORD_MATE);
    }

    private String prepareMessage() {

        return selectedWord.getWord() +
                "\n" + "Languge: " +
                selectedWord.getLanguage() +
                "\n" + "Part of speech: " +
                selectedWord.getPartOfSpeech() +
                "\n" + "Definition: " +
                selectedWord.getDefinition() +
                "\n" + "Sentence: " +
                "\n" + selectedWord.getSentence();
    }
}
