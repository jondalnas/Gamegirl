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
; $0008: Controller value
; $0009-$000A: Score
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
	JSR mux			; Get position of thumbstick (0-8)
	SBC #4			; Center value, so netural is 0
	
	BMI rightCheck	; If minus, then controller direction is right
	BEQ skipCheck	; If 0 then skip
	
	;-----LEFT-----
	STA $08
	
	LDA $01			; Load subpos of paddle into accumulator
	ADC $08			; Add controller value to subpos of paddle
	CMP #$10		; Check if paddle subpos is greater than or equals to 16
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
	EOR #%11111111	; Invert all bits to make positive
	ADC #1			; Add one to because of two compliment
	STA $08
	
	LDA $01			; Load subpos of paddle into accumulator
	ADC $08			; Add controller value to subpos of paddle
	CMP #$10		; Check if paddle subpos is greater than or equals to 16
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
	LDA $06				; Load balls sub y position
	ADC $07				; Add velocity to sub y position
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
	JSR demux		; Demux position of paddle
	STA $1801		; Store paddle inter-pos to VRAM
	
	JMP mainLoop
	
demux:
	; Set bit, in accumulator, corosponding to three first bits in accumulator
	AND #%00000111
	TAY
	BNE demuxOne	; Brach if number is not 0
	LDA #%00000001	; Set bit 0 in accumulator 
	RTS
	
demuxOne:
	CPY #%00000001
	BNE demuxTwo	; Branch if number is not 1
	LDA #%00000010	; Set bit 1 in accumulator 
	RTS

demuxTwo:
	CPY #%00000000
	BNE demuxThree	; Brach if number is not 2
	LDA #%00000100	; Set bit 2 in accumulator 
	RTS

demuxThree:
	CPY #%00000000
	BNE demuxFour	; Brach if number is not 3
	LDA #%00001000	; Set bit 3 in accumulator 
	RTS

demuxFour:
	CPY #%00000000
	BNE demuxFive	; Brach if number is not 4
	LDA #%00010000	; Set bit 4 in accumulator 
	RTS

demuxFive:
	CPY #%00000000
	BNE demuxSix	; Brach if number is not 5
	LDA #%00100000	; Set bit 5 in accumulator 
	RTS

demuxSix:
	CPY #%00000000
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