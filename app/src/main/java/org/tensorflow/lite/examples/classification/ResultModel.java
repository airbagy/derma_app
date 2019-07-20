package org.tensorflow.lite.examples.classification;

import android.graphics.Bitmap;
import java.util.Dictionary;
import android.net.Uri;
import java.lang.String;
import android.provider.MediaStore;


public class ResultModel {

    private static final ResultModel obj = new ResultModel();
    private Uri uri_org = null;
    private Uri uri_cropped = null;
    private Dictionary result = null;
    private Bitmap img_org = null;
    private Bitmap img_cropped = null;
    private Stage stage = Stage.ORIGINAL;


    public Stage returnCurState()
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        return stage;
    }

    public Bitmap getOriginalBitmap() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (obj.img_org == null) {
            IllegalStateException exp = new IllegalStateException("Original Bitmap not created");
            throw exp;
        }

        return obj.img_org;
    }

    public Bitmap getCroppedBitmap() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (obj.img_cropped == null) {
            IllegalStateException exp = new IllegalStateException("Cropped Bitmap not created");
            throw exp;
        }

        return obj.img_cropped;
    }

    public Uri getOriginalUri() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (obj.uri_org == null) {
            IllegalStateException exp = new IllegalStateException("Original Uri not created");
            throw exp;
        }

        return obj.uri_org;
    }

    public Uri getCroopedUri() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (obj.uri_cropped == null) {
            IllegalStateException exp = new IllegalStateException("Cropped Uri not created");
            throw exp;
        }

        return obj.uri_cropped;
    }

    public Stage get_Cur_State()  {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("ResultModel has not been created");
            throw exp;
        }

        return stage;
    }

    public boolean set_Cur_State(Stage stage) {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("ResultModel has not been created");
            return false;
        }
            this.stage = stage;

        return true;
    }

    public void clearAllField() throws IllegalStateException
    {
        uri_org = null;
        uri_cropped = null;
        result = null;
        img_org = null;
        img_cropped = null;
        stage = Stage.ORIGINAL;
    }

    public static ResultModel getInstance()  throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        return obj;
    }

    public boolean setOriginal(Uri org_uri, Bitmap org_img) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        try
        {
            uri_org = org_uri;
            img_org = org_img;
            return true;
        } catch (Exception e)
        {
            uri_org = null;
            img_org = null;
            stage = Stage.ORIGINAL;
            throw e;
        }
    }

    public boolean setCroppedUri(Uri cropped_uri, Bitmap cropped_img) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        try {
            uri_cropped = cropped_uri;
            img_cropped = cropped_img;
            stage = Stage.CROPPED;
            return true;
        } catch (Exception e) {
            uri_cropped = null;
            img_cropped = null;
            stage = Stage.ORIGINAL;
            throw e;
        }
    }

    public boolean setFirstStageResult(Dictionary fstage_result) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        try {
            result = fstage_result;
            stage = Stage.NV_ClASSIFIED;
            return true;
        } catch (Exception e) {
            result = null;
            stage = Stage.CROPPED;
            throw e;
        }

    }

    public boolean setSecondStageResult(Dictionary sstage_result) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        try {
            result = sstage_result;
            stage = Stage.CLASSIFIED;
            return true;
        } catch (Exception e) {
            result = null;
            stage = Stage.CROPPED;
            throw e;
        }

    }

    public Dictionary getResult() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (obj.result == null) {
            IllegalStateException exp = new IllegalStateException("Result not created");
            throw exp;
        }

        return obj.result;
    }

}
