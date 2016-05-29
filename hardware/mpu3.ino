#include "Wire.h"
#include <SoftwareSerial.h>
#include <EEPROM.h>

SoftwareSerial connection(10, 11); //(TX in blue to 10, RX to 11)

// I2Cdev and MPU6050 must be installed as libraries, or else the .cpp/.h files
// for both classes must be in the include path of your project
#include "I2Cdev.h"
#include "MPU6050.h"

// class default I2C address is 0x68
// specific I2C addresses may be passed as a parameter here
// AD0 low = 0x68 (default for InvenSense evaluation board)
// AD0 high = 0x69
MPU6050 accelgyro;

const int flexPin = A0;
int timerCount = 0;
int flexVal = 0;
int mapVal = 0;
char recVal = 0;
int count1 = 0;
int count2 = 0;
int16_t ax, ay, az;
int16_t gx, gy, gz;

int16_t steps = 0;
unsigned char stepInit = 0;
unsigned char preStep = 0;

#define LED_PIN 13
bool blinkState = false;

// accelerometer values
int accel_reading;
int accel_corrected;
int ac;
int accel_offset = 200;
float accel_angle;
float accel_scale = 1; // set to 0.01

// gyro values
int gyro_offset = 151; // 151
int gyro_corrected;
int gyro_reading;
float gyro_rate;
float gyro_scale = 0.02; // 0.02 by default - tweak as required
float gyro_angle;
float loop_time = 0.05; // 50ms loop
float angle = 0.00; // value to hold final calculated gyro angle

// time stamp variables
int last_update;
int cycle_time;
long last_cycle = 0;

void setup() {
  // join I2C bus (I2Cdev library doesn't do this automatically)
Wire.begin();

// initialize serial communication
Serial.begin(9600);
connection.begin(9600);
accelgyro.initialize();

  pinMode(8,OUTPUT);
 steps = EEPROM.read(1);
}

void loop(){  
    digitalWrite(9,LOW);
  // read raw accel/gyro measurements from device
  accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);

  // accelerometer_Y_Axis angle calc
  accel_reading = ay;
  accel_corrected = accel_reading - accel_offset;
  accel_corrected = map(accel_corrected, -16800, 16800, -90, 90);
  accel_corrected = constrain(accel_corrected, -90, 90);
  ac = accel_corrected;
  accel_angle = (float)(accel_corrected * accel_scale);

  // accelerometer_X_Axis angle calc
  accel_reading = ax;
  accel_corrected = accel_reading - accel_offset;
  accel_corrected = map(accel_corrected, -16800, 16800, -90, 90);
  accel_corrected = constrain(accel_corrected, -90, 90);
  accel_angle = (float)(accel_corrected * accel_scale);
//  Serial.print("accel_X = ");
//  Serial.print(accel_corrected);
//  Serial.print("\t");

  
  //Serial.print("accel_Y = ");
  //Serial.println(accel_corrected);
   
  // gyro_Y_Axis angle calc  
//  gyro_reading = gx;
//  gyro_corrected = (float)((gyro_reading/131) - gyro_offset);  // 131 is sensivity of gyro from data sheet
//  gyro_rate = (gyro_corrected * gyro_scale) * -loop_time;      // loop_time = 0.05 ie 50ms        
//  gyro_angle = angle + gyro_rate;  
//  
//  gyro_reading = gy;
//  gyro_corrected = (float)((gyro_reading/131) - gyro_offset);  // 131 is sensivity of gyro from data sheet
//  gyro_rate = (gyro_corrected * gyro_scale) * -loop_time;      // loop_time = 0.05 ie 50ms        
//  gyro_angle = angle + gyro_rate;
  
  gyro_reading = gz;
  gyro_corrected = (float)((gyro_reading/131) - gyro_offset);  // 131 is sensivity of gyro from data sheet
  gyro_rate = (gyro_corrected * gyro_scale) * -loop_time;      // loop_time = 0.05 ie 50ms        
  gyro_angle = angle + gyro_rate;
    
//   print values to serial monitor for checking 
//  Serial.print(" gyro_reading = ");
//  Serial.print(gyro_reading);
//  Serial.print("\t");
//  Serial.print(" gyro_corrected = ");
//  Serial.print(gyro_corrected);
//  Serial.print("\t");
//  Serial.print(" gyro_angle = ");
//  Serial.println(gyro_angle);
//  Serial.print("\t");
//  Serial.println(" ");

  if (ac > 56) {
      stepInit++;      
      digitalWrite(9,HIGH);
      delay(200);     
      digitalWrite(9,LOW);
  }

  if (stepInit >= 10 || steps > 1) {
    if (ac > 56) {   
      steps++;
    }      
  }

  if (steps % 10 == 0 && preStep != (steps/10)) {
    preStep = steps/10;
    EEPROM.write(1, steps);
    //connection.println(steps);
//    Serial.print("Steps = ");
//    Serial.println(steps);
  }

  timerCount++;
  
  if (gyro_angle >= 0.17 || gyro_angle <= 0.13) {
    timerCount = 0;
    digitalWrite(8,LOW);
  }

  if (timerCount >= 150) {
    int i = 1;
    digitalWrite(8,HIGH);
    connection.println('1');
    delay(750);
    digitalWrite(8,LOW);
    timerCount = 0;    
  } 
  
  recVal = connection.read();

  if (recVal == 'n') {
    steps = 0;
    stepInit = 0;
    connection.println('n');
  } else if (recVal == 'f') {
    //Serial.println("ftest");
  } else if (recVal == 'e') {
    //Serial.println("etest");    
  }
  
//timestamp
time_stamp();


  if (accel_angle < -35){
    count1++;
    //Serial.print("count ");
    //Serial.println(count2);
    if (count1 >= 4){
      count1 = 0;
      count2++;
     } 
  } 
  if (count2 >=2) {
    int i = 1;
    digitalWrite(8,HIGH);
    connection.println('1');
    delay(750);
    //Serial.println("sleep");
    count2 = 0;
  }

  if (count2 == 0){
  digitalWrite(8,LOW);
 } 
}
void time_stamp(){
  while ((millis() - last_cycle) < 50){
  delay(1);
  }
  // once loop cycle reaches 50ms, reset timer value and continue
  cycle_time = millis() - last_cycle;
  last_cycle = millis();
}
