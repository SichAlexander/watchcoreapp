package com.boost.watchcore.voting;

import android.app.Activity;
import android.app.FragmentManager;

import com.boost.watchcore.utils.Pref;

/**
 * Created by BruSD on 09.07.2015.
 */
public class Voting {
    private Activity mContext;

    public Voting(Activity context) {
        mContext = context;
    }

    public void tryVoting() {
        if (!Pref.isVoted(mContext)) {
            if (Pref.getRunCount(mContext) > 3) {
                FragmentManager fragmentManager = mContext.getFragmentManager();
                RatingDialog newFragment = RatingDialog.newInstance();
                newFragment.show(fragmentManager, "dialog");
            } else {
                Pref.setSetRunCount(mContext, Pref.getRunCount(mContext) + 1);
            }
        }
    }
}
