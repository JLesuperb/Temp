package com.tutorials.scanner;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Map;

public class ScanFragment extends Fragment
{
    // ===========================================================
    // Constants
    // ===========================================================

    public static final String RESULT_IMAGE_PATH = "imgPath";

    private static final int TAKE_PHOTO_REQUEST_CODE = 815;
    private static final String SAVED_ARG_TAKEN_PHOTO_LOCATION = "taken_photo_loc";

    private static final int MODE_NONE = 0;
    private static final int MODE_BLACK_AND_WHITE = 1;
    private static final int MODE_MAGIC = 2;

    // ===========================================================
    // Fields
    // ===========================================================

    private ViewHolder viewHolder = new ViewHolder();
    private ProgressDialogFragment progressDialogFragment;

    private String takenPhotoLocation;
    private Bitmap takenPhotoBitmap;
    private Bitmap documentBitmap;
    private Bitmap documentColoredBitmap;

    private Map<Integer, PointF> points;

    private boolean isCropMode = false;
    private int currentMode = MODE_MAGIC;

    private int previousOreantation = -1;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getters & Setters
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    private static class ViewHolder {
        /*private ImageView sourceImageView;
        private ScaleImageView scaleImageView;
        private FrameLayout sourceFrame;
        private PolygonView polygonView;*/

        void prepare(View parent) {
            /*sourceImageView = (ImageView) parent.findViewById(R.id.sourceImageView);
            scaleImageView = (ScaleImageView) parent.findViewById(R.id.scaleImage);
            sourceFrame = (FrameLayout) parent.findViewById(R.id.sourceFrame);
            polygonView = (PolygonView) parent.findViewById(R.id.polygonView);*/
        }
    }
}
