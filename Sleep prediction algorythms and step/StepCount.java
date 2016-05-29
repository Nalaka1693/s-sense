import java.util.HashMap;
import java.io.*;
/**
 * Created by rajit on 5/28/2016.
 */
public class StepCount{
    HashMap<String, Integer> stepCount = new HashMap<String, Integer>();
    HashMap<String, Integer> calories = new HashMap<String, Integer>();

    static int step = 0;
    static FileOutputStream fOut = null;

    public StepCount(){
        try {
            step = Integer.parseInt(read());
        } catch (NullPointerException e){
            step = 0;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addSteps(){
        try {
            fOut = new FileOutputStream(new File("MyFile.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        step =+ 10;
        try {
            fOut.write(step);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String read() throws ClassNotFoundException, IOException {
        String s = null;
            FileInputStream fileInputStream  = new FileInputStream("MyFile.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            s = (String) objectInputStream.readObject();
            objectInputStream.close();

        return s;
    }

    public static double getcalories() {
        double weight = 100;
        double calories = weight*2.2*0.6;
        return calories;
    }

    public static int getStepCount() {
        return step;
    }
}