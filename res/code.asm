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
; $0008-$0009: Score
; $07D0-$07FF: Level

	;-----INIT-----
	LDA #11		; Set paddle pos to center of screen
	STA $00
	LDA #11		; Set ball x pos to center of paddle
	STA $02
	LDA #14		; Set ball y pos to right over paddle
	STA $04
	LDA #$F6	; Set initial x-velocity to -10
	STA $03
	LDA #$F6	; Set initial y-velocity to -10
	STA $06
	
	;-----LOOP-----
mainLoop:
	
	;-----MOVE PADDLE-----
	LDA $1000		; Load controler x value
	BEQ rightCheck	; Branch if controlstick is held right (#0)
	AND #%00010000	; And controler value to get if it is held left
	BEQ skipCheck	; Controler isn't held in any direction, so skip check
	
	;-----LEFT-----
	INC $01			; Add one to subpos of paddle
	CMP #$10		; Cehck if paddle subpos is greater than or equals to 16
	BCC skipCheck
	CLC
	LDX #0
	STX $01			; Set paddle subpos to 0
	
	LDA $00			; Load value of paddle
	SBC #1			; Move paddle one left
	BVC $02			; Branch to store paddle position, if paddle isn't off screen (paddle pos < 0)
	LDA #0			; Set paddle pos to 0
	STA $00			; Store paddle position
	
	JMP skipCheck

rightCheck:
	;-----RIGHT-----
	INC $01			; Add one to subpos of paddle
	CMP #$10		; Cehck if paddle subpos is greater than or equals to 16
	BCC skipCheck
	CLC
	LDX #0
	STX $01			; Set paddle subpos to 0
	
	LDA $00			; Load value of paddle
	ADC #1			; Move paddle one right
	CMP #22			; Compare paddle pos to 22 (Off screen)
	BVC $02			; Branch to store paddle position, if paddle isn't off screen
	LDA #21			; Set paddle pos to 21
	STA $00			; Store paddle position
	
skipCheck:
	
	;-----MOVE BALL-----
	
	;-----x-----
	CLC
	LDA $03			; Load balls sub x position
	ADC $04			; Add velocity to sub x position
	STA $03			; Update ball sub x position
	BVC yMoveBall	; If sub position didn't under, or overflow, then ball didn't move
	
	LDA $02			; Load ball x position (Carry flag is not affected)
	BCC $04			; If carry is cleared, then the direction is negative and then don't move ball positivly
	ADC #1			; Add one to balls position
	BCS $03			; Branch to end of subtraction
	CLC
	SBC #1			; Subtract one from balls position
	
	;-----Collision-----
	; To get player x screen pos, right-shift x three times (devide by 8), this is the row offset.
	; AND x with %111, this is the inter-byte position.
	; To get player y screen pos, left-shit once and add y to result (multiply by 3), this is the row. (Maybe just add the number to itself three times)
	; To get player final position, add row and row offset, then the inter-byte position is the bit to set on that row.
	
	
	
yMoveBall:
	CLC
	LDA $06			; Load balls sub y position
	ADC $07			; Add velocity to sub y position
	BVC skipBallMove	; If sub position didn't under, or overflow, then ball didn't move
	
	
skipBallMove:
	
	;-----RENDER-----
	LDX $00			; Copy paddle pos to X
	TXA				; Transfer paddle pos to A
	LSR A			; Divide paddle pos with 8 (A >> 3), this should then be the row number
	LSR A
	LSR A
	ADC #45			; Offset paddle row to last row
	STA $1800		; Store row value to VRAM

	TXA				; Transfer paddle pos to A
	AND #%00000111	; Get the lowest three bits of paddle pos (Position on row)
	STA $1801		; Store paddle inter-pos to VRAM
	
	JMP mainLoop