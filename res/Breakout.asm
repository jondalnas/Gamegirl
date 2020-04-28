; $0000: RAM
; $0800: ARAM
; $1000: IO
; $1800: VRAM
; $2000: ROM
;
; -----Defines-----
; $0000: Paddle pos
; $0001: Paddle subpos
; $0002: Ball x
; $0003: Ball sub-x
; $0004: Ball velocity x
; $0005: Ball y
; $0006: Ball sub-y
; $0007: Ball velocity y
; $0009: Paddle interbyte value
; $000A: Ball row value
; $000B: Ball interbyte value
; $00F4-$00FF: Level

init:
	;-----INIT-----
	LDA #11		; Set paddle pos to center of screen
	STA $00
	LDA #11		; Set ball x pos to center of paddle
	STA $02
	LDA #14		; Set ball y pos to right over paddle
	STA $05
	LDA #$F0	; Set initial x-velocity to -16
	STA $04
	LDA #$F0	; Set initial y-velocity to -16
	STA $07
	
	;-----INIT BALL SCREEN POS-----
	LDA $02
	JSR demux
	STA $0B
	
	LDA $02
	LSR
	LSR
	LSR
	ADC $05
	ADC $05
	ADC $05
	STA $0A
	
	;-----INIT LEVEL-----
	LDA #$FF		; Load all ones into accumulator
	LDX #0			; Set start of loop to 0
initLvlLp:
	STA $F4,X		; Store all ones to ram area designated to level
	INX				; Incriment row counter
	CPX #$0C		; Test agains 12 (4 whole rows)
	BNE initLvlLp
	
	;-----LOOP-----
mainLoop:
	
	;-----MOVE PADDLE-----
	; $0008: Controller value 
	LDA $1000		; Load controler x value
	JSR mux			; Get position of thumbstick (0-8)
	CLC
	SBC #4			; Center value, so netural is 0
	
	BMI rightCheck	; If minus, then controller direction is right
	BEQ skipCheck	; If 0 then skip
	
	;-----LEFT-----
	STA $08
	
	LDA $01			; Load subpos of paddle into accumulator
	ADC $08			; Add controller value to subpos of paddle
	STA $01			; Store subpos of paddle
	CMP #$10		; Check if paddle subpos is greater than or equals to 16
	BCC skipCheck
	CLC
	LDX #0
	STX $01			; Set paddle subpos to 0
	
	LDA $00			; Load value of paddle
	SBC #1			; Move paddle one left
	BVC padGreZero	; Branch to store paddle position, if paddle isn't off screen (paddle pos < 0)
	LDA #0			; Set paddle pos to 0
padGreZero:
	STA $00			; Store paddle position
	
	JMP skipCheck

rightCheck:
	;-----RIGHT-----
	EOR #%11111111	; Invert all bits to make positive
	ADC #1			; Add one, because of two compliment
	STA $08
	
	LDA $01			; Load subpos of paddle into accumulator
	ADC $08			; Add controller value to subpos of paddle
	STA $01			; Store subpos of paddle
	CMP #$10		; Check if paddle subpos is greater than or equals to 16
	BCC skipCheck
	CLC
	LDX #0
	STX $01			; Set paddle subpos to 0
	
	LDA $00			; Load value of paddle
	ADC #1			; Move paddle one right
	CMP #22			; Compare paddle pos to 22 (Off screen)
	BMI padLsTwnThr	; Branch to store paddle position, if paddle isn't off screen
	LDA #21			; Set paddle pos to 21
padLsTwnThr:
	STA $00			; Store paddle position
	
skipCheck:
	
	;-----MOVE BALL-----
	
	;-----x-----
	; $0008: Old ball x pos
	; $0010: Old ball row
	CLC
	LDA $03			; Load balls sub x position
	ADC $04			; Add velocity to sub x position
	STA $03			; Update ball sub x position
	BVC yMoveBall	; If sub position didn't change its sign, then ball didn't move
	
	PHP				; Push procesor status to stack, since LDA affects negtive flag
	LDA #0			; Set ball sub x position to zero
	STA $03
	LDA $02			; Load ball x position
	STA $08			; Store old x pos of ball to memory
	PLP				; Retriever procesor status from stack

moveBallX:
	CLC
	BMI blXPosi		; If subpos is negative, then the direction is positive and then don't move ball negatively

blXNeg:
	SBC #1			; Subtract one from balls position
	STA $02
	BVC blXOnScrn1	; If ball position is negative after subtraction (overflow set), then ball hit left wall
	JSR invBallXDir
	CLC
	LDA #0			; Set x position to 0
	STA $02
	
blXPosi:
	ADC #1			; Add one to balls position
	STA $02
	CMP #$18		; Check if ball position is greater than 24 (level width)
	BCC blXOnScrn0	; If ball position is greter than 24 after addition, then ball hit right wall
	JSR invBallXDir
	CLC
	LDA #$17		; Set x position of ball to 23
	STA $02
	JMP blXNeg		; Jump to negative direction code
	
