package si.saltoplus.testmsgonwear;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import si.saltoplus.testmsgonwear.helpers.HelpMessages;

public class MainActivity extends Activity {

    @BindView(R.id.button2)
    View btnSend;

    @BindView(R.id.edit_text)
    EditText editText;

    private static final String TAG = "MainActivity";

    private HelpMessages mHelpMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mHelpMessages = new HelpMessages(this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelpMessages.sendToFirstNode(editText.getText().toString().getBytes(/*"UTF-8"*/));
            }
        });
    }
}
