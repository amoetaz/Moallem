package com.moallem.stu.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moallem.stu.R;
import com.moallem.stu.data.PrefsHelper;
import com.moallem.stu.models.Subject;
import com.moallem.stu.ui.activities.PostingQuestionActivity;
import com.moallem.stu.utilities.Utils;

import java.util.ArrayList;

import static com.moallem.stu.utilities.FirebaseConstants.USERINFO_NODE;

public class SampleRecyclerViewAdapter extends RecyclerView.Adapter<SampleViewHolders>
{
    private Context context;
    private ArrayList<Subject> subjects;

    public SampleRecyclerViewAdapter(Context context, ArrayList<Subject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public SampleViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main,parent,false);
        SampleViewHolders holder=new SampleViewHolders(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(SampleViewHolders holder, int position) {
        Subject subect = subjects.get(position);
        if (Utils.getDeviceLanguage(context).equals("arabic")) {
            holder.nameTxt.setText(subect.getArabicName());
        }else {
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
                if (PrefsHelper.getInstance(context).getUserType().equals("student")) {
                    checkBalanceAndLaunchActivity(subect);
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void checkBalanceAndLaunchActivity(Subject subect) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(USERINFO_NODE).child(Utils.getCurrentUserId())
                .child("timeBalance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Double balance = dataSnapshot.getValue(Double.class);
                    if (balance != null && balance >= 60){
                        context.startActivity(new Intent(context, PostingQuestionActivity.class)
                                .putExtra("subjectInfo",subect)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }else {
                        Toast.makeText(context, "Your balance must be more or equal 1 minutes", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "Your balance must be more or equal 1 minutes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}