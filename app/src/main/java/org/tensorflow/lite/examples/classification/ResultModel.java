package org.tensorflow.lite.examples.classification;

import android.graphics.Bitmap;

import java.util.Dictionary;
import android.net.Uri;

public class ResultModel {

    private static ResultModel obj;
    private Uri uri_org = null;
    private Uri uri_cropped = null;
    private Dictionary result = null;
    private Bitmap image_org = null;
    private Bitmap image_cropped = null;
    private boolean cropped = false;
    private boolean first_stage = false;
    private boolean second_stage = false;


    private ResultModel() {


    }

    public static ResultModel getInstance()
    {
        if (obj==null)
            obj = new ResultModel();
        return obj;
    }



}
