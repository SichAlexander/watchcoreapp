package com.boost.watchcore.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.ViewGroup;

import com.boost.watchcore.adapter.view.ConfigListItemLayout;
import com.boost.watchcore.model.Property;

import java.util.List;

/**
 * Created by Alex_Jobs on 28.04.2015.
 */
public class ConfigMaterialAdapter  extends WearableListView.Adapter {
    private List<Property> mProperties;
    private final Context mContext;

    public ConfigMaterialAdapter(Context context, List<Property> propertyList) {
        mContext = context;
        mProperties = propertyList;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private ConfigListItemLayout mConfigItem;
        public ItemViewHolder(ConfigListItemLayout itemView) {
            super(itemView);
            mConfigItem = itemView;
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(new ConfigListItemLayout(mContext));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        Property property = mProperties.get(position);
        if (property.isShow())
            itemHolder.mConfigItem.setImageResource(property.getLogoOnId());
        else
            itemHolder.mConfigItem.setImageResource(property.getLogoOffId());
        itemHolder.mConfigItem.setText(property.getTitle());
        holder.itemView.setTag(position);
        Log.d("cs_watch", "item height: " +itemHolder.mConfigItem.getHeight() );
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return mProperties.size();
    }
}