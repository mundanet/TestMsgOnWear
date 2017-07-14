package si.saltoplus.testmsgonwear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by matjazmunda on 11/07/2017.
 */

public class MsgListenerService extends WearableListenerService {

    private static final String TAG = "MsgListenerService";

    public static final String BASE_PATH = "/mundanet";

    public static final String LOGIN_PATH = "/login";
    public static final String MESSAGE_PATH = "/msg";
    public static final String ACTIVITY_PATH = "/startActivity";

//    GoogleApiClient mGoogleApiClient;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        LOGD(TAG, "Service created");
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived: " + messageEvent);

        //filter out the pathPrefix
        String path = messageEvent.getPath().substring(BASE_PATH.length());

        // Check to see if the message is to start an activity
        if( path.equalsIgnoreCase( ACTIVITY_PATH ) ) {
            byte[] data = messageEvent.getData();

            Intent startIntent = new Intent(this, MainWearActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("data", data);
            startActivity(startIntent);
        }
        LOGD(TAG, "onMessageReceived: finished");
    }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        LOGD(TAG, "onDataChanged: " + dataEvents);
//        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
//            ConnectionResult connectionResult = mGoogleApiClient
//                    .blockingConnect(30, TimeUnit.SECONDS);
//            if (!connectionResult.isSuccess()) {
//                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient, "
//                        + "error code: " + connectionResult.getErrorCode());
//                return;
//            }
//        }
//        LOGD(TAG, "onDataChanged: finished");
//    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
