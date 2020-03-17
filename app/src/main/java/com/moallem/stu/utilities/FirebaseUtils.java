package com.moallem.stu.utilities;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.moallem.stu.data.PrefsHelper;

public class FirebaseUtils {

    public interface Action{
        void onCompleteListener();
    }

    private static final String TAG = "FirebaseUtilsclass";

    public static void setUerType(Context context,String userid,Action action){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = root.child("teachers").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    PrefsHelper.getInstance(context).setUserType("teacher");
                    Toast.makeText(context, "Please use teacher app", Toast.LENGTH_SHORT).show();

                }else {
                    PrefsHelper.getInstance(context).setUserType("student");
                    action.onCompleteListener();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
