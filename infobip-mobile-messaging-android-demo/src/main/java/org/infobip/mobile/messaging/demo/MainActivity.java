package org.infobip.mobile.messaging.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import org.infobip.mobile.messaging.Event;
import org.infobip.mobile.messaging.Message;
import org.infobip.mobile.messaging.MessageStore;
import org.infobip.mobile.messaging.MobileMessaging;

public class MainActivity extends AppCompatActivity {
    private boolean isReceiverRegistered;
    private TextView totalReceivedTextView;
    private ExpandableListView messagesListView;
    private ExpandableListAdapter listAdapter;
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = new Message(intent.getExtras());
            String body = message.getBody();
            Toast.makeText(MainActivity.this, "Message received: " + body, Toast.LENGTH_LONG).show();
            updateCount(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MobileMessaging.Builder(this)
                .withCallbackActivity(MainActivity.class)
                .withApplicationCode(getResources().getString(R.string.infobip_application_code))
                .withGcmSenderId(getResources().getString(R.string.google_app_id))
                .withDefaultTitle(getResources().getString(R.string.app_name))
                .withDefaultIcon(R.mipmap.ic_launcher)
//                .withDisplayNotification()
//                .withoutDisplayNotification()
//                .withApiUri("http://10.116.52.238:18080")
                .withApiUri("https://oneapi.ioinfobip.com")
                .withMessageStore()
//                .withoutMessageStore()
                .build();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalReceivedTextView = (TextView) findViewById(R.id.totalReceivedTextView);
        messagesListView = (ExpandableListView) findViewById(R.id.messagesListView);
        listAdapter = new ExpandableListAdapter(this);
        messagesListView.setAdapter(listAdapter);

        registerReceiver();
        updateCount(false);
        MobileMessaging.getInstance().disableNotification();
    }

    private void fillSomeData() {
        MessageStore.INSTANCE.save(Message.create("1", "Top 250"));
        MessageStore.INSTANCE.save(Message.create("2", "Now Showing"));
        MessageStore.INSTANCE.save(Message.create("3", "Coming Soon.."));

        updateCount(true);
    }

    private void updateCount(boolean refreshList) {
        totalReceivedTextView.setText(String.valueOf(MessageStore.INSTANCE.countAll()));
        if (refreshList) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onEraseInboxClick(View view) {
        MessageStore.INSTANCE.deleteAll();
        updateCount(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobileMessaging.getInstance().disableNotification();
        registerReceiver();
        updateCount(true);
    }

    @Override
    protected void onPause() {
        MobileMessaging.getInstance().enableNotification();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {

            LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                    new IntentFilter(Event.MESSAGE_RECEIVED.getKey()));
            isReceiverRegistered = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fillSomeData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
