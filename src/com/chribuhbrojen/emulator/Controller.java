package com.chribuhbrojen.emulator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {
	public int[] IORAM = new int[4];
	
	public void keyPressed(KeyEvent arg0) {
		
	}

	public void keyReleased(KeyEvent arg0) {
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}
	
	public int read(int address) {
		return IORAM[address & 0b11];
	}
}
