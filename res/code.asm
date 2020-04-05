	LDA #0
	STA $1800
	LDA #0
	STA $1801
lable:
	ROR A
	STA $1801
	JMP lable