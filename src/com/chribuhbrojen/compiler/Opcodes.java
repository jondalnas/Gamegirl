package com.chribuhbrojen.compiler;

public enum Opcodes {
	A	("^A",							new char[] {'A'},					new char[] {'1', '3', '5', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'},				1),
	ABS ("^\\$[0-9|A-F|a-f]{4}$",		new char[] {'0', 'C', 'D', 'E'},	new char[] {'1', '3', '5', '7', '9', 'B', 'D', 'F'},									3),
	ABSX("^\\$[0-9|A-F|a-f]{4},X",		new char[] {'C', 'D', 'E'},			new char[] {'0', '2', '4', '6', '8', 'A', 'C', 'E'},									3),
	ABSY("^\\$[0-9|A-F|a-f]{4},Y",		new char[] {'9', 'E'},				new char[] {'0', '2', '4', '6', '8', 'A', 'C', 'E'},									3),
	IMM ("#(.*)",						new char[] {'0', '2', '9'},			new char[] {'1', '3', '5', '7', '9', 'B', 'D', 'F'},									2),
	IMPL("^(?![\\s\\S])",				new char[] {'0', '8', 'A'},			new char[] {},																			1),
	IND ("\\(\\$[0-9|A-F|a-f]{4}\\)",	new char[] {'C'},					new char[] {'0', '1', '2', '3', '4', '5', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'},	3),
	XIND("\\(\\$[0-9|A-F|a-f]{2},X\\)",	new char[] {'1'},					new char[] {'1', '3', '5', '7', '9', 'B', 'D', 'F'},									2),
	INDY("\\(\\$[0-9|A-F|a-f]{2}\\),Y",	new char[] {'1'},					new char[] {'0', '2', '4', '6', '8', 'A', 'C', 'E'},									2),
	REL ("^\\$[0-9|A-F|a-f]{2}$",		new char[] {'0'},					new char[] {'0', '2', '4', '6', '8', 'A', 'C', 'E'},									2),
	ZPG ("^\\$[0-9|A-F|a-f]{2}$",		new char[] {'4', '5', '6'},			new char[] {'1', '3', '5', '7', '9', 'B', 'D', 'F'},									2),
	ZPGX("^\\$[0-9|A-F|a-f]{2},X",		new char[] {'4', '5', '6'},			new char[] {'0', '2', '4', '6', '8', 'A', 'C', 'E'},									2),
	ZPGY("^\\$[0-9|A-F|a-f]{2},Y",		new char[] {'6'},					new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', 'A', 'C', 'D', 'E', 'F'},		2);
	
	String regx;
	char[] loAddr;
	char[] notHiAddr;
	int length;
	Opcodes(String regx, char[] loAddr, char[] notHiAddr, int length) {
		this.regx = regx;
		this.loAddr = loAddr;
		this.notHiAddr = notHiAddr;
		this.length = length;
	}
	
	public static Opcodes match(String value) {
		for (Opcodes opc : Opcodes.values()) {
			if (opc.name() == "REL") continue;
			
			if (value.matches(opc.regx)) return opc;
		}
		
		return null;
	}
}
