import processing.core.*;
import jp.crestmuse.cmx.commands.*;
import jp.crestmuse.cmx.amusaj.sp.*;
import jp.crestmuse.cmx.processing.*;

public class pc_keyboard3 extends PApplet {  

  CMXController cmx = CMXController.getInstance();
  MidiEventSender midiSender;
  ModelServer ms;

  boolean[] isNowOn = new boolean[65536];
  boolean[] Pressing = new boolean[65536];
  boolean getRange = false;
  boolean setKeys = false;
  boolean playable = false;
  boolean first = false;
  boolean octaveUp = false;
  boolean octaveDown = false;
  boolean farte = false;
  boolean piano = false;
  boolean Mac = false;
  int[] notenums = new int[65536];
  int octave = 12;
  int note = 0;
  int baseVel = 0;
  int baseTime = 0;
  int prev_vel = 0;
  int vel = 20;
  int left;
  int rectX, rectY, rectW, rectH, keyW;
  int a=0, b=0, c=0, d=0;
  float times = 0.75f;
  float noteOnTime = 0.0f;
  float noteOffTime = 0.0f;
  float prev_note_len = 0.0f;
  float output;
  int[] div = new int[8];
  int[] blackKeys = new int[8];
  String names = "567890-^";
  String whiteKeys_1 = "rtyuiop@[";
  String whiteKeys_2 = "fghjkl;:]";
  String pitchName = "";

  public void settings() {
    size(1200, 700);
    ms = new ModelServer();
    rectX = 50;
    rectY = 200;
    rectW = 900;
    rectH = 200;
  }
  
  public void setup() {
    PFont font = createFont("Meiryo", 50);
    textFont(font);
    println("Input chord(C:'1',D:'2',E:'3',F:'4',G:'5',A:'6',B:'7'):");
    
  }
  
