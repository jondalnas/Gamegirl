
void setup() {

}

void loop() {

}

inline void writeAddress(uint16_t addr) {
  PORTA |= addr & 0b11111111;
  PORTC |= (addr >> 8) & 0b11111111;
}

inline void clearAddress() {
  PORTA = 0;
  PORTC = 0;
}

inline void writeData(uint8_t data) {
  PORTL |= data;
}

inline uint8_t readData() {
  return PINA;
}

inline  void clearData() {
  PORTL = 0;
}