blXOnScrn0:
	LDA $0A			; Load ball row into accumulator
	STA $10			; Store ball row to memory
	LDA $0B			; Load ball pos to accumulator
	TAY				; Transfer ball pos to Y
	ASL A			; Move ball one right on screen
	BCC ballXColLvl	; If ball screen pos overflows, then overflow pos to next row
	LDA $0A			; Load ball row
	ADC #0			; Add one to row number (Carry is set)
	STA $0A
	LDA #%00000001	; Set ball row pos right most bit
	JMP ballXColLvl
	
blXOnScrn1:
	LDA $0A			; Load ball row into accumulator
	STA $10			; Store ball row to memory
	LDA $0B			; Load ball pos to accumulator
	TAY				; Transfer ball pos to Y
	LSR A			; Move ball one left on screen
	BCC ballXColLvl	; If ball screen pos overflows, then overflow pos to next row
	LDA $0A			; Load ball row
	SBC #0			; Subtract one from row number (Carry is set)
	STA $0A
	LDA #%10000000	; Set ball row pos left most bit
	JMP ballXColLvl
	
ballXColLvl:
	STA $0B			; Store new ball pos
	LDX $0A			; Load ball row position to X
	AND $F4,X		; AND ball position on screen with the level byte, that ball is on, based on row position
	BEQ yMoveBall	; If ball doesn't hit level (if AND returns zero), then don't do anything, else invert ball direction and undo moveing of ball
	
	BRK
	LDA $F4,X		; Load row data from row ball hit
	EOR $0B			; EX-OR with ball row data, to flip hit level bit off
	STA $F4,X		; Store level row data back
	
	STY $0B			; Store old ball pos
	LDA $08			; Store old x pos of ball to ball x pos
	STA $02
	LDA $10			; Store old ball row back to ball row
	STA $0A
	JSR invBallXDir
	EOR #$80		; Invert negative flag
	PHP
	LDA $02			; Load ball x position into accumulator
	PLP
	JMP moveBallX	; Jump back to move ball again, but with inverted direction
	
	;------y------
yMoveBall:
	CLC
	LDA $06			; Load balls sub y position
	ADC $07			; Add velocity to sub y position
	STA $06			; Update ball sub y position
	BVC skpBlMov	; If sub position didn't change its sign, then ball didn't move
	
	PHP				; Push procesor status to stack, since LDA affects negtive flag
	LDA #0			; Set ball sub y position to zero
	STA $06
	LDA $05			; Load ball y position
	STA $08			; Store old y pos of ball to memory
	PLP				; Retriever procesor status from stack
	
moveBallY:
	CLC
	BMI blYPosi		; If subpos is negative, then the direction is positive and then don't move ball negatively
	
blYNeg:
	SBC #1			; Subtract one from balls position
	STA $05
	BVC blYOnScrn1	; If ball position is negative after subtraction (overflow set), then ball hit top wall
	JSR invBallYDir
	CLC
	LDA #0			; Set y position to 0
	STA $05
	
blYPosi:
	ADC #1			; Add one to balls position
	STA $05
	CMP #$10		; Check if ball position is greater than 16 (level height)
	BCC blYOnScrn0	; If ball position is greter than 16 after addition, then ball hit bottom wall
	JMP init		; Reset game
	;JSR invBallYDir
	;CLC
	;LDA #$0F		; Set y position of ball to 15
	;STA $05
	;JMP blYNeg		; Jump to negative direction code
	
blYOnScrn0:
	LDA $0A			; Load ball row to accumulator
	TAY				; Transfer ball row to Y
	ADC #3			; Move ball one down on screen
	JMP ballYColLvl
	
blYOnScrn1:
	LDA $0A			; Load ball row to accumulator
	TAY				; Transfer ball row to Y
	CLC
	SBC #3			; Move ball one up on screen
	
ballYColLvl:
	STA $0A			; Store new ball row
	TAX				; Transfer ball row position to X
	LDA $0B			; Load row data
	AND $F4,X		; AND ball position on screen with the level byte, that ball is on, based on row position
	BEQ skpBlMov	; If ball doesn't hit level (if AND returns zero), then don't do anything, else invert ball direction and undo moveing of ball
	
	LDX $0A			; Load ball row into accumulator
	LDA $F4,X		; Load row data from row ball hit
	EOR $0B			; EX-OR with ball row data, to flip hit level bit off
	STA $F4,X		; Store level row data back
	
	STY $0A			; Store old ball row
	LDA $08			; Store old y pos of ball to ball y pos
	STA $05
	JSR invBallYDir
	EOR #$80		; Invert negative flag
	PHP
	LDA $05			; Load ball y position into accumulator
	PLP
	JMP moveBallY	; Jump back to move ball again, but with inverted direction
	
