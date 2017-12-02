package com.azizinetwork.hajde.newpost;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.settings.CameraUtil;
import com.azizinetwork.hajde.settings.Global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewImageActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private final static String TAG = "NewImageActivity";
    private Camera mCamera;
    private int cameraPosition = 0;
    SurfaceHolder holder;

    SurfaceView mSurfaceView;
    ImageButton openLight;
    ImageView img_lib;
    View focusIndex;
    View bootomRly;

    private float pointX, pointY;
    static final int FOCUS = 1;
    static final int ZOOM = 2;
    private int mode;

    int curZoomValue = 0;
    private float dist;
    Camera.Parameters parameters;
    private Handler handler = new Handler();
    boolean safeToTakePicture = true;

    private final static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);

        CameraUtil.init(this);
        initView();
        initData();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceView);
        openLight = (ImageButton) findViewById(R.id.openLight);
        focusIndex = findViewById(R.id.focus_index);
        bootomRly = findViewById(R.id.bootomRly);

        Button btn_back = (Button) findViewById(R.id.back);
        img_lib = (ImageView) findViewById(R.id.lookPictureIv);
        Button btn_camera = (Button) findViewById(R.id.takePhoto);
        ImageButton btn_switch = (ImageButton) findViewById(R.id.cameraSwitch);

        if (Global.g_bitmap != null) {
            img_lib.setImageBitmap(Global.g_bitmap);
        }

        btn_back.setOnClickListener(this);
        img_lib.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        openLight.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
    }

    protected void initData() {
        holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);

        bootomRly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        mode = FOCUS;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        if (spacing(event) > 10f) {
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = FOCUS;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == FOCUS) {
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float tScale = (newDist - dist) / dist;
                                if (tScale < 0) {
                                    tScale = tScale * 10;
                                }
                                addZoomIn((int) tScale);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pointFocus((int) pointX, (int) pointY);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
                layout.setMargins((int) pointX - 60, (int) pointY - 60, 0, 0);
                focusIndex.setLayoutParams(layout);
                focusIndex.setVisibility(View.VISIBLE);
                ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(800);
                focusIndex.startAnimation(sa);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusIndex.setVisibility(View.INVISIBLE);
                    }
                }, 700);
            }
        });
    }

    private void addZoomIn(int delta) {
        try {
            Camera.Parameters params = mCamera.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }
            curZoomValue += delta;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                mCamera.setParameters(params);
                return;
            } else {
                mCamera.startSmoothZoom(curZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pointFocus(int x, int y) {
        mCamera.cancelAutoFocus();
        parameters = mCamera.getParameters();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        mCamera.setParameters(parameters);
        autoFocus();
    }

    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void showPoint(int x, int y) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int rectY = -x * 2000 / CameraUtil.screenWidth + 1000;
            int rectX = y * 2000 / CameraUtil.screenHeight - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            parameters.setMeteringAreas(areas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mCamera == null) {
                    return;
                }
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            setupCamera(camera);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
        autoFocus();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(cameraPosition);
            if (holder != null) {
                startPreview(mCamera, holder);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void turnLight(Camera mCamera) {
        if (mCamera == null || mCamera.getParameters() == null
                || mCamera.getParameters().getSupportedFlashModes() == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = mCamera.getParameters().getFlashMode();
        List<String> supportedModes = mCamera.getParameters().getSupportedFlashModes();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(parameters);
//            openLight.setImageResource(R.mipmap.camera_flash_on);
            openLight.setBackgroundResource(R.mipmap.camera_flash_on);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//                openLight.setImageResource(R.mipmap.camera_flash_auto);
                openLight.setBackgroundResource(R.mipmap.camera_flash_auto);
                mCamera.setParameters(parameters);
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                openLight.setImageResource(R.mipmap.camera_flash_off);
                openLight.setBackgroundResource(R.mipmap.camera_flash_off);
                mCamera.setParameters(parameters);
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
//            openLight.setImageResource(R.mipmap.camera_flash_off);
            openLight.setBackgroundResource(R.mipmap.camera_flash_off);
        }
    }

    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {

        }
        return camera;
    }

    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, cameraPosition, camera);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        Camera.Size previewSize = CameraUtil.findBestPreviewResolution(camera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 1000);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);

        int picHeight = CameraUtil.screenWidth * previewSize.width / previewSize.height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(CameraUtil.screenWidth, picHeight);
        mSurfaceView.setLayoutParams(params);

    }

    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    final private Camera.PictureCallback mJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }).start();

            Log.v(TAG, "======================= get Camera data ===========================");

            mCamera.stopPreview();
            mCamera.startPreview();

            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                switch (cameraPosition) {
                    case 0:
                        matrix.preRotate(90);
                        break;
                    case 1:
                        float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
                        matrix.setValues(mirrorY);
                        matrix.preRotate(-90);
                        break;
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Global.g_bitmap = bitmap;
                img_lib.setImageBitmap(Global.g_bitmap);

                Global.g_cameraIsOn = true;

                int REQUEST_CODE = 123;
                Intent i = new Intent(NewImageActivity.this, ImageEditActivity.class);
                startActivityForResult(i, REQUEST_CODE);

            } catch (Exception e) {
//                        e.printStackTrace();
            }
            safeToTakePicture = true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Global.g_bitmap = BitmapFactory.decodeFile(picturePath);
            img_lib.setImageBitmap(Global.g_bitmap);

            Global.g_cameraIsOn = false;

            int REQUEST_CODE = 123;
            Intent i = new Intent(NewImageActivity.this, ImageEditActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        } else if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.lookPictureIv:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.takePhoto:
                if (safeToTakePicture) {
                    safeToTakePicture = false;
//                    mCamera.takePicture(null, null, mJpeg);
                    mCamera.takePicture(shutterCallback, null, mJpeg);
                }
                break;
            case R.id.openLight:
                turnLight(mCamera);
                break;
            case R.id.cameraSwitch:
                releaseCamera();
                cameraPosition = (cameraPosition + 1) % mCamera.getNumberOfCameras();
                mCamera = getCamera(cameraPosition);
                if (holder != null) {
                    startPreview(mCamera, holder);
                }
                break;
        }

    }
}
