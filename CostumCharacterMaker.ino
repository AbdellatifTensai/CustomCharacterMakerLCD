#define serialPin 2
#define latchPin 3
#define clckPin 4
#define enablePin 5
#define readWritePin 6
#define registerSelectPin 7

/*<10001>1<01010>2<00100>3<01010>4<10001>5<00000>6<11111>7<00000>8
  <00100>9<01010>10<10101>11<01010>12<00100>13<01010>14<10101>15<10101>16
  <00001>17<00010>18<00010>19<10100>20<10100>21<01000>22<10110>23<00000>24
  <10001>25<01010>26<01010>27<01010>28<01010>29<10001>30<10001>31<01110>32
  <00100>33<00100>34<00100>35<01010>36<10001>37<10001>38<10001>39<01110>40
  <10101>41<11011>42<10001>43<10101>44<11011>45<10101>46<10001>47<01110>48
  <00001>49<00010>50<00010>51<00001>52<00010>53<00010>54<00100>55<00000>56
  <10000>57<01000>58<01000>59<10000>60<01000>61<01000>62<00100>63<00000>64*/

char receivedByte[8];
byte received;
int loc;

void setup() {
  Serial.begin(115200);
  pinMode(serialPin, OUTPUT);
  pinMode(latchPin, OUTPUT);
  pinMode(clckPin, OUTPUT);
  pinMode(enablePin, OUTPUT);
  pinMode(readWritePin, OUTPUT);
  pinMode(registerSelectPin, OUTPUT);
//    while(1){writeData(0xFF);}
  setUp();
  Serial.println(F("write something"));
}

void loop() {
  if (getData())makeChar(received);
}
bool getData() {
  static bool incomingData = false;
  static bool incomingLoc = false;
  int r = Serial.read();
  r != -1 ? Serial.print((char)r) : false;
  switch (r) {
    case '<': incomingData = true; received = 0; break;
    case '>': incomingData = false; incomingLoc = true; break;
    case '0':
      if (incomingData) received <<= 1;
      break;
    case '1':
      if (incomingData) {
        received <<= 1;
        received |= 1;
      }
      break;
    case -1: return false; break;
    default:  break;
  }

  if (!incomingData && incomingLoc) {
    loc = Serial.parseInt();
    incomingLoc = false;
    return true;
  }
  return false;
}

void writeInst(byte value) {
  digitalWrite(readWritePin, LOW);
  digitalWrite(registerSelectPin, LOW);
  digitalWrite(latchPin, LOW);
  shiftOut(serialPin, clckPin, MSBFIRST, value);
  digitalWrite(latchPin, HIGH);
  delay(2);
  digitalWrite(enablePin, HIGH);
  digitalWrite(enablePin, LOW);
}

void writeData(byte value) {
  digitalWrite(readWritePin, LOW);
  digitalWrite(registerSelectPin, HIGH);
  digitalWrite(latchPin, LOW);
  shiftOut(serialPin, clckPin, MSBFIRST, value );
  digitalWrite(latchPin, HIGH);
  delay(2);
  digitalWrite(enablePin, HIGH);
  digitalWrite(enablePin, LOW);
}

void makeChar(byte pattern) {
  Serial.println(F("")); Serial.print(F("Location: ")); Serial.println(loc);
  writeInst(0x40 + loc);
  writeData(pattern);
}
void setUp() {
  writeInst(0x01);
  writeInst(0x38);
  writeInst(0x0C);

  writeInst(0x80);
  writeData(0x00);

  writeInst(0x81);
  writeData(0x01);

  writeInst(0x82);
  writeData(0x02);

  writeInst(0x83);
  writeData(0x03);

  writeInst(0xC0);
  writeData(0x04);

  writeInst(0xC1);
  writeData(0x05);

  writeInst(0xC2);
  writeData(0x06);

  writeInst(0xC3);
  writeData(0x07);

}


