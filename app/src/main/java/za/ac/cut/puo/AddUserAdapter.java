package za.ac.cut.puo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Watlinton on 2016/10/26.
 */

public class AddUserAdapter extends ArrayAdapter<BackendlessUser> {

    private final Context context;
    private final List<BackendlessUser> values;
    TextView tvUsername, tvStatus, tvUsertype;
    CircleImageView civ_user_pic;
    ImageView ivCall, ivMail, ivSMS;
    String word = "";
    BackendlessUser user = Backendless.UserService.CurrentUser();

    public AddUserAdapter(Context context, List<BackendlessUser> list) {
        super(context, R.layout.word_mates_list_item, list);
        this.context = context;
        this.values = list;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.word_mates_list_item, parent, false);
        tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        tvUsertype = (TextView) convertView.findViewById(R.id.tvUsertype);
        civ_user_pic = (CircleImageView) convertView.findViewById(R.id.civ_user_pic);
        ivCall = (ImageView) convertView.findViewById(R.id.ivCall);
        ivMail = (ImageView) convertView.findViewById(R.id.ivMail);
        ivSMS = (ImageView) convertView.findViewById(R.id.ivSMS);
        tvUsername.setText(values.get(position).getProperty("name").toString() + " " + values.get(position).getProperty("surname").toString());
        tvStatus.setText(values.get(position).getProperty("status").toString());
        tvStatus.setText(values.get(position).getProperty("status").toString());
        tvUsertype.setText(values.get(position).getProperty("role").toString());
        String imageUri = "https://api.backendless.com/D200A885-7EED-CB51-FFAC-228F87E55D00/v1/files/UserProfilePics/" + values.get(position).getEmail() + "_.png";
        if (!(imageUri.equals("")))
            Picasso.with(context).load(imageUri).fit()
                    .placeholder(PUOHelper.getTextDrawable(values.get(position)))
                    .error(PUOHelper.getTextDrawable(values.get(position)))
                    .into(civ_user_pic);
        if (values.get(position).getProperty("status").toString().equals("Online")) {
            tvStatus.setTextColor(getContext().getResources()
                    .getColor(R.color.gColorTime));
        }

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + values.get(position).getProperty("cell").toString()));
                context.startActivity(intent);
            }
        });
        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{values.get(position).getEmail()});
                context.startActivity(Intent.createChooser(intent, "Send Email:"));
            }
        });
        ivSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("address", values.get(position).getProperty("cell").toString());
                //sendIntent.putExtra("sms_body"  , "default");
                sendIntent.setType("vnd.android-dir/mms-sms");
                context.startActivity(sendIntent);
            }
        });
        return convertView;
    }

}
