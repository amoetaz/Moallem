package com.moallem.stu.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.moallem.stu.R;
import com.moallem.stu.models.Session;
import com.moallem.stu.ui.activities.ChattingActivity;
import com.moallem.stu.ui.fragments.ViewQuestionFragment;

import java.util.ArrayList;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.MyViewHolder> {

    private static final String TAG = "SessionsAdapterclass";
    private Context context;
    private ArrayList<Session> sessions = new ArrayList<>();

    public SessionsAdapter(Context context, ArrayList<Session> sessions) {
        this.context = context;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row,parent,false);
        return new SessionsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Session session = sessions.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(40));
        Glide.with(context).load(session.getFirstPic()).apply(requestOptions).into(holder.image);
        updateUI(session,holder);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getReplyed()){
                    Intent intent = new Intent(context, ChattingActivity.class);
                    intent.putExtra("session_extra",session);
                    context.startActivity(intent);
                }
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewQuestionFragment viewQuestionFragment = new ViewQuestionFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("session_data",session);
                viewQuestionFragment.setArguments(bundle);

                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fsessios,viewQuestionFragment).addToBackStack(null).commit();
            }
        });

    }

    private void updateUI(Session session, MyViewHolder holder) {
        if (session.getTeacherPic() != null){
            Glide.with(context).load(session.getTeacherPic()).into(holder.teacherImage);
        }else {
            holder.teacherImage.setImageResource(R.drawable.ic_face);
        }
        if (session.getTeacherName() != null){
            holder.teacherName.setText(session.getTeacherName());
        }
        if (session.getDate() != null){
            holder.date.setText(session.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image,teacherImage;
        Button view;
        TextView teacherName, date;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_qestion_row);
            view = itemView.findViewById(R.id.view_question_row);
            teacherName = itemView.findViewById(R.id.teacher_name);
            date = itemView.findViewById(R.id.question_date);
            teacherImage = itemView.findViewById(R.id.teacher_image_qestion_row);
        }
    }
}



