//package Singletons;
//
//import android.app.Activity;
//import android.content.Context;
//import android.widget.EditText;
//
//import theperfectfit.sizedoesmatter.R;
//
//public class Helper {
//
//    private Helper(){}
//
//    static double getScaleHeight(Activity a){
//        EditText text = (EditText) a.findViewById(R.id.HeightText);
//
//        if(text == null) return -1;
//
//        String t = text.getText().toString();
//        double d = -1;
//
//        try{
//            d = Double.parseDouble(text.getText().toString());
//        } catch (NumberFormatException e){
//            System.err.println("Scale height number format error.  Value: " + t);
//        }
//
//        return d;
//    }
//
//    static double getScaleWidth(Activity a){
//        EditText text = (EditText) a.findViewById(R.id.WidthText);
//
//        if(text == null) return -1;
//
//        String t = text.getText().toString();
//        double d = -1;
//
//        try{
//            d = Double.parseDouble(text.getText().toString());
//        } catch (NumberFormatException e){
//            System.err.println("Scale width number format error.  Value: " + t);
//        }
//
//        return d;
//    }
//
//
//
//}
