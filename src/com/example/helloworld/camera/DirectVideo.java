
package com.example.helloworld.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

public class DirectVideo {

    private static final String TAG = "DirectVideo";

    private final String vertexShaderCode = "attribute vec4 position;"
            + "attribute vec2 inputTextureCoordinate;" + "uniform mat4 transformMatrix ;"
            + "varying vec2 textureCoordinate;" + "void main()" + "{" + "gl_Position = position;"
            + "textureCoordinate = (transformMatrix*vec4(inputTextureCoordinate,0.0,1.0)).xy;" +
            // "textureCoordinate = inputTextureCoordinate;" +
            "}";

    private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;"
            + "varying vec2 textureCoordinate;                            \n"
            + "uniform samplerExternalOES s_texture;               \n" + "void main() {"
            + "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" + "}";

    // vertex position buffer
    private FloatBuffer vertexBuffer;

    // vertex texture coord buffer
    private FloatBuffer textureVerticesBuffer;

    // vertex index buffer for glDrawElements
    private ShortBuffer drawListBuffer;

    // for vertex shader and fragment shader
    private final int mProgram;

    private int mPositionHandle;

    private int mColorHandle;

    private int mTextureCoordHandle;

    private int mTranformMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;

    static float squareVertices[] = { // in counterclockwise order:
            -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f
    };

    private short drawOrder[] = {
            0, 1, 2, 2, 3, 0
    }; // order to draw vertices

    static float textureVertices[] = { // in counterclockwise order:
            0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    };

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
                                                            // vertex

    private int texture;

    public DirectVideo(int _texture) {
        texture = _texture;

        ByteBuffer bb = ByteBuffer.allocateDirect(squareVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareVertices);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
                                                       // to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
                                                         // shader to program
        GLES20.glLinkProgram(mProgram);

        init();
    }

    private void init() {
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

        // move from Render
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, vertexStride, textureVerticesBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        GLES20.glUniform1i(mColorHandle, 0);

        mTranformMatrixHandle = GLES20.glGetUniformLocation(mProgram, "transformMatrix");

    }

    public void draw(float[] mtx) {
        Log.i(TAG, "Enter------------------------draw()");

        ByteBuffer bf = ByteBuffer.allocateDirect(mtx.length * 4);
        bf.order(ByteOrder.nativeOrder());
        FloatBuffer mtxBuffer = bf.asFloatBuffer();
        mtxBuffer.put(mtx);
        mtxBuffer.position(0);

        GLES20.glUniformMatrix4fv(mTranformMatrixHandle, 1, false, mtxBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer);

        Log.i(TAG, "Leave------------------------draw()");
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