  public void draw() {
    background(255);
    fill(0);
    textSize(20);
    text("スペースキー：１オクターブ上げる", rectX, 50);
    text("「c」キー：１オクターブ下げる", rectX, 70);
    text("「１」キー：強調", rectX, 90);
    text("「ALT」キー：弱める", rectX, 110);  
  
    fill(255);
    rect(rectX, rectY, rectW, rectH);
    keyW = rectW/18;
    drawLeftKeys();
    for(int j = 9; j < 18; j++) {
      drawRightKeys(j);
    }
    if(setKeys) {
      for(int i = 0; i < names.length(); i++) {
            if(Pressing[names.charAt(i)]) {
              fill(0);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);

              fill(255);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            }else {
              fill(255);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            }
          
        }

      drawPlayingKeys();
      printKey();
    }
    
    
    
  }
  
  public void drawLeftKeys() {
    for(int i = 0; i < 9; i++) {
      if(!octaveUp) {
        if(setKeys) {
          if(Pressing[whiteKeys_1.charAt(i)] || Pressing[whiteKeys_2.charAt(i)]) {
            fill(0);
          }else {
            fill(255);
          }
        }
        rect(rectX+keyW*i, rectY, keyW, rectH);
      }else {
        fill(255);
        rect(rectX+keyW*i, rectY, keyW, rectH);
      }
    /*  
    }else {
      fill(255);
      rect(rectX+keyW*i, rectY, keyW, rectH);
    }
    */
    }
  }
  
  public void drawRightKeys(int n) {
    if(octaveUp) {
      if(setKeys) {
        if(Pressing[whiteKeys_1.charAt(n-9)] || Pressing[whiteKeys_2.charAt(n-9)]) {
          fill(0);
        }else {
          fill(255);
        }
        rect(rectX+keyW*n, rectY, keyW, rectH);
      }
    }else {
      fill(255);
      rect(rectX+keyW*n, rectY, keyW, rectH);
    }
  }
  
  public void drawPlayingKeys() {
  
    fill(255);
    for(int i = 0; i < names.length(); i++) {
    
      if(notenums[names.charAt(i)] != 0) {
        
        if(octaveUp) {
          
            if(Pressing[names.charAt(i)]) {
              fill(0);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
              fill(255);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
            }else {
              fill(255);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            }
        }else {
            if(Pressing[names.charAt(i)]) {
              fill(0);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);

              fill(255);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            }else {
              fill(255);
              rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
              rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            }
          
        }
        
      }else {
        if(blackKeys[i] != 0) {
          fill(255);
          rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
          rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
        }
      }
 
    
    }
  
  }
  
  public void printKey() {
    fill(0);
    text(pitchName, width/2, rectH/2);
    if(Pressing[key]) {
      text(key, width*3/4, rectH/2);
    }
  }

  public void keyPressed() {
    
    if (!isNowOn[key]) {
      if(!getRange) {
        note = setBlackKeys();
        println(note);
        getRange = true;
      }else {
        if(!setKeys) {
          left = note;
          for(int i = 0; i < div.length; i++) {
            if(left % 12 == 4 || left % 12 == 11) {
              div[i] = 1;
              blackKeys[i] = 0;
            }else {
              div[i] = 2;
              blackKeys[i] = left+1; 
            }
            left += div[i];
          }    
          setKeyDisplay(note);
            
          for(int i = 0; i < names.length(); i++) {
            notenums[names.charAt(i)] = blackKeys[i];
          }
          
          setChord();
            
          midiSender = new MidiEventSender();
          MidiOutputModule midiout = cmx.createMidiOut();
          cmx.addSPModule(midiSender);
          cmx.addSPModule(midiout);
          cmx.connect(midiSender, 0, midiout, 0);
          cmx.startSP();
          setKeys = true;
          Pressing[key] = false;

        }else {
          noteOnTime = millis();
          Pressing[key] = true;
          playable = true;
          switch(key) {
            case '2':
            case 'a':
            case 's':
              switch(key) {
                case '2':
                  pitchName += "C";
                  break;
                case 'a':
                  pitchName += "F";
                  break;
                case 's':
                  pitchName += "G";
                  break;
              }
              a = notenums[key];
              b = a+4;
              c = a+7;
              d = 0;
              if(octaveUp) {
                a += octave;
                b += octave;
                c += octave;
              }else if(octaveDown) {
                a -= octave;
                b -= octave;
                c -= octave;
              }
              midiSender.sendNoteOn(0, 0, a, baseVel);
              midiSender.sendNoteOn(0, 0, b, baseVel);
              midiSender.sendNoteOn(0, 0, c, baseVel);
              break;
            case 'q':
            case 'w':
            case 'z':
              switch(key) {
                case 'q':
                  pitchName += "Dm";
                  break;
                case 'w':
                  pitchName += "Em";
                  break;
                case 'z':
                  pitchName += "Am";
                  break;
              }
              a = notenums[key];
              b = a+3;
              c = a+7;
              d = 0;
              if(octaveUp) {
                a += octave;
                b += octave;
                c += octave;
              }else if(octaveDown) {
                a -= octave;
                b -= octave;
                c -= octave;
              }
            
              midiSender.sendNoteOn(0, 0, a, baseVel);
              midiSender.sendNoteOn(0, 0, b, baseVel);
              midiSender.sendNoteOn(0, 0, c, baseVel);
              break;
            case 'x':
              pitchName += "Bm-5";
              a = notenums[key];
              b = a+3;
              c = a+6;
              d = 0;
              if(octaveUp) {
                a += octave;
                b += octave;
                c += octave;
              }else if(octaveDown) {
                a -= octave;
                b -= octave;
                c -= octave;
              }
              midiSender.sendNoteOn(0, 0, a, baseVel);
              midiSender.sendNoteOn(0, 0, b, baseVel);
              midiSender.sendNoteOn(0, 0, c, baseVel);
              break;
            default:
              d = notenums[key];
              //println(d + " " + noteOnTime + " " + noteOffTime + " " + prev_vel + " " + prev_note_len);
              //ModelServer ms = new ModelServer(d, noteOnTime, noteOffTime, prev_vel, prev_note_len);
              
              ms.setNoteNumber(d);
              ms.setNoteOnTime(noteOnTime);
              ms.setNoteOffTime(noteOffTime);
              ms.setPrev_velocity(prev_vel);
              ms.setPrev_note_len(prev_note_len);
              
              ms.predict();
              output = ms.getOutput();
              baseVel = round(output);
                            
              if(octaveUp) {
                midiSender.sendNoteOn(0, 0, (d+octave), baseVel);
              }else if(octaveDown) {
                if(notenums[key]-octave > 0) {
                  midiSender.sendNoteOn(0, 0, (d-octave), baseVel);
                }
              }else {
                
                
                if(farte) {
                  midiSender.sendNoteOn(0, 0, d, baseVel+vel);
                }else if(piano) {
                  midiSender.sendNoteOn(0, 0, d, baseVel-vel);
                }else {
                  midiSender.sendNoteOn(0, 0, d, baseVel);
                  
                }
              }
              break;
          }
          
      
          if(key == ' ') {
            octaveUp = true;
          }
          if(key == 'c') {
            octaveDown = true;
          }
          if(key == '1') {
            farte = true; 
          }
          if(Mac) {
            if(keyCode == CONTROL) {
              piano = true;
            }
          }else {
            if(keyCode == ALT) {
              piano = true;
            }
          }
          isNowOn[key] = true;
          Pressing[key] = true;
          println("chord:" + a + " " + b + " " + c + " melody:" + d + " velocity:" + baseVel);
          println("noteOn:" + noteOnTime / 1000);
        }
      }
    }  
  }

  public void keyReleased() {
    
    if(setKeys) {
      
      switch(key) {    
        case '2':
        case 'a':
        case 's':
          //chord
          a = notenums[key];
          b = a+4;
          c = a+7;
          if(octaveUp) {
            a += octave;
            b += octave;
            c += octave;
          }else if(octaveDown) {
            a -= octave;
            b -= octave;
            c -= octave;
          }
          midiSender.sendNoteOff(0, 0, a, baseVel);
          midiSender.sendNoteOff(0, 0, b, baseVel);
          midiSender.sendNoteOff(0, 0, c, baseVel);
          a = 0;
          b = 0;
          c = 0;
          break;
        case 'q':
        case 'w':
        case 'z':
          a = notenums[key];
          b = a+3;
          c = a+7;
          if(octaveUp) {
            a += octave;
            b += octave;
            c += octave;
          }else if(octaveDown) {
            a -= octave;
            b -= octave;
            c -= octave;
          }
          midiSender.sendNoteOff(0, 0, a, baseVel);
          midiSender.sendNoteOff(0, 0, b, baseVel);
          midiSender.sendNoteOff(0, 0, c, baseVel);
          a = 0;
          b = 0;
          c = 0;
          break;
        case 'x':
          a = notenums[key];
          b = a+3;
          c = a+6;
          if(octaveUp) {
            a += octave;
            b += octave;
            c += octave;
          }else if(octaveDown) {
            a -= octave;
            b -= octave;
            c -= octave;
          }
          midiSender.sendNoteOff(0, 0, a, baseVel);
          midiSender.sendNoteOff(0, 0, b, baseVel);
          midiSender.sendNoteOff(0, 0, c, baseVel);
          a = 0;
          b = 0;
          c = 0;
          break;
        default:
          d = notenums[key];
          
          if(octaveUp) {
            midiSender.sendNoteOff(0, 0, (d+octave), baseVel);
          }else if(octaveDown) {
            if(notenums[key]-octave > 0) {
              midiSender.sendNoteOff(0, 0, (d-octave), baseVel);
            }  
          }else {
            if(farte) {
              midiSender.sendNoteOff(0, 0, d, baseVel+vel);
            }else if(piano) {
              midiSender.sendNoteOff(0, 0, d, baseVel-vel);
            }else {
              midiSender.sendNoteOff(0, 0, d, baseVel);
              
            }
          }
          d = 0;
          break;
      }
      if(playable) {
        noteOffTime = millis();
        println("noteOff:" + noteOffTime / 1000 + " seconds");
        prev_note_len = noteOffTime - noteOnTime;  
        prev_vel = baseVel;
      }
      if(key == ' ') {
        octaveUp = false;
      }
      if(key == 'c') {
        octaveDown = false;
      }
      if(key == '1') {
        farte = false;
      }
      if(keyCode == ALT) {
        piano = false;
      }
      
      //println("velocity:"+ baseVel);
      
      pitchName = "";
      isNowOn[key] = false;
      Pressing[key] = false;
    }
  }
    
  public void setKeyDisplay(int note) {
    notenums['r'] = note;
    notenums['f'] = notenums['r'];
    notenums['t'] = notenums['r']+div[0];
    notenums['g'] = notenums['t'];
    notenums['6'] = blackKeys[0];
    notenums['y'] = notenums['t']+div[1];
    notenums['h'] = notenums['y'];
    notenums['7'] = blackKeys[1];
    notenums['u'] = notenums['y']+div[2];
    notenums['j'] = notenums['u'];
    notenums['i'] = notenums['u']+div[3];
    notenums['k'] = notenums['i'];
    notenums['9'] = blackKeys[2];
    notenums['o'] = notenums['i']+div[4];
    notenums['l'] = notenums['o'];
    notenums['0'] = blackKeys[3];
    notenums['p'] = notenums['o']+div[5];
    notenums[';'] = notenums['p'];
    notenums['-'] = blackKeys[4];
    notenums['@'] = notenums['p']+div[6];
    notenums[':'] = notenums['@'];
    notenums['['] = notenums['@']+div[7];
    notenums[']'] = notenums['['];
  }
  
  public void setChord() {
    notenums['2'] = 48;
    notenums['q'] = 50;
    notenums['w'] = 52;
    notenums['a'] = 53;
    notenums['s'] = 55;
    notenums['z'] = 57;
    notenums['x'] = 59;
  }

  public int autoModify(int a, int b, int c, int d) {
    if(abs(a-d) == 1 || abs(a-d) == 11 || abs(b-d) % 12 == 1 || abs(b-d) == 11 || abs(c-d) == 1 || abs(c-d) == 11 || abs(c-d) == 13) {
      if(d % 12 == 0) {
        d -= 1;
      }else if(d % 12 == 4) {
        d += 1;
      }else if(d % 12 == 5) {
        d -= 1;
      }else if(d % 12 == 11) {
        d += 1;
      }              
    }
    return d;
  }

  public int setBlackKeys() {
    int min;
    switch(key) {
      case '1':
        min = 48;
        break;
      case '2':
        min = 50;
        break;
      case '3':
        min = 52;
        break;
      case '4':
        min = 53;
        break;
      case '5':
        min = 55;
        break;
      case '6':
        min = 57;
        break;
      case '7':
        min = 59;
        break;
      default:
        min = 0;
        break;
    }
    return min;
  }

  public int setKeyMode() {
    int mode;
    if(key == 'm') {
      mode = 2;
    }else if(key == 'w'){
      mode = 1;
    }else {
      mode = 0;
    }
    return mode;
  }
  
  public static void main(String[] args) {
    PApplet.main("pc_keyboard3");
  }
}