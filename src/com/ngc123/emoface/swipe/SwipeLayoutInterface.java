package com.ngc123.emoface.swipe;

import com.ngc123.emoface.swipe.SwipeLayout.Status;

public interface SwipeLayoutInterface {

	Status getCurrentStatus();
	
	void close();
	
	void open();
}
