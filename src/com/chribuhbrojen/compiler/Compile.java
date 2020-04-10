package com.chribuhbrojen.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chribuhbrojen.emulator.Memory;

public class Compile {
	public static void main(String[] args) {
		loadCode("/code.asm");
	}
	
	private static void loadCode(String file) {
		Scanner scan = new Scanner(Compile.class.getResourceAsStream(file));
		
		Map<String, Integer> labels = new HashMap<String, Integer>();
		
		int pc = 0x2000;
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			line = line.replace("	", "");
			
			if (line.startsWith(";") || line.isEmpty()) continue;
			
			if (line.contains(";")) {
				line = line.substring(0, line.indexOf(';'));
			}
			
			if (line.contains(":")) {
				labels.put(line.substring(0, line.indexOf(':')), pc);
				
				continue;
			}

			String instr = line.split(" ")[0];
			Instructions instruction = Instructions.valueOf(instr);
			
			String opc = line.contains(" ") ? ((line.contains("$") || line.contains("#") || (line.endsWith("A") && line.length() == 5)) ? 
					line.split(" ")[1] : 
					((instruction == Instructions.BCC || instruction == Instructions.BCS || instruction == Instructions.BEQ || instruction == Instructions.BMI ||
					instruction == Instructions.BNE || instruction == Instructions.BPL || instruction == Instructions.BVC || instruction == Instructions.BVS) ? 
							"$00" : "$0000")) : "";
			Opcodes opcode = Opcodes.match(opc);

			pc += opcode.length;
		}
		
		scan = new Scanner(Compile.class.getResourceAsStream(file));
		
		pc = 0x2000;
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			line = line.replace("	", "");
			
			if (line.startsWith(";") || line.isEmpty() || line.contains(":")) continue;
			
			if (line.contains(";")) {
				line = line.substring(0, line.indexOf(';'));
			}

			String instr = line.split(" ")[0];
			Instructions instruction = Instructions.valueOf(instr);
			
			String opc = line.contains(" ") ? ((line.contains("$") || line.contains("#") || (line.endsWith("A") && line.length() == 5)) ? 
					line.split(" ")[1] : 
					((instruction == Instructions.BCC || instruction == Instructions.BCS || instruction == Instructions.BEQ || instruction == Instructions.BMI ||
					instruction == Instructions.BNE || instruction == Instructions.BPL || instruction == Instructions.BVC || instruction == Instructions.BVS) ? 
							"$" + Integer.toHexString((0x100 + (labels.get(line.split(" ")[1]) - (pc + 2))) & 0xFF) : "$" + Integer.toHexString(labels.get(line.split(" ")[1])))) : "";
			Opcodes opcode = Opcodes.match(opc);
			
			if (instruction == Instructions.BCC ||instruction == Instructions.BCS ||instruction == Instructions.BEQ ||instruction == Instructions.BMI ||
				instruction == Instructions.BNE ||instruction == Instructions.BPL ||instruction == Instructions.BVC ||instruction == Instructions.BVS) {
				opcode = Opcodes.REL;
			}

			String loAddrInstr = new String(instruction.loAddr);
			String hiAddrInstr = new String(instruction.hiAddr);
			String loAddrOpc = new String(opcode.loAddr);
			String notHiAddrOpc = new String(opcode.notHiAddr);
			
			char loNibble = 0;
			for (char c : loAddrInstr.toCharArray()) {
				if (loAddrOpc.indexOf(c) != -1) {
					loNibble = c;
					break;
				}
			}

			char hiNibble = 0;
			for (char c : hiAddrInstr.toCharArray()) {
				if (notHiAddrOpc.indexOf(c) == -1) {
					hiNibble = c;
					break;
				}
			}
			
			String write = hiNibble + "" + loNibble;
			
			if (opcode == Opcodes.IMM) {
				if (opc.indexOf('$') != -1) {
					write += " " + opc.substring(opc.indexOf('$')+1, opc.indexOf('$')+3);
				} else if (opc.indexOf('%') != -1) {
					int val = Integer.parseInt(opc.substring(opc.indexOf('%')+1, opc.indexOf('%')+9), 2);
					write += " " + (Integer.toHexString(val).length() == 1 ? "0" : "") + Integer.toHexString(val);
				} else {
					int val = Integer.parseInt(opc.substring(1));
					write += " " + (Integer.toHexString(val).length() == 1 ? "0" : "") + Integer.toHexString(val);
				}
			} else if (opcode != Opcodes.IMPL) {
				if (opcode == Opcodes.ABS || opcode == Opcodes.ABSX || opcode == Opcodes.ABSY || opcode == Opcodes.IND) 
					write += " " + opc.substring(opc.indexOf('$')+3, opc.indexOf('$')+5) + " " + opc.substring(opc.indexOf('$')+1, opc.indexOf('$')+3);
				else if (opcode == Opcodes.XIND || opcode == Opcodes.INDY || opcode == Opcodes.REL || opcode == Opcodes.ZPG || opcode == Opcodes.ZPGX || opcode == Opcodes.ZPGY) 
					write += " " + opc.substring(opc.indexOf('$')+1, opc.indexOf('$')+3 > opc.length() ? opc.length() : opc.indexOf('$')+3);
			}
			
			write = write.toUpperCase();
			
			System.out.println(write);
			pc += opcode.length;
		}
	}
}
