package com.ibicha.webrtc.test;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by charleszhang on 22/08/2017.
 */

public class OpenGLESDrawTester {
    private static final String TAG = OpenGLESDrawTester.class.getSimpleName();

    private static final OpenGLESDrawTester instance = new OpenGLESDrawTester();
    private OpenGLESDrawTester() {}
    public static OpenGLESDrawTester getInstance() {
        return instance;
    }

    private static final String VERTEX_SHADER =
            "attribute vec4 vPosition;\n"
                    + "void main() {\n"
                    + "  gl_Position = vPosition;\n"
                    + "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n"
                    + "void main() {\n"
                    + "  gl_FragColor = vec4(1, 0, 0, 1);\n"
                    + "}";
    private final float[] VERTEX = {   // in counterclockwise order:
            0, 1, 0,  // top
            -0.5f, -1, 0,  // bottom left
            1, -1, 0,  // bottom right
    };

    private FloatBuffer mVertexBuffer;

    private int mProgram = -1;
    private int mPositionHandle;

    private int[] mFrameBuffer;
    private int[] mFrameBufferTexture;

    private boolean haveReadPixels;
    private ByteBuffer mRGBABuffer;

    /**
     * Draw a triangle with OpenGL ES 2.0
     * @param mainActivity
     * @param imgWidth
     * @param imgHeight
     * @return
     */
    public int drawTriangle(Activity mainActivity, int imgWidth, int imgHeight) {
        //will only init once
        initShaderProgram();
        initFrameBuffer(imgWidth, imgHeight);

        /**
         * The following are OpenGL ES per-frame drawing operations.
         */
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        if (!haveReadPixels) {
            mRGBABuffer = ByteBuffer.allocate(imgWidth * imgHeight * 4);
            GLES20.glReadPixels(0, 0, imgWidth, imgHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mRGBABuffer);
            haveReadPixels = true;

            //Save drawing results to SD card.
            Utils.saveRgb2Bitmap(mRGBABuffer, mainActivity.getExternalCacheDir()
                    + "/gl_drawing_dump_" + imgWidth + "_" + imgHeight + ".png", imgWidth, imgHeight);
        }


        return mFrameBufferTexture[0];
    }

    private void initShaderProgram() {
        if (mProgram == -1) {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);

            mProgram = GLES20.glCreateProgram();
            Log.d(TAG, "Created a GL program with programID: " + mProgram);
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);

            GLES20.glUseProgram(mProgram);

            mPositionHandle = 5;
            GLES20.glBindAttribLocation(mProgram, mPositionHandle, "vPosition");
            GLES20.glLinkProgram(mProgram);

            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    12, mVertexBuffer);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        }
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private void initFrameBuffer(int width, int height) {
        //TODO: I do not care about resize right now.
        if (mFrameBuffer == null) {
            mFrameBuffer = new int[1];
            mFrameBufferTexture = new int[1];

            GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
            GLES20.glGenTextures(1, mFrameBufferTexture, 0);

            bindFrameBuffer(mFrameBufferTexture[0], mFrameBuffer[0], width, height);

            Log.d(TAG, "Created a frame buffer and a associative texture: " + width + "x" + height);
        }
    }

    private void bindFrameBuffer(int textureId, int frameBuffer, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

}
