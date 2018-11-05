package com.tutorials.camera.ui.fragments.gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.tutorials.camera.R;
import com.tutorials.camera.models.Picture;
import com.tutorials.camera.ui.fragments._BaseFragment;

public class PhotoViewerFragment extends _BaseFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_gallery_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()==null || getActivity()==null)
            return;
        Picture picture = (Picture) getArguments().getSerializable("picture");
        if(picture==null)
            return;

        SubsamplingScaleImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImage(ImageSource.uri(picture.getPicturePath()));

        /*Matrix m = imageView.getImageMatrix();
        RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
        RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
        imageView.setImageMatrix(m);*/

        /*PhotoView photoView = view.findViewById(R.id.photoView);
        Glide
                .with(getActivity())
                .load(Uri.fromFile(new File(picture.getPicturePath())))
                .into(photoView);*/
    }
}
