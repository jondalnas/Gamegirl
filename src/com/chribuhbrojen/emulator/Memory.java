package com.chribuhbrojen.emulator;

import java.util.Scanner;

public class Memory {
	public int[] ROM = new int[0x2000];
	public int[] RAM = new int[0x0800];
	
	public Memory() {
		loadCode("/code.bin");
	}
	
	public void write(int address, int data) {
		RAM[address] = data;
	}
	
	public int read(int address) {
		if ((address & 0x2000) == 0) {
			return RAM[address];
		} else {
			return ROM[address & 0x1FFF];
		}
	}
	
	private void loadCode(String file) {
		Scanner scan = new Scanner(Memory.class.getResourceAsStream(file));
		
		int ptr = 0;
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			
			for (String integer : line.split(" ")) {
				ROM[ptr++] = Integer.parseInt(integer, 16);
			}
		}
	}
}
