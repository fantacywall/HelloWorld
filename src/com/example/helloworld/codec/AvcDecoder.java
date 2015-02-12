package com.example.helloworld.codec;

import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

public class AvcDecoder implements FrameListener{

	
	 private MediaCodec mediaCodec;  
	    int m_width;  
	    int m_height;  
	    private static String TAG = "AvcDecoder";
	    
	    private static int mCount = 1;
	
	public AvcDecoder(int videoWidth, int videoHeight, Surface surface) {
		// TODO Auto-generated constructor stub
		
//		Log.d(TAG,"in  constructor");
		
		Log.d(TAG,"in constructor, height is: " + videoHeight 
    			+ " width is: " + videoWidth);
		
	    mediaCodec = MediaCodec.createDecoderByType("video/avc");  
	    MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", videoWidth, videoHeight);  
	    mediaCodec.configure(mediaFormat, surface, null, 0);  
	    mediaCodec.start();  
	    Log.d(TAG,"finished  constructor");
	}
	
	
	@Override
	public void onFrame(byte[] buf, int offset, int length, int flag) {
		// TODO Auto-generated method stub
		Log.d(TAG,"in AvcDecoder onFrame");
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();  
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);  
    if (inputBufferIndex >= 0) {  
        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];  
        inputBuffer.clear();  
//        inputBuffer.put(buf, offset, length);  
        inputBuffer.put(buf);  

        mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * 1000000 / 30, 0);  
               mCount++;  
    }  

   MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();  
   int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);  
   while (outputBufferIndex >= 0) {  
       mediaCodec.releaseOutputBuffer(outputBufferIndex, true);  
       outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);  
   }  
		
   Log.d(TAG,"finish AvcDecoder onFrame");
	}

	public void setOutSurface(Surface mOutSurface) {
		// TODO Auto-generated method stub
		
	}

}
