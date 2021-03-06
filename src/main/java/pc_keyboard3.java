import processing.core.*;
import jp.crestmuse.cmx.commands.*;
import jp.crestmuse.cmx.amusaj.sp.*;
import jp.crestmuse.cmx.processing.*;
import java.util.ArrayList;

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
  int changed_vel = 0;
  int left;
  int rectX, rectY, rectW, rectH, keyW;
  int a=0, b=0, c=0, d=0;
  int number = 0;
  int count;
  float times = 0.75f;
  float predict_time = 0.0f;
  float noteOnTime = 0.0f;
  float noteOffTime = 0.0f;
  float execTime = 0.0f;
  float sumExec = 0.0f;
  float meanExec = 0.0f;
  float prev_note_len = 0.0f;
  float output;
  int[] div = new int[8];
  int[] blackKeys = new int[8];
  String names = "567890-^";
  String whiteKeys_1 = "rtyuiop@[";
  String whiteKeys_2 = "fghjkl;:]";
  String pitchName = "";
  ArrayList<Float> execTL = new ArrayList<Float>();

  public void settings() {
    size(1200, 700);
    ms = new ModelServer();
    rectX = 50;
    rectY = 200;
    rectW = 900;
    rectH = 200;
  }
  
  public void setup() {
    cmx.showMidiOutChooser(null);
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
    
    if(!setKeys) {
      fill(0);
      textSize(20);
      text("１～７のどれかを入力して一番低い音を決めてください", rectX*2, height/2 - 50);
      text("１：「ド」、２：「レ」、３：「ミ」、４：「ファ」、５：「ソ」、６：「ラ」、７：「シ」", rectX*2, height/2 + 50);
    }  
  
    fill(255);
    keyW = rectW/18;
    if(setKeys) {
      rect(rectX, rectY, rectW-keyW*2, rectH);
      drawLeftKeys();
      drawRightKeys();
      printRange();
    }
    
        
    if(setKeys) {
      drawPlayingKeys();
      printKey();
    }
    
  }
  
  public void drawLeftKeys() {
    for(int i = 0; i < whiteKeys_1.length(); i++) {
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
    }
  }
  
  public void drawRightKeys() {
    for(int i = whiteKeys_1.length(); i < whiteKeys_1.length() * 2; i++) {
      if(octaveUp) {
        if(setKeys) {
          if(Pressing[whiteKeys_1.charAt(i-9)] || Pressing[whiteKeys_2.charAt(i-9)]) {
            fill(0);
          }else {
            fill(255);
          }
          rect(rectX+keyW*(i-2), rectY, keyW, rectH);
        }
        
      }
    }
    for(int i = whiteKeys_1.length(); i < whiteKeys_1.length() * 2 - 2; i++) {
      if(!octaveUp) {
      
        fill(255);
        rect(rectX+keyW*i, rectY, keyW, rectH);
      }
    }
  }
  
  public void drawPlayingKeys() {
  
    fill(255);
    //names = '567890-^'
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
            fill(255);
            rect(rectX+keyW*(times+i+7), rectY, keyW/2, rectH/2);
            fill(0);
            rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
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
    for(int i = 7; i < 15; i++) {
      if(notenums[names.charAt(i-7)] != 0) {
        if(octaveUp) {
          if(Pressing[names.charAt(i-7)]) {
            fill(0);
            rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
          }else {
            fill(255);
            rect(rectX+keyW*(times+i), rectY, keyW/2, rectH/2);
          }
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
      if(!setKeys) {
        note = getMinBlackKey();
        println(note);

        left = note;
        decideBlackKeys(left);
        setKeyDisplay(note);
        setBlackKeys();    
                  
        setChord();
        startCmx();    
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
            noteOnCFG();
            break;
          case 'q':
          case 'w':
          case 'z':
            noteOnDmEmAm();
            break;
          case 'x':
            noteOnBm_5();
            break;
          default:
            number = notenums[key];
            d = octaveChange(number);  
            execPrediction();               
            changed_vel = velChange();                      
            midiSender.sendNoteOn(0, 0, d, changed_vel);  
            break;
        }          
        noteOnEffects();
        isNowOn[key] = true;
        Pressing[key] = true;
        if(notenums[key] != 0) {
          println("chord:" + a + " " + b + " " + c + " melody:" + d + " velocity:" + baseVel);
        }
        println("noteOn:" + noteOnTime / 1000);
      }
    }  
  }

  public void keyReleased() {
    
    if(setKeys) {
      switch(key) {    
        case '2':
        case 'a':
        case 's':
          noteOffCFG();
          break;
        case 'q':
        case 'w':
        case 'z':
          noteOffDmEmAm();
          break;
        case 'x':
          noteOffBm_5();
          break;
        default:
          number = notenums[key];
          midiSender.sendNoteOff(0, 0, d, changed_vel);
          break;
      }
      if(playable) {
        if(d != 0) {
          noteOffTime = millis();
          println("noteOff:" + noteOffTime / 1000 + " seconds");
          prev_note_len = noteOffTime - noteOnTime;  
          prev_vel = baseVel;
        }
      }
      noteOffEffects();
      isNowOn[key] = false;
      Pressing[key] = false;
    }
  }
  
  public void decideBlackKeys(int left) {
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
  
  public void setBlackKeys() {
    for(int i = 0; i < names.length(); i++) {
      notenums[names.charAt(i)] = blackKeys[i];
    }
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
  
  public void startCmx() {
    midiSender = new MidiEventSender();
    MidiOutputModule midiout = cmx.createMidiOut();
    cmx.addSPModule(midiSender);
    cmx.addSPModule(midiout);
    cmx.connect(midiSender, 0, midiout, 0);
    cmx.startSP();

  }
  
  public void noteOnCFG() {
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
  }
  
  public void noteOnDmEmAm() {
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
  }
  
  public void noteOnBm_5() {
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
  }
  
  public int octaveChange(int number) {
    int d = 0;
    if(octaveUp) {
      d = number + octave;
    }else if(octaveDown){
      if(notenums[key]-octave > 0) {
        d = number - octave;
      }
    }else {
      d = number;
    }
    return d;
  }
  
  public void noteOffCFG() {
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
  }
  
  public void noteOffDmEmAm() {
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
  }
  
  public void noteOffBm_5() {
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
  }
  
  public void execPrediction() {
    if(notenums[key] != 0) {
      ms.setFeatures(d, noteOnTime, noteOffTime, prev_vel, prev_note_len);
      ms.predict();
      predict_time = millis();
      execTime = predict_time - noteOnTime;
      println("execute time:" + execTime);
      output = ms.getOutput();
      execTL.add(execTime);
      sumExec += execTime;
      baseVel = round(output);
    }      
  }
  
  public int velChange() {
    int changed_vel = 0;
    if(farte) {
      changed_vel = baseVel + vel;
    }else if(piano) {
      changed_vel = baseVel - vel;
    }else {
      changed_vel = baseVel;
    }
              
    if(changed_vel < 0) {
      changed_vel = 0;
    }else if(changed_vel > 127){
      changed_vel = 127;
    }
    return changed_vel;
  }
  
  public void noteOnEffects() {
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
  }
  
  public void noteOffEffects() {
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

  public int getMinBlackKey() {
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
  
  String getNoteName(int note) {
    String noteName;
    int remainder = note % 12;
    switch(remainder) {
      case 0:
        noteName = "ド";
        break;
      case 2:
        noteName = "レ";
        break;
      case 4:
        noteName = "ミ";
        break;
      case 5:
        noteName = "ファ";
        break;
      case 7:
        noteName = "ソ";
        break;
      case 9:
        noteName = "ラ";
        break;
      case 11:
        noteName = "シ";
        break;
      default:
        noteName = "";
        break;
    }
    return noteName;
  }
  
  public void printRange() {
    String minNote = getNoteName(notenums['r']);
    String maxNote = getNoteName(notenums['[']);
    fill(0);
    text("音域：" + minNote + "～" + maxNote, width/2, 50);
  }
  
  public float getStd() {
    float std = 0.0f;
    for(int i = 0; i < execTL.size(); i++) {
      std += pow(execTL.get(i) - meanExec, 2.0f);
    }
    std = std / execTL.size();
    std = sqrt(std);
    return std;
  }
  
  public void dispose() {
    meanExec = sumExec / execTL.size();
    float std = getStd();
    println("sum of execute time: " + sumExec);
    println("mean execute time: " + meanExec);
    println("standard deviation: " + std);
  }
  
  public static void main(String[] args) {
    PApplet.main("pc_keyboard3");
  }
}