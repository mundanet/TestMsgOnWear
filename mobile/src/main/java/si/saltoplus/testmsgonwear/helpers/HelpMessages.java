package si.saltoplus.testmsgonwear.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

/**
 * Created by matjazmunda on 11/07/2017.
 */

public class HelpMessages implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HelpMessages";

    public static final String LOGIN_PATH = "/login";
    public static final String MESSAGE_PATH = "/msg";
    public static final String ACTIVITY_PATH = "/startActivity";

    private GoogleApiClient mGoogleApiClient;

    private String mNode;
    private final Context mContext;
    private final Activity mActivity;

    public HelpMessages(Activity activity) {
        this.mContext = activity;
        this.mActivity = activity;

        // Setting up the Wearable API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "Connection established");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e(TAG, "Connection suspended");
                    }
                })
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mGoogleApiClient.disconnect();
    }

    public void sendToFirstNode(@Nullable final byte[] data) {
        // Find the connected watches and store their UUIDs to distinguish at a later moment
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        List<Node> nodes = getConnectedNodesResult.getNodes();

                        if (nodes.isEmpty()) {
                            Log.e(TAG, "No Nodes found");
                            return;
                        }

                        mNode = nodes.get(0).getId();
                        Log.e(TAG, "Node is nearby: " + nodes.get(0).isNearby());
                        Log.e(TAG, mNode);
                        send(data);
                    }
                });
    }

    private void send(@Nullable byte[] data) {
        Log.e(TAG, "Trying to send message to: " + mNode);

        if (!mGoogleApiClient.isConnected()) mGoogleApiClient.connect();

        Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode, ACTIVITY_PATH, data).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                    return;
                }
                Log.e(TAG, "Message successfully sent");
                showToast(sendMessageResult.getStatus().toString());
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showToast(final String message) {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
