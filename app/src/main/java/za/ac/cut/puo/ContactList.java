package za.ac.cut.puo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class ContactList extends AppCompatActivity {
    ListView lvContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        lvContactList = (ListView) findViewById(R.id.lvContactList);
    }

}
