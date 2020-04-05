package com.chribuhbrojen.emulator;

public class Memory {
	public int[] ROM = new int[0x2000];
	public int[] RAM = new int[0x0800];
	
	public Memory() {
		//INIT ROW
		ROM[0] = 0xA9;
		ROM[1] = 0x00;
		ROM[2] = 0x8D;
		ROM[3] = 0x00;
		ROM[4] = 0x18;

		//INIT DATA
		ROM[5] = 0xA9;
		ROM[6] = 0x00;
		ROM[7] = 0x8D;
		ROM[8] = 0x01;
		ROM[9] = 0x18;
		//ROM[10] = 0x38;
		
		//LOOP
		ROM[10] = 0x69;
		ROM[11] = 0x01;
		ROM[12] = 0x8D;
		ROM[13] = 0x01;
		ROM[14] = 0x18;

		for (int i = 0; i < 60; i++) ROM[15 + i] = 0xFA;
		
		ROM[74] = 0x4C;
		ROM[75] = 0x0A;
		ROM[76] = 0x20;
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
}
