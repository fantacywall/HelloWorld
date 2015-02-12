
package com.example.helloworld.codec;

import java.nio.ByteBuffer;  

import android.annotation.SuppressLint;  
import android.media.MediaCodec;  
import android.media.MediaCodecInfo;  
import android.media.MediaCodecList;
import android.media.MediaFormat;  
import android.util.Log;  
  
  
  
public class AvcEncoder   
{  
  
	private static final String MIME_TYPE = "video/avc"; 
	
    private MediaCodec mediaCodec;  
    int m_width;  
    int m_height;  
    byte[] m_info = null;  
    
    private static String TAG = "AvcEncoder";
  
    private byte[] yuv420 = null;
	private FrameListener frameListener;   
	
	private  static final  int TIMEOUT_USEC = 10000;
	ByteBuffer[] inputBuffers;
	ByteBuffer[] outputBuffers;
	
	private long startMs;
	
    @SuppressLint("NewApi")  
    public AvcEncoder(int width, int height, int framerate, int bitrate) {   
          
    	Log.d(TAG,"in constructor, height is: " + height 
    			+ " width is: " + width
    			+ " framerate is: " + framerate
    			+ " bitrate is: " + bitrate);
    	
        m_width  = width;  
        m_height = height;  
        yuv420 = new byte[width*height*3/2];  
      
        mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);  
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height);  
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);  
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);  
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);    
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5); //关键帧间隔时间 单位s  
          
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);  
        mediaCodec.start();  
        startMs =System.currentTimeMillis() ;
//        listAllEncoders();
        
        inputBuffers = mediaCodec.getInputBuffers();  
        outputBuffers = mediaCodec.getOutputBuffers();  
        bufferInfo = new MediaCodec.BufferInfo(); 
        Log.d(TAG,"finished constructor");
    }  
    
    
    private void listAllEncoders()
    {
    	int n = MediaCodecList.getCodecCount();
    	for (int i = 0; i < n; ++i) {
    		MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
    		Log.d(TAG,"codec name is: " + info.getName());
    	}
    }
    
    
    
    @SuppressLint("NewApi")  
    public void close() {  
        try {  
            mediaCodec.stop();  
            mediaCodec.release();  
        } catch (Exception e){   
            e.printStackTrace();  
        }  
    }  
    
    MediaCodec.BufferInfo bufferInfo = null;
    
    public synchronized  void onFrame(byte[] buf, int offset, int length, int flag) 
    {  
//    	Log.d(TAG,"in AvcEncoder onFrame");	
    	
    	try{
    	
//       ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();  
//       ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();  
       int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);  
//       Log.d(TAG,"in AvcEncoder onFrame, inputBufferIndex is: " + inputBufferIndex);	
       if (inputBufferIndex >= 0)  
       {
//    	   Log.d(TAG,"inputBufferIndex >=0");	
           ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];  
           inputBuffer.clear();  
           inputBuffer.put(buf);
//           inputBuffer.put(buf, offset, length);  
//           inputBuffer.put(yuv420); 
           mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, (System.currentTimeMillis() - startMs) * 1000, 0);  
       }  
//       bufferInfo = new MediaCodec.BufferInfo();  
//       Log.d(TAG,"before dequeueOutputBuffer" );	
       int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,TIMEOUT_USEC);  
//       Log.d(TAG,"in AvcEncoder onFrame, outputBufferIndex is: " + outputBufferIndex);	
       while (outputBufferIndex >= 0) {  
    	   Log.d(TAG," A outputBufferIndex: " + outputBufferIndex );	
           ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];  
           
           byte[] outData = new byte[bufferInfo.size];
           outputBuffer.get(outData);  
//           Log.d(TAG,"in AvcEncoder onFrame,frameListener is: " + frameListener);	
           
           
//           if (frameListener != null)  
//               frameListener.onFrame(outData, 0, length, flag);  
           mediaCodec.releaseOutputBuffer(outputBufferIndex, false);  
           outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);  
           Log.d(TAG,"B outputBufferIndex: " + outputBufferIndex );	
       }   
       
    	
    } catch (Throwable t) {
        t.printStackTrace();
    }
    }
    
    @SuppressLint("NewApi")  
    public int offerEncoder(byte[] input, byte[] output)   
    {     
        int pos = 0;  
        swapYV12toI420(input, yuv420, m_width, m_height);  
        try {  
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();  
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();  
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);  
            if (inputBufferIndex >= 0)   
            {  
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];  
                inputBuffer.clear();  
                inputBuffer.put(yuv420);  
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length, 0, 0);  
            }  
  
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();  
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);  
             
            while (outputBufferIndex >= 0)   
            {  
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];  
                byte[] outData = new byte[bufferInfo.size];  
                outputBuffer.get(outData);  
                  
                if(m_info != null)  
                {                 
                    System.arraycopy(outData, 0,  output, pos, outData.length);  
                    pos += outData.length;  
                      
                }  
                  
                else //保存pps sps 只有开始时 第一个帧里有， 保存起来后面用  
                {  
                     ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);    
                     if (spsPpsBuffer.getInt() == 0x00000001)   
                     {    
                         m_info = new byte[outData.length];  
                         System.arraycopy(outData, 0, m_info, 0, outData.length);  
                     }   
                     else   
                     {    
                            return -1;  
                     }        
                }  
                  
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);  
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);  
            }  
  
            if(output[4] == 0x65) //key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上  
            {  
                System.arraycopy(output, 0,  yuv420, 0, pos);  
                System.arraycopy(m_info, 0,  output, 0, m_info.length);  
                System.arraycopy(yuv420, 0,  output, m_info.length, pos);  
                pos += m_info.length;  
            }  
              
        } catch (Throwable t) {  
            t.printStackTrace();  
        }  
  
        return pos;  
    }  
     //yv12 转 yuv420p  yvu -> yuv  
    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height)   
    {        
        System.arraycopy(yv12bytes, 0, i420bytes, 0,width*height);  
        System.arraycopy(yv12bytes, width*height+width*height/4, i420bytes, width*height,width*height/4);  
        System.arraycopy(yv12bytes, width*height, i420bytes, width*height+width*height/4,width*height/4);    
    }

	public void setFrameListener(FrameListener decoder) {
		// TODO Auto-generated method stub
		frameListener = decoder;
	}    
  
      
}  