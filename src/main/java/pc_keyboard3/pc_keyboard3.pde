import jp.crestmuse.cmx.commands.*;
import jp.crestmuse.cmx.amusaj.sp.*;
import jp.crestmuse.cmx.processing.*;
  
CMXController cmx = CMXController.getInstance();
MidiEventSender midiSender;

boolean[] isNowOn = new boolean[65536];
boolean getRange = false;
boolean setKeys = false;
boolean first = false;
boolean octaveUp = false;
boolean octaveDown = false;
boolean farte = false;
boolean piano = false;
boolean Mac = false;
int[] notenums = new int[65536];
int octave = 12;
int note = 0;
int baseVel = 100;
int vel = 20;
int left, n, tmp;
int rectX, rectY, rectW, rectH, keyW;
int a=0, b=0, c=0, d=0;
int[] div = new int[8];
int[] blackKeys = new int[8];
String names = "567890-^";
String whiteKeys_1 = "rtyuiop@[";
String whiteKeys_2 = "fghjkl;:]";
String pitchName = "";

void setup() {
  /*
  MidiInputModule vk = cmx.createVirtualKeyboard();
  MidiOutputModule mo = cmx.createMidiOut();
  cmx.addSPModule(vk);
  cmx.addSPModule(mo);
  cmx.connect(vk, 0, mo, 0);
  cmx.startSP();
  */
  //cmx.showMidiOutChooser(this);
  PFont font = createFont("Meiryo", 50);
  textFont(font);
  println("Input chord(C:'1',D:'2',E:'3',F:'4',G:'5',A:'6',B:'7'):");
  size(1200, 700);
  rectX = 50;
  rectY = 200;
  rectW = 900;
  rectH = 200;
}  
  
void draw() {
  background(255);
  fill(0);
  textSize(20);
  text("スペースキー：１オクターブ上げる", rectX, 50);
  text("「c」キー：１オクターブ下げる", rectX, 70);
  text("「１」キー：強調", rectX, 90);
  text("「ALT」キー：弱める", rectX, 110);  
  
  fill(255);
  rect(50, 200, 900, 200);
  keyW = rectW/18;
  for(int i = 0; i < 9; i++) {
    if(!octaveUp) {
      if(keyPressed) {
        if(key == whiteKeys_1.charAt(i) || key == whiteKeys_2.charAt(i)) {
          fill(0);
        }else {
          fill(255);
        }
        rect(rectX+keyW*i, rectY, keyW, rectH);
      }else {
        fill(255);
        rect(rectX+keyW*i, rectY, keyW, rectH);
      }
    }else {
      fill(255);
      rect(rectX+keyW*i, rectY, keyW, rectH);
    }
  }
  for(int j = 9; j < 18; j++) {
    if(octaveUp) {
      if(keyPressed) {
        if(key == whiteKeys_1.charAt(j-9) || key == whiteKeys_2.charAt(j-9)) {
          fill(0);
        }else {
          fill(255);
        }
        rect(rectX+keyW*j, rectY, keyW, rectH);
      }
    }else {
      fill(255);
      rect(rectX+keyW*j, rectY, keyW, rectH);
    }
  }
  if(setKeys) {
    fill(255);
    for(int i = 0; i < names.length(); i++) {
      if(notenums[names.charAt(i)] != 0) {
        if(keyPressed) {
          if(octaveUp) {
            if(key == names.charAt(i)) {
              fill(0);
              rect(rectX+keyW*(0.75+i+7), rectY, keyW/2, rectH/2);
              fill(255);
              rect(rectX+keyW*(0.75+i), rectY, keyW/2, rectH/2);
            }else {
              fill(255);
              rect(rectX+keyW*(0.75+i), rectY, keyW/2, rectH/2);
              rect(rectX+keyW*(0.75+i+7), rectY, keyW/2, rectH/2);
            }
          }else {
            if(key == names.charAt(i)) {
              fill(255);
              rect(rectX+keyW*(0.75+i+7), rectY, keyW/2, rectH/2);
              fill(0);
              rect(rectX+keyW*(0.75+i), rectY, keyW/2, rectH/2);
            }else {
              fill(255);
              rect(rectX+keyW*(0.75+i), rectY, keyW/2, rectH/2);
              rect(rectX+keyW*(0.75+i+7), rectY, keyW/2, rectH/2);
            }
          }
        }else {
          fill(255);
          rect(rectX+keyW*(0.75+i), rectY, keyW/2, rectH/2);
          rect(rectX+keyW*(0.75+i+7), rectY, keyW/2, rectH/2);
        }
      }
    }
    /*
    rect(rectX+keyW*0.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*1.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*3.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*4.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*5.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*7.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*8.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*10.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*11.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*12.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*14.75, rectY, keyW/2, rectH/2);
    rect(rectX+keyW*15.75, rectY, keyW/2, rectH/2);
    */
  }
  
  if(setKeys) {
    fill(0);
    
    text(pitchName, width/2, rectH/2);
    if(keyPressed) {
      text(key, width*3/4, rectH/2);
    }
  }
}

void keyPressed() {
  
  if (!isNowOn[key]) {
    if(!getRange) {
      note = setBlackKeys();
      println(note);
      getRange = true;
    }else {
      if(!setKeys) {
        left = note;
        n = 0;
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
  
        for(int i = 0; i < names.length(); i++) {
          notenums[names.charAt(i)] = blackKeys[i];
        }
    
        notenums['2'] = 48;
        notenums['q'] = 50;
        notenums['w'] = 52;
        notenums['a'] = 53;
        notenums['s'] = 55;
        notenums['z'] = 57;
        notenums['x'] = 59;
  
  
        midiSender = new MidiEventSender();
        MidiOutputModule midiout = cmx.createMidiOut();
        cmx.addSPModule(midiSender);
        cmx.addSPModule(midiout);
        cmx.connect(midiSender, 0, midiout, 0);
        cmx.startSP();
        setKeys = true;

      }else {
        
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
        println("chord:" + a + " " + b + " " + c + " melody:" + d);
      }
    }
  }  
}

void keyReleased() {
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
    pitchName = "";
    isNowOn[key] = false;
  }
}

int setBlackKeys() {
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

int setKeyMode() {
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
