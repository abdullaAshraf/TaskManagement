package com.example.abdul.testground.Organizations;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.abdul.testground.R;

/**
 * Created by aabdu on 7/11/2018.
 */

public class itemsAdapter extends RecyclerView.Adapter<itemsAdapter.ViewHolder> {

    Context context;
    int size;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mIcon;

        public ViewHolder(View v) {
            super(v);
            mIcon = v.findViewById(R.id.icon_item);
        }
    }


    public itemsAdapter(Context context, int size) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.size = size;
    }


    public itemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_icon, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return size;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String icon = "icon" + position;
        final int resID = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
        holder.mIcon.setImageResource(resID);

        holder.mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.mIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryBright), android.graphics.PorterDuff.Mode.MULTIPLY);
                //holder.mIcon.setRotation(90);
                AddOrgFragment.selectediconp = position;
                AddOrgFragment.selectedicon.setImageResource(resID);
            }
        });

    }

}