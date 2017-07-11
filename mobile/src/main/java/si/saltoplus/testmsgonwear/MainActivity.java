package si.saltoplus.testmsgonwear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.button2)
    View btnSend;

    private static final String TAG = "MainActivity";

    public static final String LOGIN_PATH = "/login";
    public static final String MESSAGE_PATH = "/msg";
    public static final String ACTIVITY_PATH = "/startActivity";

    private GoogleApiClient mGoogleApiClient;

    private String mNode;
    private final Context mContext;

    public MainActivity() {
        mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFirstNode();
            }
        });

        // Setting up the Wearable API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    private void sendToFirstNode() {
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
                        send();
                    }
                });
    }

    public void send() {
        Log.e(TAG, "Trying to send message to: " + mNode);

        if (!mGoogleApiClient.isConnected()) mGoogleApiClient.connect();

        Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode, ACTIVITY_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        });

    }
}
