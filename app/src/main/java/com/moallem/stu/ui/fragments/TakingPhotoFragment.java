package com.moallem.stu.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.moallem.stu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakingPhotoFragment extends Fragment {


    ImageView imageView;
    public TakingPhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taking_photo, container, false);
        imageView = view.findViewById(R.id.quick_start_cropped_image);
        cropAndRotateImage();
        return view;
    }

    public void cropAndRotateImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(400, 400)
                .setCropMenuCropButtonIcon(R.drawable.ic_done)
                .start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {

                 goToFregment(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), R.string.cropping_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToFregment(Uri uri) {
        SendingQuestionFragment sendingQuestionFragment = new SendingQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("imageUri",uri.toString());
        sendingQuestionFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction().add(R.id.fpostquestion, sendingQuestionFragment)
                .commit();
    }
}
