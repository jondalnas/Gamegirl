
void setup() {
  //Set address lines to read only
  DDRA = 0xFF;
  DDRC = 0xFF;
}

uint8_t acc = 0;
uint8_t x = 0;
uint8_t y = 0;
void loop() {
  //Instruction set is 6502: https://www.masswerk.at/6502/6502_instruction_set.htm
  
  writeAddress(0x0000);
  uint8_t instr = readData();

  switch(instr) {
  case 0x00: //nop
    break;
  case 0x01: //ORA X,ind
    
    break;
  case 0x05: //ORA zpg
    
    break;
  case 0x06: //ASL zpg
    
    break;
  case 0x08: //PHP impl
    
    break;
  case 0x09: //ORA #
    
    break;
  case 0x0A: //ASL A
    
    break;
  case 0x0D: //ORA abs
    
    break;
  case 0x0E: //ASL abs
    
    break;
  case 0x10: //BPL rel
    
    break;
  case 0x11: //ORA ind,Y
    
    break;
  case 0x15: //ORA zpg,X
    
    break;
  case 0x16: //ASL zpg,X
    
    break;
  case 0x18: //CLC impl
    
    break;
  case 0x19: //ORA abs,Y
    
    break;
  case 0x1D: //ORA abs,X
    
    break;
  case 0x1E: //ASL abs,X
    
    break;
  case 0x20: //JSR abs
    
    break;
  case 0x21: //AND X,ind
    
    break;
  case 0x24: //BIT zpg
    
    break;
  case 0x25: //AND zpg
    
    break;
  case 0x26: //ROL zpg
    
    break;
  case 0x28: //PLP impl
    
    break;
  case 0x29: //AND #
    
    break;
  case 0x2A: //ROL A
    
    break;
  case 0x2C: //BIT abs
    
    break;
  case 0x2D: //AND abs
    
    break;
  case 0x2E: //ROL abs
    
    break;
  case 0x30: //BMI rel
    
    break;
  case 0x31: //AND X,ind
    
    break;
  case 0x35: //AND zpg,X
    
    break;
  case 0x36: //ROL zpg,X
    
    break;
  case 0x38: //SEC impl
    
    break;
  case 0x39: //AND abs,Y
    
    break;
  case 0x3D: //AND abs,X
    
    break;
  case 0x3E: //ROL abs,X
    
    break;
  case 0x40: //RTI impl
    
    break;
  case 0x41: //EOR X,ind
    
    break;
  case 0x45: //EOR zpg
    
    break;
  case 0x46: //LSR zpg
    
    break;
  case 0x48: //PHA impl
    
    break;
  case 0x49: //EOR #
    
    break;
  case 0x4A: //LSR A
    
    break;
  case 0x4C: //JMP abs
    
    break;
  case 0x4D: //EOR abs
    
    break;
  case 0x4E: //LSR abs
    
    break;
  case 0x50: //BVC rel
    
    break;
  case 0x51: //EOR ind,Y
    
    break;
  case 0x55: //EOR zpg,X
    
    break;
  case 0x56: //LSR zpg,X
    
    break;
  case 0x58: //CLI impl
    
    break;
  case 0x59: //EOR abs,Y
    
    break;
  case 0x5D: //EOR abs,X
    
    break;
  case 0x5E: //LSR abs,X
    
    break;
  case 0x60: //RTS impl
    
    break;
  case 0x61: //ADC X,ind
    
    break;
  case 0x65: //ADC zpg
    
    break;
  case 0x66: //ROR zpg
    
    break;
  case 0x68: //PLA impl
    
    break;
  case 0x69: //ADC #
    
    break;
  case 0x6A: //ROR A
    
    break;
  case 0x6C: //JMP ind
    
    break;
  case 0x6D: //ADC abs
    
    break;
  case 0x6E: //ROR abs
    
    break;
  case 0x70: //BVS rel
    
    break;
  case 0x71: //ADC ind,Y
    
    break;
  case 0x75: //ADC zpg,X
    
    break;
  case 0x76: //ROR zpg,X
    
    break;
  case 0x78: //SEI impl
    
    break;
  case 0x79: //ADC abs,Y
    
    break;
  case 0x7D: //ADC abs,X
    
    break;
  case 0x7E: //ROR abs,X
    
    break;
  case 0x81: //STA X,ind
    
    break;
  case 0x84: //STY zpg
    
    break;
  case 0x85: //STA zpg
    
    break;
  case 0x86: //STX zpg
    
    break;
  case 0x88: //DEY impl
    
    break;
  case 0x8A: //TXA impl
    
    break;
  case 0x8C: //STY abs
    
    break;
  case 0x8D: //STA abs
    
    break;
  case 0x8E: //STX abs
    
    break;
  case 0x90: //BCC rel
    
    break;
  case 0x91: //STA ind,Y
    
    break;
  case 0x94: //STY zpg,X
    
    break;
  case 0x95: //STA zpg,X
    
    break;
  case 0x96: //STX zpg,Y
    
    break;
  case 0x98: //TYA impl
    
    break;
  case 0x99: //STA abs,Y
    
    break;
  case 0x9A: //TXS impl
    
    break;
  case 0x9D: //STA abs,X
    
    break;
  case 0xA0: //LDY #
    
    break;
  case 0xA1: //LDA X,ind
    
    break;
  case 0xA2: //LDX #
    
    break;
  case 0xA4: //LDY zpg
    
    break;
  case 0xA5: //LDA zpg
    
    break;
  case 0xA6: //LDX zpg
    
    break;
  case 0xA8: //TAY impl
    
    break;
  case 0xA9: //LDA #
    
    break;
  case 0xAA: //TAX impl
    
    break;
  case 0xAC: //LDY abs
    
    break;
  case 0xAD: //LDA abs
    
    break;
  case 0xAE: //LDX abs
    
    break;
  case 0xB0: //BCS rel
    
    break;
  case 0xB1: //LDA ind,Y
    
    break;
  case 0xB4: //LDY zpg,X
    
    break;
  case 0xB5: //LDA zpg,X
    
    break;
  case 0xB6: //LDX zpg,Y
    
    break;
  case 0xB8: //CLV impl
    
    break;
  case 0xB9: //LDA abs,Y
    
    break;
  case 0xBA: //TSX impl
    
    break;
  case 0xBC: //LDY abs,X
    
    break;
  case 0xBD: //LDA abs,X
    
    break;
  case 0xBE: //LDX abs,Y
    
    break;
  case 0xC0: //CPY #
    
    break;
  case 0xC1: //CMP X,ind
    
    break;
  case 0xC4: //CPY zpg
    
    break;
  case 0xC5: //CMP zpg
    
    break;
  case 0xC6: //DEC zpg
    
    break;
  case 0xC8: //INY impl
    
    break;
  case 0xC9: //CMP #
    
    break;
  case 0xCA: //DEX impl
    
    break;
  case 0xCC: //CPY abs
    
    break;
  case 0xCD: //CMP abs
    
    break;
  case 0xCE: //DEC abs
    
    break;
  case 0xD0: //BNE rel
    
    break;
  case 0xD1: //CMP ind,Y
    
    break;
  case 0xD5: //CMP zpg,X
    
    break;
  case 0xD6: //DEC zpg,X
    
    break;
  case 0xD8: //CLD impl
    
    break;
  case 0xD9: //CMP abs,Y
    
    break;
  case 0xDD: //CMP abs,X
    
    break;
  case 0xDE: //DEC abs,X
    
    break;
  case 0xE0: //CPX #
    
    break;
  case 0xE1: //SBC X,ind
    
    break;
  case 0xE4: //CPX zpg
    
    break;
  case 0xE5: //SBC zpg
    
    break;
  case 0xE6: //INC zpg
    
    break;
  case 0xE8: //INX impl
    
    break;
  case 0xE9: //SBC #
    
    break;
  case 0xEA: //NOP impl
    
    break;
  case 0xEC: //CPX abs
    
    break;
  case 0xED: //SBC abs
    
    break;
  case 0xEE: //INC abs
    
    break;
  case 0xF0: //BEQ rel
    
    break;
  case 0xF1: //SBC ind,Y
    
    break;
  case 0xF5: //SBC zpg,X
    
    break;
  case 0xF6: //INC zpg,X
    
    break;
  case 0xF8: //SED impl
    
    break;
  case 0xF9: //SBC abs,Y
    
    break;
  case 0xFD: //SBC abs,X
    
    break;
  case 0xFE: //INC abs,X
    
    break;
  }
}

inline void writeAddress(uint16_t addr) {
  PORTA |= addr & 0b11111111;
  PORTC |= addr >> 8;
}

inline void clearAddress() {
  PORTA = 0;
  PORTC = 0;
}

inline void writeData(uint8_t data) {
  DDRL = 0xFF;
  PORTL |= data;
}

inline uint8_t readData() {
  DDRL = 0;
  return PINA;
}

inline  void clearData() {
  PORTL = 0;
}