skpBlMov:
	
	;-----RENDER-----
	LDX $00			; Copy paddle pos to X
	TXA				; Transfer paddle pos to A
	
	LSR A			; Divide paddle pos with 8 (A >> 3), this should then be the row number
	LSR A
	LSR A
	CLC
	ADC #45			; Offset paddle row to last row
	STA $1800		; Store row value to VRAM
	TAY				; Store row number to Y

	TXA				; Transfer paddle pos to A
	JSR demux		; Demux position of paddle
	STA $09			; Store paddle position
	CMP #%01000000	; Cehck if paddle crosses row
	BPL changeRow
	ASL $09			; Left shift paddle to get next position
	ORA $09			; Or with current position, to get whole paddle
	ASL $09
	ORA $09
	
	STA $1801		; Render paddle
	JMP paddleRenderEnd
changeRow:
	BEQ oneOnNext	; If paddle pos is equal to %01000000, then only render one dot on next row, else render two
	LDA #%10000000	; Load value for row with only one dot on it
	STA $1801		; Render part of paddle
	INY				; Incriment paddle row to 
	STY $1800		; Store new row to VRAM
	LDA #%00000011	; Load value for row with two dots on it
	STA $1801		; Render second part of paddle
	JMP paddleRenderEnd
	
oneOnNext:
	LDA #%11000000	; Load value for row with two dots on it
	STA $1801		; Render part of paddle
	INY				; Incriment paddle row to 
	STY $1800		; Store new row to VRAM
	LDA #%00000001	; Load value for row with only one dot on it
	STA $1801		; Render second part of paddle
	
paddleRenderEnd:
	
	;-----RENDER BALL-----
	LDA $0A
	STA $1800
	LDA $0B
	STA $1801
	
	;-----RENDER LEVEL-----
	LDX #0			; Set row counter to 0
rndrLvlLp:
	STX $1800		; Set VRAM row to current row
	LDA $F4,X		; Load data from row X
	STA $1801		; Set VRAM row data to current row data
	INX				; Incriment row counter to next row
	CPX #$0C		; Test X against 12 (number of rows)
	BNE rndrLvlLp
	
	SLP
	JMP mainLoop
	;-----END OF LOOP-----
	
demux:
	; Set bit, in accumulator, corosponding to three first bits in accumulator
	AND #%00000111
	TAX
	BNE demuxOne	; Brach if number is not 0
	LDA #%00000001	; Set bit 0 in accumulator 
	RTS
	
demuxOne:
	CPX #%00000001
	BNE demuxTwo	; Branch if number is not 1
	LDA #%00000010	; Set bit 1 in accumulator 
	RTS

demuxTwo:
	CPX #%00000010
	BNE demuxThree	; Brach if number is not 2
	LDA #%00000100	; Set bit 2 in accumulator 
	RTS

demuxThree:
	CPX #%00000011
	BNE demuxFour	; Brach if number is not 3
	LDA #%00001000	; Set bit 3 in accumulator 
	RTS

demuxFour:
	CPX #%00000100
	BNE demuxFive	; Brach if number is not 4
	LDA #%00010000	; Set bit 4 in accumulator 
	RTS

demuxFive:
	CPX #%00000101
	BNE demuxSix	; Brach if number is not 5
	LDA #%00100000	; Set bit 5 in accumulator 
	RTS

demuxSix:
	CPX #%00000110
	BNE demuxSeven	; Brach if number is not 6
	LDA #%01000000	; Set bit 6 in accumulator 
	RTS

demuxSeven:
	LDA #%10000000	; Set bit 7 in accumulator 
	RTS
	
	
mux:
	; Get position of last set bit in acxumulator
	LDX #0			; Set bit pos register To 0
	CMP #0			; Compare mux byte to 0

muxLoop:
	BEQ endMux		; Check if mux byte is zero (has hit last set bit)
	INX				; Increment bit pos register
	LSR A			; Right shift mux byte
	BPL muxLoop		; Same as JMP, but one cycle faster (mux byte is always positive)

endMux:
	TXA				; Transfer pos of last set bit to accumulator
	RTS
	
	
invBallXDir:
	;-----INVERT BALL X DIRECTION-----
	CLC
	LDA $04			; Load ball x velocity
	EOR #$FF		; Invert all bits
	ADC #1			; Add one (because of twos compliment)
	STA $04			; Store inverted direction back to memory
	RTS
	
	
invBallYDir:
	;-----INVERT BALL Y DIRECTION-----
	LDA $07			; Load ball y velocity
	CLC
	EOR #$FF		; Invert all bits
	ADC #1			; Add one (because of twos compliment)
	STA $07			; Store inverted direction back to memory
	RTS