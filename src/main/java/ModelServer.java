import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.Session;
import org.tensorflow.Session.Run;
import org.tensorflow.Output;
import java.util.List;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.IntNdArray;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TInt32;
import org.tensorflow.types.TFloat32;
import org.tensorflow.SavedModelBundle;
import java.io.File;
import java.nio.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import com.google.protobuf.Internal.LongList;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
*/

public class ModelServer {

  private static TFloat32 output;
  private static float prediction;
  private int noteNum_int;
  private int prev_noteNum;
  private int div_noteNum;
  private int count = 0;
  private float noteOnTime;
  private float noteOffTime;
  private float interval;
  private float prev_vel;
  private float prev_note_len;
  private static final int ax1 = 1;
  private static final int ax2 = 4;
  private static final int ax3 = 8;
  private static final int data_len = 128;
  private static final int milliChange = 1000;
  private FloatNdArray input_matrix = NdArrays.ofFloats(Shape.of(ax1, ax2, ax3));
    
  private static File file = new File("モデルへの絶対パス");
  private static String strPath = file.getPath();
  private static SavedModelBundle model = SavedModelBundle.load(strPath, "serve");
  
  /*
  ModelServer(int n, float noteOn, float noteOff, int vel, float pn_len) {
    noteNum_int = n;
    noteOnTime = noteOn;
    noteOffTime = noteOff;
    prev_vel = vel;
    prev_note_len = pn_len / milliChange;
  }
  */
  
  
  
  public void predict() {
    
    //System.out.println("test load...");
    
    count++;
        
    System.out.println(count);
        
    
    if(count > 1) {
      //ノートナンバーの差
      div_noteNum = Math.abs(noteNum_int - prev_noteNum);
      //前の音からの間隔
      interval = Math.abs((noteOnTime - noteOffTime) / milliChange);
    }else {
      div_noteNum = 0;
      interval = 0;
    }
    
    //ノートナンバーの値
    float noteNum_float = noteNum_int;
    
    //前の音のベロシティ
    float vel_float = prev_vel;
    
    
    
    
    
    System.out.println("noteNum:" + noteNum_float);
    System.out.println("interval:" + interval + " seconds");
    System.out.println("div_noteNumber:" + div_noteNum);
    System.out.println("prev_note_len:" + prev_note_len + " seconds");
    System.out.println("prev_velcoity:" + vel_float);
        
    if(count == 1) { 
      for(int j = 0; j < ax2; j++) {
        for(int k = 0; k < ax3; k++) {
          input_matrix.setFloat(0.0f, 0, j, k);
        }
      }
    }
             
    //入力したデータをずらす
    
    if(count > 1) {
      for(int i = 1; i < ax2; i++) {
        for(int j = 0; j < ax3; j++) {
          input_matrix.setFloat(input_matrix.getFloat(0, i, j), 0, i-1, j);
        }
      }
    }
    
    FloatNdArray features = NdArrays.ofFloats(Shape.of(ax3));
      
    features.setFloat(noteNum_float, 0);
    features.setFloat(interval, 1);
    features.setFloat(0.0f, 2);
    features.setFloat(div_noteNum, 3);
    features.setFloat(0.0f, 4);
    features.setFloat(0.0f, 5);
    features.setFloat(prev_note_len, 6);
    features.setFloat(vel_float, 7);
    
    for(int i = 0; i < ax3; i++) {
      input_matrix.setFloat(features.getFloat(i), 0, ax2-1, i);
    }
    
    float mean = 0.0f;
    
    
    TFloat32 input_tensor = TFloat32.tensorOf(input_matrix);
    /*
    Map<String, Tensor> feed_dict = new HashMap<>();
    feed_dict.put("lstm_input", input_tensor);
    model.function("serving_default").call(feed_dict);
    */    
        
    output = (TFloat32) model.session()
        .runner()
        .feed("serving_default_lstm_input:0", input_tensor)
        .fetch("StatefulPartitionedCall:0")
        .run()
        .get(0);
    
    prediction = output.getFloat(0, 0); 
    prediction = prediction * data_len;
    System.out.println("prediction = " + prediction);
    
    /*
    System.out.println(model.metaGraphDef().getSignatureDefMap().get("serving_default"));
     
    System.out.println(output.getClass().getSimpleName());  
        
    mean /= ax2;
    System.out.println("mean: " + mean);
    */
    
    output.close();
    System.out.println("predicted");
    prev_noteNum = noteNum_int;
    
  }
  
  //ベロシティを取得する  
  public float getOutput() {
    return this.prediction;
  }
  
  public void setFeatures(int n, float noteOn, float noteOff, int vel, float pn_len) {
    this.noteNum_int = n;
    this.noteOnTime = noteOn;
    this.noteOffTime = noteOff;
    this.prev_vel = vel;
    this.prev_note_len = pn_len / milliChange;
  }
  /*
  public void setNoteNumber(int n) {
    this.noteNum_int = n;
  }
  
  public void setNoteOnTime(float noteOn) {
    this.noteOnTime = noteOn;
  }
  
  public void setNoteOffTime(float noteOff) {
    this.noteOffTime = noteOff;
  }
  
  public void setPrev_velocity(int vel) {
    this.prev_vel = vel;
  }
  
  public void setPrev_note_len(float pn_len) {
    this.prev_note_len = pn_len / milliChange;
  }
  */
}
