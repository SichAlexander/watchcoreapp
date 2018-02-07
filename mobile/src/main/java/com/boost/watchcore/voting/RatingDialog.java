package com.boost.watchcore.voting;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.boost.watchcore.R;
import com.boost.watchcore.utils.Pref;

/**
 * Created by BruSD on 09.07.2015.
 */
public class RatingDialog extends DialogFragment implements View.OnClickListener {
    private View rootView;
    public static RatingDialog newInstance() {
        RatingDialog f = new RatingDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_rate_us, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        rootView.findViewById(R.id.awesome_TextView_VoteDialog).setOnClickListener(this);
        rootView.findViewById(R.id.normal_TextView_VoteDialog).setOnClickListener(this);
        rootView.findViewById(R.id.bad_TextView_VoteDialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.awesome_TextView_VoteDialog:
                openGooglePlayPage();
                break;
            case R.id.normal_TextView_VoteDialog:
                openGooglePlayPage();
                break;
            case R.id.bad_TextView_VoteDialog:
                sendMessageToDeveloper();
                break;
        }
    }

    private void openGooglePlayPage() {
        final String appPackageName = getActivity().getPackageName(); // Can also use getPackageName(), as below
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        Pref.setVoted(getActivity());
        getDialog().cancel();

    }

    private void sendMessageToDeveloper() {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:boostingconsult@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Dear developers can you please fix");

        startActivity(Intent.createChooser(emailIntent, "Send email to developers"));
        Pref.setVoted(getActivity());
        getDialog().cancel();
    }


}
