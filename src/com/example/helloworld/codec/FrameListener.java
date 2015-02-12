package com.example.helloworld.codec;

public interface FrameListener {
	public void onFrame(byte[] buf, int offset, int length, int flag);
}
