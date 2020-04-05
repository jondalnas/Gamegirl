package com.chribuhbrojen.emulator;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Display extends Canvas {
	public static int WIDTH = 640, HEIGHT = 480, SCREEN_WIDTH = 3, SCREEN_HEIGHT = 2;
	
	private int rowRegister;
	private int[] screen;
	private int[] pixels;
	private BufferedImage img;
	
	public Display() {
		screen = new int[8*SCREEN_WIDTH*SCREEN_HEIGHT];
		
		img = new BufferedImage(SCREEN_WIDTH * 8, SCREEN_HEIGHT * 8, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		setSize(WIDTH, HEIGHT);
	}
	
	public void write(int address, int data) {
		if ((address & 1) == 0) {
			rowRegister = data;
		} else {
			screen[rowRegister] = data;
		}
	}
	
	public void render() {
		//Setup
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (int row = 0; row < screen.length; row++) {
			for (int col = 0; col < 8; col++) {
				int color = ((screen[row] >> col) & 1) * 0xFFFFFF;
				
				pixels[(col + ((row % 3) * 8)) + (row / 3) * SCREEN_WIDTH * 8] = color;
			}
		}

		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
		g.dispose();
		bs.show();
	}
}
