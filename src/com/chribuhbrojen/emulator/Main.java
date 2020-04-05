package com.chribuhbrojen.emulator;

import java.awt.Panel;
import java.util.Stack;

import javax.swing.JFrame;

public class Main implements Runnable {
	public static Display display;
	public static Controller controller;
	public static Memory memory;
	
	public Main() {
		display = new Display();
		controller = new Controller();
		memory = new Memory();
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		
		Panel panel = new Panel();
		JFrame jf = new JFrame("Gamegirl Emulator");
		panel.add(Main.display);
		
		jf.setContentPane(panel);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setResizable(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		new Thread(main).run();
	}
	
	class Flags {
		boolean c, z, i, d, b, v, n;

		public int toInt() {
			return 	(c ? 0b00000001 : 0) | 
					(z ? 0b00000010 : 0) | 
					(i ? 0b00000100 : 0) | 
					(d ? 0b00001000 : 0) | 
					(b ? 0b00010000 : 0) | 
					(v ? 0b00100000 : 0) | 
					(n ? 0b01000000 : 0);
		}
		
		public void set(int integer) {
			c = (integer & 0b00000001) == 1;
			z = (integer & 0b00000010) == 1;
			i = (integer & 0b00000100) == 1;
			d = (integer & 0b00001000) == 1;
			b = (integer & 0b00010000) == 1;
			v = (integer & 0b00100000) == 1;
			n = (integer & 0b01000000) == 1;
		}
	}

	private int pc = 0x2000, x, y, acc;
	private int reg[] = new int[16];
	private Stack<Integer> stack = new Stack<Integer>();
	private Flags flags;
	public void run() {
		flags = new Flags();
		
		long nano = System.nanoTime();
		long nanoSleep = System.nanoTime();
		while(true) {
			int instr = read(pc++);
			System.out.println(Integer.toHexString(instr));

			switch(instr) {
			case 0x00: //BRK impl
				while(true);
				//while(!CONT); TODO: Add debugging
				
			case 0x01: //ORA X,ind
				acc |= readXind();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x05: //ORA zpg
				acc |= readZpg();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x06: //ASL zpg
				reg[0] = readZpg();
				reg[1] = reg[0] << 1;
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x08: //PHP impl
				stack.push(flags.toInt());
				
				break;
			case 0x09: //ORA #
				acc |= read(pc++);
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x0A: //ASL A
				acc <<= 1;
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.c = (reg[0] & 0b10000000) != 0;
				break;
			case 0x0D: //ORA abs
				acc |= readAbs();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x0E: //ASL abs
				reg[0] = readAbs();
				reg[1] = reg[0] << 1;
				write(-1, reg[1]);
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.c = (reg[0] & 0b10000000) != 0;
				break;
			case 0x10: //BPL rel
				if (!flags.n) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0x11: //ORA ind,Y
				acc |= readIndy();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x15: //ORA zpg,X
				acc |= readZpgx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x16: //ASL zpg,X
				reg[0] = readZpgx();
				reg[1] = reg[0] << 1;
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x18: //CLC impl
				flags.c = false;
				
				break;
			case 0x19: //ORA abs,Y
				acc |= readAbsy();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x1D: //ORA abs,X
				acc |= readAbsx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x1E: //ASL abs,X
				reg[0] = readAbsx();
				reg[1] = reg[0] << 1;
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x20: //JSR abs
				stack.push(pc);
				pc = abs();
				
				break;
			case 0x21: //AND X,ind
				acc &= readXind();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x24: //BIT zpg
				reg[0] = readZpg();

				flags.z = (reg[0] & acc) == 0;
				flags.v = (reg[0] & 0b01000000) != 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x25: //AND zpg
				reg[0] = readZpg();
				acc &= reg[0];
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x26: //ROL zpg
				reg[0] = readZpg();
				reg[1] = (reg[0] << 1) | (flags.c ? 1 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x28: //PLP impl
				flags.set(stack.pop());
				
				break;
			case 0x29: //AND #
				acc &= read(pc++);
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x2A: //ROL A
				reg[0] = acc;
				acc = (reg[0] << 1) | (flags.c ? 1 : 0);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x2C: //BIT abs
				reg[0] = readAbs();

				flags.z = (reg[0] & acc) == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				flags.v = (reg[0] & 0b01000000) != 0;
				break;
			case 0x2D: //AND abs
				acc &= readAbs();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x2E: //ROL abs
				reg[0] = readAbs();
				reg[1] = (reg[0] << 1) | (flags.c ? 1 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x30: //BMI rel
				if (flags.n) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0x31: //AND X,ind
				acc &= readXind();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x35: //AND zpg,X
				acc &= readZpgx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x36: //ROL zpg,X
				reg[0] = readZpgx();
				reg[1] = (reg[0] << 1) | (flags.c ? 1 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x38: //SEC impl
				flags.c = true;
				
				break;
			case 0x39: //AND abs,Y
				acc &= readAbsy();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x3D: //AND abs,X
				acc &= readAbsx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x3E: //ROL abs,X
				reg[0] = readAbsx();
				reg[1] = (reg[0] << 1) | (flags.c ? 1 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b10000000) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x40: //RTI impl
				//??????
				
				break;
			case 0x41: //EOR X,ind
				acc ^= readXind();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x45: //EOR zpg
				acc ^= readZpg();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x46: //LSR zpg
				reg[0] = readZpg();
				reg[1] = reg[0] >> 1;
				write(-1, reg[1]);
				
				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x48: //PHA impl
				stack.push(acc);
				
				break;
			case 0x49: //EOR #
				acc ^= read(pc++);
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x4A: //LSR A
				reg[0] = acc;
				acc = reg[0] >> 1;
				
				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x4C: //JMP abs
				pc = abs();
				
				break;
			case 0x4D: //EOR abs
				acc ^= readAbs();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x4E: //LSR abs
				reg[0] = readAbs();
				reg[1] = reg[0] >> 1;
				write(-1, reg[1]);
				
				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x50: //BVC rel
				if (!flags.v) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0x51: //EOR ind,Y
				acc ^= readIndy();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x55: //EOR zpg,X
				acc ^= readZpgx();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x56: //LSR zpg,X
				reg[0] = readZpgx();
				reg[1] = reg[0] >> 1;
				write(-1, reg[1]);
				
				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x58: //CLI impl
				flags.i = false;
				
				break;
			case 0x59: //EOR abs,Y
				acc ^= readAbsy();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x5D: //EOR abs,X
				acc ^= readAbsx();
				acc &= 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x5E: //LSR abs,X
				reg[0] = readAbsx();
				reg[1] = reg[0] >> 1;
				write(-1, reg[1]);
				
				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[1] == 0;
				flags.n = (reg[1] & 0b10000000) != 0;
				break;
			case 0x60: //RTS impl
				pc = stack.pop() + 1;
				
				break;
			case 0x61: //ADC X,ind
				reg[0] = readXind();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2] & 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x65: //ADC zpg
				reg[0] = readZpg();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2] & 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x66: //ROR zpg
				reg[0] = readZpg();
				reg[1] = (reg[0] >> 1) | (flags.c ? 0b10000000 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x68: //PLA impl
				acc = stack.pop();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x69: //ADC #
				reg[0] = read(pc++);
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2];
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x6A: //ROR A
				reg[0] = acc;
				acc = reg[0] >> 1 | (flags.c ? 0b10000000 : 0);

				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x6C: //JMP ind
				reg[0] = read(pc++);
				reg[1] = reg[0] | (read(pc++) << 8);
				pc = read(reg[1]) | (read(reg[1] + 1) << 8);
				
				break;
			case 0x6D: //ADC abs
				reg[0] = readAbs();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2] & 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x6E: //ROR abs
				reg[0] = readAbs();
				reg[1] = reg[0] >> 1 | (flags.c ? 0b10000000 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x70: //BVS rel
				if (flags.v) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0x71: //ADC ind,Y
				reg[0] = readIndy();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2] & 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x75: //ADC zpg,X
				reg[0] = readZpgx();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2] & 0b11111111;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x76: //ROR zpg,X
				reg[0] = readZpgx();
				acc = reg[0] >> 1 | (flags.c ? 1 : 0);

				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x78: //SEI impl
				flags.i = true;
				
				break;
			case 0x79: //ADC abs,Y
				reg[0] = readAbsy();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2];
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x7D: //ADC abs,X
				reg[0] = readAbsx();
				reg[1] = acc;
				reg[2] = reg[1] + reg[0] + (flags.c ? 1 : 0);
				acc = reg[2];
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				flags.v = (((reg[0] | reg[1]) ^ acc) & 0b10000000) != 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x7E: //ROR abs,X
				reg[0] = readAbsx();
				reg[1] = (reg[0] >> 1) | (flags.c ? 0b10000000 : 0);
				write(-1, reg[1]);

				flags.c = (reg[0] & 0b1) != 0;
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0x81: //STA X,ind
				writeXind(acc);
				
				break;
			case 0x84: //STY zpg
				writeZpg(y);
				
				break;
			case 0x85: //STA zpg
				writeZpg(acc);
				
				break;
			case 0x86: //STX zpg
				writeZpg(x);
				
				break;
			case 0x88: //DEY impl
				y--;
				if (y < 0) y = 0xFF;

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0x8A: //TXA impl
				acc = x;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x8C: //STY abs
				writeAbs(y);
				
				break;
			case 0x8D: //STA abs
				writeAbs(acc);
				
				break;
			case 0x8E: //STX abs
				writeAbs(x);
				
				break;
			case 0x90: //BCC rel
				if (!flags.c) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0x91: //STA ind,Y
				writeIndy(acc);
				
				break;
			case 0x94: //STY zpg,X
				writeZpgx(y);
				
				break;
			case 0x95: //STA zpg,X
				writeZpgx(acc);
				
				break;
			case 0x96: //STX zpg,Y
				writeZpgy(x);
				
				break;
			case 0x98: //TYA impl
				acc = y;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0x99: //STA abs,Y
				writeAbsy(acc);
				
				break;
			case 0x9A: //TXS impl
				stack.push(x);
				
				break;
			case 0x9D: //STA abs,X
				writeAbsx(acc);
				
				break;
			case 0xA0: //LDY #
				y = read(pc++);

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xA1: //LDA X,ind
				acc = readXind();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xA2: //LDX #
				x = read(pc++);

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xA4: //LDY zpg
				y = readZpg();

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xA5: //LDA zpg
				acc = readZpg();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xA6: //LDX zpg
				x = readZpg();

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xA8: //TAY impl
				y = acc;
				
				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xA9: //LDA #
				acc = read(pc++);
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xAA: //TAX impl
				x = acc;
				
				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xAC: //LDY abs
				y = readAbs();

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xAD: //LDA abs
				acc = readAbs();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xAE: //LDX abs
				x = readAbs();

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xB0: //BCS rel
				if (flags.c) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0xB1: //LDA ind,Y
				acc = readIndy();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xB4: //LDY zpg,X
				y = readZpgx();

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xB5: //LDA zpg,X
				acc = readZpgx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xB6: //LDX zpg,Y
				x = readZpgx();

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xB8: //CLV impl
				flags.v = false;
				
				break;
			case 0xB9: //LDA abs,Y
				acc = readAbsy();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xBA: //TSX impl
				x = stack.pop();
				
				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xBC: //LDY abs,X
				y = readAbsx();

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xBD: //LDA abs,X
				acc = readAbsx();
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xBE: //LDX abs,Y
				x = readAbsy();

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xC0: //CPY #
				reg[0] = y - read(pc++);
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xC1: //CMP X,ind
				reg[0] = acc - readXind();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xC4: //CPY zpg
				reg[0] = y - readZpg();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xC5: //CMP zpg
				reg[0] = acc - readZpg();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xC6: //DEC zpg
				reg[0] = readZpg() - 1;
				if (reg[0] < 0) reg[0] = 0xFF;
				write(-1, reg[0]);

				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xC8: //INY impl
				y++;
				y &= 0b11111111;

				flags.z = y == 0;
				flags.n = (y & 0b10000000) != 0;
				break;
			case 0xC9: //CMP #
				reg[0] = acc - read(pc++);
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xCA: //DEX impl
				x--;
				if (x < 0) x = 0xFF;

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xCC: //CPY abs
				reg[0] = y - readAbs();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				
				break;
			case 0xCD: //CMP abs
				reg[0] = acc - readAbs();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xCE: //DEC abs
				reg[0] = readAbs() - 1;
				if (reg[0] < 0) reg[0] = 0xFF;
				write(-1, reg[0]);

				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xD0: //BNE rel
				if (!flags.z) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0xD1: //CMP ind,Y
				reg[0] = acc - readIndy();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xD5: //CMP zpg,X
				reg[0] = acc - readZpgx();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xD6: //DEC zpg,X
				reg[0] = readZpgx() - 1;
				if (reg[0] < 0) reg[0] = 0xFF;
				write(-1, reg[0]);

				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xD8: //CLD impl
				flags.d = false;
				
				break;
			case 0xD9: //CMP abs,Y
				reg[0] = acc - readAbsy();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xDD: //CMP abs,X
				reg[0] = acc - readAbsx();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xDE: //DEC abs,X
				reg[0] = readAbsx() - 1;
				if (reg[0] < 0) reg[0] = 0xFF;
				write(-1, reg[0]);

				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xE0: //CPX #
				reg[0] = x - read(pc++);
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xE1: //SBC X,ind
				reg[0] = acc - readXind() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = reg[0] > -1;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xE4: //CPX zpg
				reg[0] = x - readZpg();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xE5: //SBC zpg
				reg[0] = acc - readZpg() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xE6: //INC zpg
				reg[0] = (readZpg() + 1) & 0xFF;
				write(-1, reg[0]);
				
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xE8: //INX impl
				x++;
				x &= 0xFF;

				flags.z = x == 0;
				flags.n = (x & 0b10000000) != 0;
				break;
			case 0xE9: //SBC #
				reg[0] = acc - read(pc++) - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xEA: //NOP impl
				break;
			case 0xEC: //CPX abs
				reg[0] = x - readAbs();
				
				flags.c = reg[0] >= 0;
				flags.z = reg[0] == 0;
				flags.n = reg[0] < 0;
				break;
			case 0xED: //SBC abs
				reg[0] = acc - readAbs() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xEE: //INC abs
				reg[0] = (readAbs() + 1) & 0xFF;
				write(-1, reg[0]);
				
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xF0: //BEQ rel
				if (!flags.z) {
					pc += read(pc++);
					pc &= 0xFF;
				} else {
					pc++;
				}
				
				break;
			case 0xF1: //SBC ind,Y
				reg[0] = acc - readIndy() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xF5: //SBC zpg,X
				reg[0] = acc - readZpgx() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xF6: //INC zpg,X
				reg[0] = (readZpgx() + 1) & 0xFF;
				write(-1, reg[0]);
				
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			case 0xF8: //SED impl
				flags.d = true;
				
				break;
			case 0xF9: //SBC abs,Y
				reg[0] = acc - readAbsy() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xFA:
				if (System.nanoTime() - nanoSleep < 1e9/60);
				nanoSleep = System.nanoTime();
				
				break;
			case 0xFD: //SBC abs,X
				reg[0] = acc - readAbsx() - (1 - (flags.c ? 1 : 0));
				if (reg[0] < 0) reg[0] += 0x100;
				acc = reg[0];

				flags.c = (reg[0] & 0b100000000) != 0;
				flags.z = acc == 0;
				flags.n = (acc & 0b10000000) != 0;
				break;
			case 0xFE: //INC abs,X
				reg[0] = (readAbsx() + 1) & 0xFF;
				write(-1, reg[0]);
				
				flags.z = reg[0] == 0;
				flags.n = (reg[0] & 0b10000000) != 0;
				break;
			}
			
			if (System.nanoTime() - nano > 1e9/60) {
				nano = System.nanoTime();
				display.render();
			}
			
			try {
				Thread.sleep(0, 250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//Address
	private int xind() {
		reg[0] = read(pc++) + x;
		reg[1] = read(reg[0]);
		return reg[1] | (read(reg[0] + 1) << 8);
	}

	private int indy() {
		reg[0] = read(pc++);
		reg[1] = read(reg[0]);
		return (reg[0] | (read(reg[0] + 1) << 8)) + y;
	}

	private int zpg() {
		return read(pc++);
	}

	private int abs() {
		reg[0] = read(pc++);
		return reg[0] | (read(pc++) << 8);
	}

	private int zpgx() {
		return readZpg() + x;
	}

	private int zpgy() {
		return readZpg() + y;
	}


	private int absx() {
		return readAbs() + x;
	}

	private int absy() {
		return readAbs() + y;
	}

	//Read
	private int readXind() {
		return read(xind());
	}

	private int readIndy() {
		return read(indy());
	}

	private int readZpg() {
		return read(zpg());
	}

	private int readAbs() {
		return read(abs());
	}

	private int readZpgx() {
		return read(zpgx());
	}

	private int readZpgy() {
		return read(zpgy());
	}

	private int readAbsx() {
		return read(absx());
	}

	private int readAbsy() {
		return read(absy());
	}

	//Write
	private void writeXind(int data) {
		write(xind(), data);
	}

	private void writeIndy(int data) {
		write(indy(), data);
	}

	private void writeZpg(int data) {
		write(zpg(), data);
	}

	private void writeAbs(int data) {
		write(abs(), data);
	}

	private void writeZpgx(int data) {
		write(zpgx(), data);
	}

	private void writeZpgy(int data) {
		write(zpgy(), data);
	}

	private void writeAbsx(int data) {
		write(absx(), data);
	}

	private void writeAbsy(int data) {
		write(absy(), data);
	}

	private int lastAddress;
	public int read(int address) {
		if (address == -1) address = lastAddress;
		
		lastAddress = address;
		
		if ((address & 0x2000) == 0) {
			int top = address >> 11;
			int bottom = address & 0x7FF;
			switch(top) {
			case 0b00:	//RAM
				return memory.read(bottom);
				
			case 0b01:	//ARAM
				break; //return audio.read(bottom);
				
			case 0b10:	//IO
				return controller.read(bottom);
				
			case 0b11:	//VRAM
				break; //return display.read(bottom);
			}
		} else {		//ROM
			return memory.read(address);
		}
		
		return -1;
	}
	
	public void write(int address, int data) {
		data = data & 0b11111111;
		
		if (address == -1) address = lastAddress;
		
		lastAddress = address;
		
		if ((address & 0x2000) == 0) {
			int top = address >> 11;
			int bottom = address & 0x7FF;
			switch(top) {
			case 0b00:	//RAM
				memory.write(bottom, address);
				
			case 0b01:	//ARAM
				break; //return audio.write(bottom);
				
			case 0b10:	//IO
				//controller.write(bottom);
				
			case 0b11:	//VRAM
				display.write(bottom, data);
			}
		} else {		//ROM
			//memory.write(address);
		}
	}
}
