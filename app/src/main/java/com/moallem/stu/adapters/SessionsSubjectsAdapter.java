package com.moallem.stu.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moallem.stu.R;
import com.moallem.stu.models.Subject;
import com.moallem.stu.ui.fragments.SessionsFragment;

import java.util.ArrayList;

public class SessionsSubjectsAdapter extends RecyclerView.Adapter<SessionsSubjectsAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Subject> subjects;

    public SessionsSubjectsAdapter(Context context, ArrayList<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Subject subect = subjects.get(position);
        holder.nameTxt.setText(subect.getName());
        Glide.with(context).load(subect.getImage()).into(holder.img);
        if (isBigBox(position))
            holder.itemView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.itemview_height1);
        else
            holder.itemView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.itemview_height2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionsFragment sessionsFragment = new SessionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subjectKey",subect.getKey());
                sessionsFragment.setArguments(bundle);
                slideTrans(sessionsFragment);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fsessios,sessionsFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    private boolean isBigBox(int position){
        return position % 2 != 0;
    }


    private void slideTrans(Fragment fragment){
        Slide slideTransition = null ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slideTransition = new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START
                    , context.getResources().getConfiguration().getLayoutDirection()));
            slideTransition.setDuration(context.getResources().getInteger(R.integer.anim_duration_medium));

            ChangeBounds changeBoundsTransition = new ChangeBounds();
            changeBoundsTransition.setDuration(context.getResources().getInteger(R.integer.anim_duration_medium));

            fragment.setEnterTransition(slideTransition);
            fragment.setAllowEnterTransitionOverlap(false);
            fragment.setAllowReturnTransitionOverlap(false);
            fragment.setSharedElementEnterTransition(changeBoundsTransition);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView img;
        public MyViewHolder(View itemView) {
            super(itemView);
            nameTxt= (TextView) itemView.findViewById(R.id.tv);
            img= (ImageView) itemView.findViewById(R.id.iv);
        }
    }
}
