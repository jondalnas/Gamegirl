package com.chribuhbrojen.compiler;

public enum Instructions {
	ADC(new char[] {'6', '7'},	new char[] {'1', '5', '9', 'D'}),
	AND(new char[] {'2', '3'},	new char[] {'1', '5', '9', 'D'}),
	ASL(new char[] {'0', '1'},	new char[] {'6', 'A', 'E'}),
	BCC(new char[] {'9'},		new char[] {'0'}),
	BCS(new char[] {'B'},		new char[] {'0'}),
	BEQ(new char[] {'F'},		new char[] {'0'}),
	BIT(new char[] {'2'},		new char[] {'4', 'C'}),
	BMI(new char[] {'3'},		new char[] {'0'}),
	BNE(new char[] {'D'},		new char[] {'0'}),
	BPL(new char[] {'1'},		new char[] {'0'}),
	BRK(new char[] {'0'},		new char[] {'0'}),
	BVC(new char[] {'5'},		new char[] {'0'}),
	BVS(new char[] {'7'},		new char[] {'0'}),
	CLC(new char[] {'1'},		new char[] {'8'}),
	CLD(new char[] {'D'},		new char[] {'8'}),
	CLI(new char[] {'5'},		new char[] {'8'}),
	CLV(new char[] {'B'},		new char[] {'8'}),
	CMP(new char[] {'C', 'D'},	new char[] {'1', '5', '9', 'D'}),
	CPX(new char[] {'E'},		new char[] {'0', '4', 'C'}),
	CPY(new char[] {'C'},		new char[] {'0', '4', 'C'}),
	DEC(new char[] {'C', 'D'},	new char[] {'6', 'E'}),
	DEX(new char[] {'C'},		new char[] {'A'}),
	DEY(new char[] {'8'},		new char[] {'8'}),
	EOR(new char[] {'4', '5'},	new char[] {'1', '5', '9', 'D'}),
	INC(new char[] {'E', 'F'},	new char[] {'6', 'E'}),
	INX(new char[] {'E'},		new char[] {'8'}),
	INY(new char[] {'C'},		new char[] {'8'}),
	JMP(new char[] {'4', '6'},	new char[] {'C'}),
	JSR(new char[] {'2'},		new char[] {'0'}),
	LDA(new char[] {'A', 'B'},	new char[] {'1', '5', '9', 'D'}),
	LDX(new char[] {'A', 'B'},	new char[] {'2', '6', 'E'}),
	LDY(new char[] {'A', 'B'},	new char[] {'0', '4', 'C'}),
	LSR(new char[] {'4', '5'},	new char[] {'6', 'A', 'E'}),
	NOP(new char[] {'E'},		new char[] {'A'}),
	ORA(new char[] {'0', '1'},	new char[] {'1', '5', '9', 'D'}),
	PHA(new char[] {'4'},		new char[] {'8'}),
	PHP(new char[] {'0'},		new char[] {'8'}),
	PLA(new char[] {'6'},		new char[] {'8'}),
	PLP(new char[] {'2'},		new char[] {'8'}),
	ROL(new char[] {'2', '3'},	new char[] {'6', 'A', 'E'}),
	ROR(new char[] {'6', '7'},	new char[] {'6', 'A', 'E'}),
	RTI(new char[] {'4'},		new char[] {'0'}),
	RTS(new char[] {'6'},		new char[] {'0'}),
	SBC(new char[] {'E', 'F'},	new char[] {'1', '5', '9', 'D'}),
	SEC(new char[] {'3'},		new char[] {'8'}),
	SED(new char[] {'F'},		new char[] {'8'}),
	SEI(new char[] {'7'},		new char[] {'8'}),
	SLP(new char[] {'F'},		new char[] {'A'}),
	STA(new char[] {'8', '9'},	new char[] {'1', '5', '9', 'D'}),
	STX(new char[] {'8', '9'},	new char[] {'6', 'E'}),
	STY(new char[] {'8', '9'},	new char[] {'4', 'C'}),
	TAX(new char[] {'A'},		new char[] {'A'}),
	TAY(new char[] {'A'},		new char[] {'8'}),
	TSX(new char[] {'B'},		new char[] {'A'}),
	TXA(new char[] {'8'},		new char[] {'A'}),
	TXS(new char[] {'9'},		new char[] {'A'}),
	TYA(new char[] {'9'},		new char[] {'8'});

	char[] hiAddr;
	char[] loAddr;
	Instructions(char[] hiAddr, char[] loAddr) {
		this.hiAddr = hiAddr;
		this.loAddr = loAddr;
	}
	
	public static Instructions findInstruction(String byteInstr) {
		byteInstr = byteInstr.length() == 2 ? byteInstr : ("0" + byteInstr);
		byteInstr = byteInstr.toUpperCase();
		
		for (Instructions inst : Instructions.values()) {
			for (char hi : inst.hiAddr) {
				if (hi == byteInstr.charAt(0)) {
					for (char lo : inst.loAddr) {
						if (lo == byteInstr.charAt(1)) {
							return inst;
						}
					}
					
					break;
				}
			}
		}
		
		return null;
	}
}
