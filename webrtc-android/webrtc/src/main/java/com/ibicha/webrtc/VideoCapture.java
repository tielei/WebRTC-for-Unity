package com.ibicha.webrtc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.Logging;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Created by bhadriche on 8/15/2017.
 */

public class VideoCapture implements ActivityResultHelper.ActivityResultListener {
    private static final String TAG = VideoCapture.class.getSimpleName();

    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;

    private static VideoCapture _instance;

    public static VideoCapture getInstance() {
        if (_instance == null) {
            _instance = new VideoCapture();
        }
        return _instance;
    }

    private VideoCallback callback;
    private int videoWidth;
    private int videoHeight;
    private int videoFps;
    private Activity mainActivity;


    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 145;

    private VideoCapture() {
        ActivityResultHelper.addListener(this);
    }

    static void StartCameraCapture(Activity mainActivity, boolean frontCamera, VideoCallback callback) {
        StartCameraCapture(mainActivity, frontCamera, callback, 0, 0, 0);
    }


    void StartScreenCapture(Activity mainActivity, VideoCallback callback) {
        StartScreenCapture(mainActivity, callback, 0, 0, 0);
    }

    static void StartCameraCapture(Activity mainActivity, boolean frontCamera, final VideoCallback callback, int videoWidth, int videoHeight, int videoFps) {
        final VideoCapturer videoCapturer = createCameraCapturer(mainActivity.getApplicationContext(), frontCamera);

        if (videoWidth == 0 || videoHeight == 0) {
            videoWidth = HD_VIDEO_WIDTH;
            videoHeight = HD_VIDEO_HEIGHT;
        }
        if (videoFps == 0) {
            videoFps = 30;
        }

        createVideoTrack(mainActivity, videoCapturer, callback, videoWidth, videoHeight, videoFps);

    }

    void StartScreenCapture(Activity mainActivity, VideoCallback callback, int videoWidth, int videoHeight, int videoFps) {
        if (mainActivity == null) {
            callback.onVideoCapturerError("Could not get main activity.");
            return;
        }
        this.mainActivity = mainActivity;
        this.callback = callback;

        if (videoWidth == 0 || videoHeight == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager =
                    (WindowManager) mainActivity.getApplication().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        if (videoFps == 0) {
            videoFps = 30;
        }
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;

        Log.d(TAG, "Got size: " + videoWidth + "x" + videoHeight);

        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) mainActivity.getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        mainActivity.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);

    }



    private static VideoCapturer createCameraCapturer(Context context, boolean frontCamera) {
        CameraEnumerator enumerator = new Camera1Enumerator(WebRTC.HW_ACCELERATE);// new Camera2Enumerator(context);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if ((enumerator.isFrontFacing(deviceName) && frontCamera) || (enumerator.isBackFacing(deviceName) && !frontCamera)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            Logging.d(TAG, "Creating other camera capturer.");
            VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
            if (videoCapturer != null) {
                return videoCapturer;
            }

        }

        return null;
    }

    private static void createVideoTrack(Activity mainActivity, VideoCapturer videoCapturer, final VideoCallback callback, int videoWidth, int videoHeight, int videoFps) {
        VideoSource videoSource = UnityEGLUtils.getFactory(mainActivity).createVideoSource(videoCapturer);
        videoCapturer.startCapture(videoWidth, videoHeight, videoFps);
        VideoTrack videoTrack = UnityEGLUtils.getFactory(mainActivity).createVideoTrack("ARDAMSv0", videoSource);
        videoTrack.setEnabled(true);
        videoTrack.addRenderer(new VideoRenderer(new VideoRenderer.Callbacks() {
            @Override
            public void renderFrame(VideoRenderer.I420Frame i420Frame) {
                if (i420Frame.yuvFrame) {
                    throw new UnsupportedOperationException("Only texture frames.");
                }
                Log.d(TAG, "renderFrame: texture:" + i420Frame.textureId + " size:" + i420Frame.rotatedWidth() + "x" + i420Frame.rotatedHeight() +
                        " rotation:" + i420Frame.rotationDegree);
                callback.renderFrame(i420Frame.rotatedWidth(), i420Frame.rotatedHeight(), i420Frame.rotationDegree , i420Frame.textureId, i420Frame);

            }
        }));
        Log.d(TAG, "onVideoCapturerStarted");
        callback.onVideoCapturerStarted(videoCapturer, videoTrack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        if (resultCode != Activity.RESULT_OK) {
            callback.onVideoCapturerError("User didn't give permission to capture the screen.");
            return;
        }
        Log.d(TAG, "onActivityResult RESULT_OK");
        final ScreenCapturerAndroid videoCapturer = new ScreenCapturerAndroid(
                data, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                callback.onVideoCapturerStopped();
            }
        });

        createVideoTrack(mainActivity, videoCapturer, callback, videoWidth, videoHeight, videoFps);
    }
}
