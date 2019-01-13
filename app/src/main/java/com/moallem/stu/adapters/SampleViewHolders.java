package com.moallem.stu.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moallem.stu.R;

public class SampleViewHolders extends RecyclerView.ViewHolder
{
    ImageView img;
    TextView nameTxt;

    public SampleViewHolders(View itemView) {
        super(itemView);

        nameTxt= (TextView) itemView.findViewById(R.id.tv);
        img= (ImageView) itemView.findViewById(R.id.iv);

    }

}
