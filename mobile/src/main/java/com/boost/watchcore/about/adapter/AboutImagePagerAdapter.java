package com.boost.watchcore.about.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boost.watchcore.R;

import java.util.List;

public class AboutImagePagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private List<Integer> mResources;
    private List<String> mDescription;
    public AboutImagePagerAdapter(Context context, final List<Integer> resources,  List<String> description) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = resources;
        mDescription = description;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.about_item_vp, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.about_ImageView_ItemViewPager);
        imageView.setImageResource(mResources.get(position));
        if (mDescription != null && position < mDescription.size()) {
            ((TextView) itemView.findViewById(R.id.description_TextView_ItemViewpager))
                    .setText(mDescription.get(position));
        }
        container.addView(itemView);
        if (position == getCount() -1){
            itemView.findViewById(R.id.next_ImageView_ItemViewPager)
                    .setVisibility(View.GONE);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
