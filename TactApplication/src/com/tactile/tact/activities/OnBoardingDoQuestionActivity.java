package com.tactile.tact.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.tactile.tact.R;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.AppBaseBroadcast;

/**
 * Created by sebafonseca on 10/2/14.
 */
public class OnBoardingDoQuestionActivity extends AppBaseBroadcast {

    private Button done;
    private Button question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBaseActivityReceiver();
        setContentView(R.layout.do_question);
        done        = (Button) findViewById(R.id.done_question);
        question    = (Button) findViewById(R.id.do_question);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tactEmail    = "Tact Support <feedback@tactile.com>";
                String tactSubject  = getResources().getString(R.string.question_subject) + " #" + TactSharedPrefController.getUUID() + "#";
                //String tactBody     = "\nUser ID: " + LocalStorage.getInstance().getUuid() + "\n--------\n\n\n";
                String tactBody     = "";

                        // Try to send the email using Gmail
                Intent gmail        = new Intent(Intent.ACTION_VIEW);
                gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
                gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { tactEmail });
                gmail.setData(Uri.parse(tactEmail));
                gmail.putExtra(Intent.EXTRA_SUBJECT, tactSubject);
                gmail.setType("plain/text");
                gmail.putExtra(Intent.EXTRA_TEXT, tactBody);
                try {
                    startActivity(gmail);
                }
                catch (Exception e){
                    // Send the email with other client
                    Intent intent   = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{ tactEmail });
                    intent.putExtra(Intent.EXTRA_SUBJECT, tactSubject);
                    intent.putExtra(Intent.EXTRA_TEXT   , tactBody);
                    try {
                        startActivity(Intent.createChooser(intent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(view.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }
}
