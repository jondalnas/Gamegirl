#define CLOCK PINK
#define CONT PINB
#define STACK_SIZE 16

struct Flags {
  bool c, z, i, d, b, v, n;
} flags;

uint8_t acc = 0;
uint8_t x = 0;
uint8_t y = 0;
uint16_t ptr = 0;
uint16_t *stack; //Set size?
uint8_t reg[16]; 

void setup() {
  //Set address lines to write only
  DDRA = 0xFF;
  DDRC = 0xFF;

  //Set clock pin to read only
  DDRK = 0;

  //Set continue pin to read only
  DDRB = 0;

  stack = (uint16_t*) malloc(STACK_SIZE * sizeof(uint16_t));
}

void loop() {
  //Instruction set is 6502: https://www.masswerk.at/6502/6502_instruction_set.htm
  
  writeAddress(ptr++);
  uint8_t instr = readData();

  switch(instr) {
  case 0x00: //BRK impl
    while(!CONT);
    break;
  case 0x01: //ORA X,ind
    acc |= readXind();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x05: //ORA zpg
    acc |= readZpg();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x06: //ASL zpg
    reg[0] = readZpg();
    reg[1] = reg[0] << 1;
    writeData(reg[0]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x08: //PHP impl
    *stack = (uint16_t) &flags;
    stack++;
    
    break;
  case 0x09: //ORA #
    writeAddress(ptr++);
    acc |= readData();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x0A: //ASL A
    acc <<= 1;
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    flags.c = reg[0] & 0b10000000;
    break;
  case 0x0D: //ORA abs
    acc |= readAbs();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x0E: //ASL abs
    reg[0] = readAbs();
    reg[1] = reg[0] << 1;
    writeData(reg[1]);

    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    flags.c = reg[0] & 0b10000000;
    break;
  case 0x10: //BPL rel
    if (!flags.n) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0x11: //ORA ind,Y
    acc |= readIndy();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x15: //ORA zpg,X
    acc |= readZpgx();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x16: //ASL zpg,X
    reg[0] = readZpgx();
    reg[1] = reg[0] << 1;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x18: //CLC impl
    flags.c = 0;
    
    break;
  case 0x19: //ORA abs,Y
    acc |= readAbsy();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x1D: //ORA abs,X
    acc |= readAbsx();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x1E: //ASL abs,X
    reg[0] = readAbsx();
    reg[1] = reg[0] << 1;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x20: //JSR abs
    *stack = ptr;
    stack++;
    
    break;
  case 0x21: //AND X,ind
    acc &= readXind();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x24: //BIT zpg
    reg[0] = readZpg() & readData();

    flags.z = !reg[0];
    flags.v = reg[0] & 0b01000000;
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x25: //AND zpg
    reg[0] = readZpg();
    acc &= reg[0];
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x26: //ROL zpg
    reg[0] = readZpg();
    reg[1] = reg[0] << 1 | flags.c;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x28: //PLP impl
    stack--;
    flags = *((Flags*) &stack);
    
    break;
  case 0x29: //AND #
    writeAddress(ptr++);
    acc &= readData();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x2A: //ROL A
    reg[0] = acc;
    acc = reg[0] << 1 | flags.c;

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x2C: //BIT abs
    reg[0] = acc & readAbs();

    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    flags.v = reg[0] & 0b01000000;
    
    break;
  case 0x2D: //AND abs
    acc &= readAbs();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x2E: //ROL abs
    reg[0] = readAbs();
    reg[1] = reg[0] << 1 | flags.c;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x30: //BMI rel
    if (flags.n) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0x31: //AND X,ind
    acc &= readXind();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x35: //AND zpg,X
    acc &= readZpgx();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x36: //ROL zpg,X
    reg[0] = readZpgx();
    reg[1] = reg[0] << 1 | flags.c;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x38: //SEC impl
    flags.c = 1;
    
    break;
  case 0x39: //AND abs,Y
    acc &= readAbsy();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x3D: //AND abs,X
    acc &= readAbsx();
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x3E: //ROL abs,X
    reg[0] = readAbsx();
    reg[1] = reg[0] << 1 | flags.c;
    writeData(reg[1]);

    flags.c = reg[0] & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x40: //RTI impl
    //??????
    
    break;
  case 0x41: //EOR X,ind
    acc ^= readXind();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x45: //EOR zpg
    acc ^= readZpg();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x46: //LSR zpg
    reg[0] = readZpg();
    reg[1] = reg[0] >> 1;
    writeData(reg[1]);
    
    flags.c = reg[0] & 0b1;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x48: //PHA impl
    *stack = (uint16_t) &acc;
    stack++;
    
    break;
  case 0x49: //EOR #
    writeAddress(ptr++);
    acc ^= readData();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x4A: //LSR A
    reg[0] = acc;
    acc = reg[0] >> 1;
    
    flags.c = reg[0] & 0b1;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x4C: //JMP abs
    ptr = readAbs();
    
    break;
  case 0x4D: //EOR abs
    acc ^= readAbs();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x4E: //LSR abs
    reg[0] = readAbs();
    reg[1] = reg[0] >> 1;
    writeData(reg[1]);
    
    flags.c = reg[0] & 0b1;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x50: //BVC rel
    if (!flags.v) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0x51: //EOR ind,Y
    acc ^= readIndy();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x55: //EOR zpg,X
    acc ^= readZpgx();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x56: //LSR zpg,X
    reg[0] = readZpgx();
    reg[1] = reg[0] >> 1;
    writeData(reg[1]);
    
    flags.c = reg[0] & 0b1;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x58: //CLI impl
    flags.i = 0;
    
    break;
  case 0x59: //EOR abs,Y
    acc ^= readAbsy();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x5D: //EOR abs,X
    acc ^= readAbsx();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x5E: //LSR abs,X
    reg[0] = readAbsx();
    reg[1] = reg[0] >> 1;
    writeData(reg[1]);
    
    flags.c = reg[0] & 0b1;
    flags.z = !reg[1];
    flags.n = reg[1] & 0b10000000;
    break;
  case 0x60: //RTS impl
    stack--;
    ptr = *stack;
    
    break;
  case 0x61: //ADC X,ind
    reg[0] = readXind();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x65: //ADC zpg
    reg[0] = readZpg();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x66: //ROR zpg
    reg[0] = readZpg();
    reg[1] = reg[0] >> 1 | flags.c << 7;
    writeData(reg[1]);

    flags.c = reg[0] & 0b1;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x68: //PLA impl
    stack--;
    acc = *stack;

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x69: //ADC #
    writeAddress(ptr++);
    reg[0] = readData();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x6A: //ROR A
    reg[0] = acc;
    acc = reg[0] >> 1 | flags.c << 7;

    flags.c = reg[0] & 0b1;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x6C: //JMP ind
    writeAddress(ptr++);
    reg[0] = readData();
    writeAddress(ptr++);
    writeAddress(reg[0] | readData() << 8);
    ptr = readData();
    
    break;
  case 0x6D: //ADC abs
    reg[0] = readAbs();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x6E: //ROR abs
    reg[0] = readAbs();
    reg[1] = reg[0] >> 1 | flags.c << 7;
    writeData(reg[1]);

    flags.c = reg[0] & 0b1;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x70: //BVS rel
    if (flags.v) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0x71: //ADC ind,Y
    reg[0] = readIndy();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x75: //ADC zpg,X
    reg[0] = readZpgx();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x76: //ROR zpg,X
    reg[0] = readZpgx();
    acc = reg[0] >> 1 | flags.c;

    flags.c = reg[0] & 0b1;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0x78: //SEI impl
    flags.i = 1;
    
    break;
  case 0x79: //ADC abs,Y
    reg[0] = readAbsy();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x7D: //ADC abs,X
    reg[0] = readAbsx();
    reg[1] = acc;
    reg[2] = reg[1] + reg[0] + flags.c;
    acc = reg[2];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.v = ((reg[0] | reg[1]) ^ acc) & 0b10000000;
    flags.n = acc & 0b10000000;
    break;
  case 0x7E: //ROR abs,X
    reg[0] = readAbsx();
    reg[1] = reg[0] >> 1 | flags.c;
    writeData(reg[1]);

    flags.c = reg[0] & 0b1;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
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

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0x8A: //TXA impl
    acc = x;
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
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
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
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
    
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0x99: //STA abs,Y
    writeAbsy(acc);
    
    break;
  case 0x9A: //TXS impl
    *stack = x;
    
    break;
  case 0x9D: //STA abs,X
    writeAbsx(acc);
    
    break;
  case 0xA0: //LDY #
    writeAddress(ptr++);
    y = readData();

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xA1: //LDA X,ind
    acc = readXind();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xA2: //LDX #
    writeAddress(ptr++);
    x = readData();

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xA4: //LDY zpg
    y = readZpg();

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xA5: //LDA zpg
    acc = readZpg();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xA6: //LDX zpg
    x = readZpg();

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xA8: //TAY impl
    y = acc;
    
    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xA9: //LDA #
    writeAddress(ptr++);
    acc = readData();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xAA: //TAX impl
    x = acc;
    
    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xAC: //LDY abs
    y = readAbs();

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xAD: //LDA abs
    acc = readAbs();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xAE: //LDX abs
    x = readAbs();

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xB0: //BCS rel
    if (flags.c) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0xB1: //LDA ind,Y
    acc = readIndy();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xB4: //LDY zpg,X
    y = readZpgx();

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xB5: //LDA zpg,X
    acc = readZpgx();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xB6: //LDX zpg,Y
    x = readZpgx();

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xB8: //CLV impl
    flags.v = 0;
    
    break;
  case 0xB9: //LDA abs,Y
    acc = readAbsy();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xBA: //TSX impl
    x = *stack;
    
    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xBC: //LDY abs,X
    y = readAbsx();

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xBD: //LDA abs,X
    acc = readAbsx();

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xBE: //LDX abs,Y
    x = readAbsy();

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xC0: //CPY #
    writeAddress(ptr++);
    reg[0] = y - readData();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xC1: //CMP X,ind
    reg[0] = acc - readXind();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xC4: //CPY zpg
    reg[0] = y - readZpg();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xC5: //CMP zpg
    reg[0] = acc - readZpg();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xC6: //DEC zpg
    reg[0] = readZpg() - 1;
    writeData(reg[0]);

    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xC8: //INY impl
    y++;

    flags.z = !y;
    flags.n = y & 0b10000000;
    break;
  case 0xC9: //CMP #
    writeAddress(ptr++);
    reg[0] = acc - readData();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xCA: //DEX impl
    x--;

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xCC: //CPY abs
    reg[0] = y - readAbs();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    
    break;
  case 0xCD: //CMP abs
    reg[0] = acc - readAbs();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xCE: //DEC abs
    reg[0] = readAbs() - 1;
    writeData(reg[0]);

    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xD0: //BNE rel
    if (!flags.z) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0xD1: //CMP ind,Y
    reg[0] = acc - readIndy();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xD5: //CMP zpg,X
    reg[0] = acc - readZpgx();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xD6: //DEC zpg,X
    reg[0] = readZpgx() - 1;
    writeData(reg[0]);

    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xD8: //CLD impl
    flags.d = 0;
    
    break;
  case 0xD9: //CMP abs,Y
    reg[0] = acc - readAbsy();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xDD: //CMP abs,X
    reg[0] = acc - readAbsx();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xDE: //DEC abs,X
    reg[0] = readAbsx() - 1;
    writeData(reg[0]);

    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xE0: //CPX #
    writeAddress(ptr++);
    reg[0] = x - readData();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xE1: //SBC X,ind
    reg[0] = acc - readXind() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xE4: //CPX zpg
    reg[0] = x - readZpg();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xE5: //SBC zpg
    reg[0] = acc - readZpg() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xE6: //INC zpg
    reg[0] = readZpg();
    writeData(reg[0] + 1);

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xE8: //INX impl
    x++;

    flags.z = !x;
    flags.n = x & 0b10000000;
    break;
  case 0xE9: //SBC #
    writeAddress(ptr++);
    reg[0] = acc - readData() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xEA: //NOP impl
    break;
  case 0xEC: //CPX abs
    reg[0] = x - readAbs();
    
    flags.c = (~reg[0]) & 0b10000000;
    flags.z = !reg[0];
    flags.n = reg[0] & 0b10000000;
    break;
  case 0xED: //SBC abs
    reg[0] = acc - readAbs() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xEE: //INC abs
    reg[0] = readAbs();
    writeData(reg[0] + 1);

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xF0: //BEQ rel
    if (!flags.z) {
      writeAddress(ptr);
      ptr += (int8_t) readData();
    } else {
      ptr++;
    }
    
    break;
  case 0xF1: //SBC ind,Y
    reg[0] = acc - readIndy() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xF5: //SBC zpg,X
    reg[0] = acc - readZpgx() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xF6: //INC zpg,X
    reg[0] = readZpgx();
    writeData(reg[0] + 1);

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xF8: //SED impl
    flags.d = 1;
    
    break;
  case 0xF9: //SBC abs,Y
    reg[0] = acc - readAbsy() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xFD: //SBC abs,X
    reg[0] = acc - readAbsx() - (1 - flags.c);
    acc = reg[0];

    flags.c = reg[0] & 0b100000000;
    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  case 0xFE: //INC abs,X
    reg[0] = readAbsx();
    writeData(reg[0] + 1);

    flags.z = !acc;
    flags.n = acc & 0b10000000;
    break;
  }
}

inline void writeAddress(uint16_t addr) {
  while (!CLOCK);
  
  PORTA |= addr;
  PORTC |= addr >> 8;
}

inline void clearAddress() {
  PORTA = 0;
  PORTC = 0;
}

inline void writeData(uint8_t data) {
  while (!CLOCK);
  
  DDRL = 0xFF;
  PORTL |= data;
}

inline uint8_t readData() {
  while (CLOCK);
  
  DDRL = 0;
  return PINL;
}

inline void clearData() {
  PORTL = 0;
}

//Address
inline void xind() {
  writeAddress(ptr++);                    //Init reading of oprand
  reg[0] = readData() + x;                //Read oprand, add x and store to register 0
  writeAddress(reg[0]);                   //Init reading of lower indirect address
  reg[1] = readData();                    //Read lower indirect address and store to register 1
  writeAddress(reg[0] + 1);               //Init reading of higer indirect address
  writeAddress(reg[1] | readData() << 8); //Set address to indirect location
}

inline void indy() {
  writeAddress(ptr++);                          //Init reading of oprand
  reg[0] = readData();                          //Read oprand and store to register 0
  writeAddress(reg[0]);                         //Init reading of lower indirect address
  reg[1] = readData();                          //Read lower indirect address and store to register 1
  writeAddress(reg[0] + 1);                     //Init reading of higer indirect address
  writeAddress((reg[0] | readData() << 8) + y); //Set address to inderect location plus y
}

inline void zpg() {
  writeAddress(ptr);        //Init reading of oprand
  writeAddress(readData()); //Set address to oprand location
}

inline void abso() {
  writeAddress(ptr++);                    //Init reading of lower oprand
  reg[0] = readData();                    //Read lower oprand and store to register 0
  writeAddress(ptr++);                    //Init reading of higher oprand
  writeAddress(reg[0] | readData() << 8); //Combine lower oprand and higher oprand and set address to that location
}

inline void zpgx() {
  writeAddress(readZpg() + x);
}

inline void zpgy() {
  writeAddress(readZpg() + y);
}


inline void absx() {
  writeAddress(readAbs() + x);
}

inline void absy() {
  writeAddress(readAbs() + y);
}

//Read
inline uint8_t readXind() {
  xind();
  return readData();
}

inline uint8_t readIndy() {
  indy();
  return readData();
}

inline uint8_t readZpg() {
  zpg();
  return readData();
}

inline uint8_t readAbs() {
  abso();
  return readData();
}

inline uint8_t readZpgx() {
  zpgx();
  return readData();
}

inline uint8_t readZpgy() {
  zpgy();
  return readData();
}

inline uint8_t readAbsx() {
  absx();
  return readData();
}

inline uint8_t readAbsy() {
  absy();
  return readData();
}

//Write
inline void writeXind(uint8_t data) {
  xind();
  writeData(data);
}

inline void writeIndy(uint8_t data) {
  indy();
  writeData(data);
}

inline void writeZpg(uint8_t data) {
  zpg();
  writeData(data);
}

inline void writeAbs(uint8_t data) {
  abso();
  writeData(data);
}

inline void writeZpgx(uint8_t data) {
  zpgx();
  writeData(data);
}

inline void writeZpgy(uint8_t data) {
  zpgy();
  writeData(data);
}

inline void writeAbsx(uint8_t data) {
  absx();
  writeData(data);
}

inline void writeAbsy(uint8_t data) {
  absy();
  writeData(data);
}

