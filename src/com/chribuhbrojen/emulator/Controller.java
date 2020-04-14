package com.chribuhbrojen.emulator;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {
	public int[] IORAM = new int[4];
	
	public Controller() {
		IORAM[0] = 0b00001111;
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			IORAM[0] = 0b00000000;
			break;
			
		case KeyEvent.VK_LEFT:
			IORAM[0] = 0b11111111;
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			if (IORAM[0] == 0b00000000)
				IORAM[0] = 0b00001111;
			
			break;
			
		case KeyEvent.VK_LEFT:
			if (IORAM[0] == 0b11111111)
				IORAM[0] = 0b00001111;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	public int read(int address) {
		return IORAM[address & 0b11];
	}
}
