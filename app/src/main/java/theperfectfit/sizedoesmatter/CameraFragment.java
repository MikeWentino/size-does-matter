package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.graphics.PixelFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import Singletons.CurrentImage;

//Fragment class which implements camera preview and picture taking
public class CameraFragment extends android.support.v4.app.Fragment {
    private Preview mPreview;
    Camera mCamera;
    ImageButton mButton;
    View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = new View(this.getActivity());
    }

    //creates view and button for takePicture listener on camera
    //returns bitmap of captured image to MainActivity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(this.getActivity());
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);

        View endV = inflater.inflate(R.layout.c_frag, relativeLayout, false);
        mButton = (ImageButton) endV.findViewById(R.id.take_button);
        mPreview = (Preview) endV.findViewById(R.id.camView);
        mCamera = mPreview.getCamera();
        mButton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           PictureCallback mPicture = new PictureCallback() {
                                               @Override
                                               public void onPictureTaken(byte[] data, Camera camera) {

                                                   ByteArrayInputStream stream = new ByteArrayInputStream(data);
                                                   Bitmap bitmap = BitmapFactory.decodeStream(stream);
                                                   CurrentImage.getInstance().setBit(bitmap);
                                                   System.out.println("Width is " + bitmap.getWidth());
                                                   android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                                   fragmentManager.popBackStack();
                                               }
                                           };
                                           mCamera.takePicture(null, null, mPicture);
                                       }
                                   }
        );
        return endV;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

}

//Class for preview which extends ViewGroup
class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    Button mButton;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    boolean mSurfaceCreated = false;

    //preview constructors which extablish parameters for layout of camera preview
    public Preview(Context context) {
        super(context);
        mSurfaceView = new SurfaceView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addViewInLayout(mSurfaceView, 0, lp);
        setCamera(Camera.open(CameraInfo.CAMERA_FACING_BACK));
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceView = new SurfaceView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addViewInLayout(mSurfaceView, 0, lp);
        setCamera(Camera.open(CameraInfo.CAMERA_FACING_BACK));
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public Preview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSurfaceView = new SurfaceView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addViewInLayout(mSurfaceView, 0, lp);
        setCamera(Camera.open(CameraInfo.CAMERA_FACING_BACK));
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            if (mSurfaceCreated) requestLayout();
        }
    }

    //takes measurements based on camera and devices specifications
    //and sets the optimal resolution for the camera preview
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            final int width = r - l;
            final int height = b - t;
            child.layout(0, 0,
                    width, height);
        }
    }

    //preforms setup when preview is first created
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        if (mPreviewSize == null) requestLayout();
        mSurfaceCreated = true;
    }

    //destroys preview
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    //gets optimal size for camera preview based on avaliable cameras in current device
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //preforms setup for the resolution of the preview and the pictures taken by the camera
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        if (previewSizes != null) {
            Camera.Size previewSize = getOptimalPreviewSize(previewSizes, w, h);
            Camera.Size pictureSize = getOptimalPreviewSize(pictureSizes, w, h);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            parameters.set("jpeg-quality", 70);
            parameters.setPictureFormat(PixelFormat.JPEG);
            requestLayout();
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

    public Camera getCamera() {
        return mCamera;
    }


}



