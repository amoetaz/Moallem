package com.moallem.stu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.moallem.stu.R;
import com.moallem.stu.models.Subject;
import com.moallem.stu.utilities.Utils;

import java.util.ArrayList;

public class SampleRecyclerViewAdapter extends RecyclerView.Adapter<SampleViewHolders> {
    private Context context;
    private ArrayList<Subject> subjects;
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public SampleRecyclerViewAdapter(Context context, ArrayList<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public SampleViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        SampleViewHolders holder = new SampleViewHolders(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SampleViewHolders holder, int position) {
        Subject subect = subjects.get(position);
        if (Utils.getDeviceLanguage(context).equals("arabic")) {
            holder.nameTxt.setText(subect.getArabicName());
        } else {
            holder.nameTxt.setText(subect.getName());
        }
        Glide.with(context).load(subect.getImage()).into(holder.img);
        if (isBigBox(position))
            holder.itemView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.itemview_height1);
        else
            holder.itemView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.itemview_height2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    private boolean isBigBox(int position) {
        return position % 2 != 0;
    }


    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

}