package com.derma.app.classifier;

import android.graphics.Bitmap;
import android.net.Uri;
import java.lang.String;
import java.util.Map;


public class ResultModel {

    private static final ResultModel obj = new ResultModel();
    private Uri uri_org = null;
    private Uri uri_cropped = null;
    private Map<String, Float> nvResult = null;
    private Map<String, Float> cancerResult = null;
    private Bitmap img_org = null;
    private Bitmap img_cropped = null;
    private Stage stage = Stage.ORIGINAL;
    private Uri resultImageUri = null;

    public Stage getStage()
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        return stage;
    }

    public Bitmap getImg_org() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (img_org == null) {
            IllegalStateException exp = new IllegalStateException("Original Bitmap not created");
            throw exp;
        }

        return img_org;
    }

    public Bitmap getImg_cropped() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (img_cropped == null) {
            IllegalStateException exp = new IllegalStateException("Cropped Bitmap not created");
            throw exp;
        }

        return img_cropped;
    }

    public Uri getUri_org() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (uri_org == null) {
            IllegalStateException exp = new IllegalStateException("Original Uri not created");
            throw exp;
        }

        return uri_org;
    }

    public Uri getUri_cropped() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        if (uri_cropped == null) {
            IllegalStateException exp = new IllegalStateException("Cropped Uri not created");
            throw exp;
        }

        return uri_cropped;
    }

    public static ResultModel getInstance() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }

        return obj;
    }

    public void setImg_org(Bitmap img_org) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        try{
            this.img_org = img_org;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setUri_org(Uri uri_org) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        try{
            this.uri_org = uri_org;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setImg_cropped(Bitmap img_cropped) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            return;
        }
        try{
            this.img_cropped = img_cropped;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setUri_cropped(Uri uri_cropped) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            return;
        }
        try{
            this.uri_cropped = uri_cropped;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setStage(Stage stage) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        try {
            this.stage = stage;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void setNvResult(Map<String, Float> nvResult) throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        try{
            this.nvResult = nvResult;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public Map<String, Float> getNvResult() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        return nvResult;
    }

    public void setCancerResult(Map<String, Float> cancerResult) throws IllegalStateException {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        try {
            this.cancerResult = cancerResult;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public Map<String, Float> getCancerResult() throws IllegalStateException
    {
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        return cancerResult;
    }

    public void setResultImageUri(Uri uri){
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        resultImageUri = uri;
    }

    public Uri getResultImageUri(){
        if (obj == null) {
            IllegalStateException exp = new IllegalStateException("Instance of ResultModel has not been created");
            throw exp;
        }
        return resultImageUri;
    }

    public void clearResultModel() throws IllegalStateException
    {
        uri_org = null;
        uri_cropped = null;
        nvResult = null;
        cancerResult = null;
        img_org = null;
        img_cropped = null;
        stage = Stage.ORIGINAL;
        resultImageUri = null;
    }
}
