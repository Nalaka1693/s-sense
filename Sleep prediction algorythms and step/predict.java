import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rajit on 5/28/2016.
 */
public class predict {

    private static HashMap<Integer, HashMap> month = new HashMap<>();
    private Calendar calendar= Calendar.getInstance();
    public HashMap<String, Boolean> Prediction = new HashMap<String, Boolean>();

    // initializing happening in constructor
    public predict(){
        for(int i=0; i<5; i++){
            HashMap<String, ArrayList<String>> week = new HashMap<>();
            week.put("Monday", new ArrayList<String>());
            week.put("Tuesday", new ArrayList<String>());
            week.put("Wednesday", new ArrayList<String>());
            week.put("Thursday", new ArrayList<String>());
            week.put("Friday", new ArrayList<String>());
            week.put("Saturday", new ArrayList<String>());
            week.put("Sunday", new ArrayList<String>());
            month.put(i,week);
        }

    }


    public String addSleepTime(){
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm");
        String time = format1.format(cal.getTime());
        format1 = new SimpleDateFormat("EEEE");

        String date = format1.format(cal.getTime());
        HashMap<String, ArrayList<String>> temp = month.get(0);
        ArrayList<String> temparray = temp.get(date);
        temparray.add(time);
        //temp.put(date, temparry);
        //month.put(0, temp);
        return date;
    }

    public void changeWeek(){
        for(int i=4; i>0; i++){
            month.put(i+1, month.get(i));
        }
        HashMap<String, ArrayList<String>> week = new HashMap<>();
        week.put("Monday", new ArrayList<String>());
        week.put("Tuesday", new ArrayList<String>());
        week.put("Wednesday", new ArrayList<String>());
        week.put("Thursday", new ArrayList<String>());
        week.put("Friday", new ArrayList<String>());
        week.put("Saturday", new ArrayList<String>());
        week.put("Sunday", new ArrayList<String>());
        month.put(0, week);
    }

    private double getMeanLastWeek(){
        HashMap lastweek = month.get(1);
        int add = 0;
        for (Object temp: lastweek.values()) {
            ArrayList<String> tempory = (ArrayList<String>) temp;
            add += tempory.size();
        }
        return (double)add/(7*48);
    }

    private double stdLastWeek(){
        double mean = getMeanLastWeek();
        HashMap<String, ArrayList<String>> lastweek = month.get(1);
        HashMap<Integer, HashMap<String, Integer>> weekstd = new HashMap<>();
        int k = 0;
        for (Object temp: lastweek.values()) {
            ArrayList<String> tempory = (ArrayList<String>) temp;
            HashMap<String, Integer> day = new HashMap<>();
            for(int i =0; i<24; i++){
                day.put(i+":00", 0);
                day.put(i+":30", 0);
            }

            for (String i: tempory){
                String[] hrs = i.split(":");
                if(Integer.parseInt(hrs[1])>=30){
                    day.put(hrs[0]+":30", ( day.get(hrs[0]+":30") + 1));
                } else {
                    day.put(hrs[0]+":00", ( day.get(hrs[0]+":00") + 1));
                }
            }
            weekstd.put(k, day);
            k++;
        }
        int add = 0;
        for (Object temp: weekstd.values()) {
            HashMap<String, Integer> tempory = (HashMap<String, Integer>) temp;
            for (Integer i: tempory.values()) {
                add += (i - mean) * (i - mean);
            }
        }
        return Math.sqrt((double) add/(7*48));
    }

    public void dayPridiction(){
        double mean = getMeanLastWeek();
        double std = stdLastWeek();
        HashMap<String, ArrayList<String>> thisWeek = month.get(0);
        HashMap<String, ArrayList<String>> LastWeek = month.get(1);
        HashMap<String, ArrayList<String>> LastMonth = month.get(4);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat format1 = new SimpleDateFormat("EEEE");
        String yesterday = format1.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String today = format1.format(cal.getTime());
        ArrayList<String> yesterdayArray = thisWeek.get(yesterday);
        ArrayList<String> lastweekArray = LastWeek.get(today);
        ArrayList<String> lastMonthArray = LastMonth.get(today);

        HashMap<String, Integer> yesterdaySlots = new HashMap<>();
        for(int i =0; i<24; i++){
            yesterdaySlots.put(""+i+":00", 0);
            yesterdaySlots.put(""+i+":30", 0);
        }
        for (String i: yesterdayArray){
            String[] hrs = i.split(":");
            if(Integer.parseInt(hrs[1])>=30){
                yesterdaySlots.put(hrs[0]+":30", ( yesterdaySlots.get(hrs[0]+":30") + 1));
            } else {
                yesterdaySlots.put(hrs[0]+":00", ( yesterdaySlots.get(hrs[0]+":00") + 1));
            }
        }

        HashMap<String, Integer> LastWeekSlots = new HashMap<>();
        for(int i =0; i<24; i++){
            LastWeekSlots.put(""+i+":00", 0);
            LastWeekSlots.put(""+i+":30", 0);
        }
        for (String i: lastweekArray){
            String[] hrs = i.split(":");
            if(Integer.parseInt(hrs[1])>=30){
                LastWeekSlots.put(hrs[0]+":30", ( LastWeekSlots.get(hrs[0]+":30") + 1));
            } else {
                LastWeekSlots.put(hrs[0]+":00", ( LastWeekSlots.get(hrs[0]+":00") + 1));
            }
        }

        HashMap<String, Integer> LastMonthSlots = new HashMap<>();
        for(int i =0; i<24; i++){
            LastMonthSlots.put(""+i+":00", 0);
            LastMonthSlots.put(""+i+":30", 0);
        }
        for (String i: lastMonthArray){
            String[] hrs = i.split(":");
            if(Integer.parseInt(hrs[1])>=30){
                LastMonthSlots.put(hrs[0]+":30", ( LastMonthSlots.get(hrs[0]+":30") + 1));
            } else {
                LastMonthSlots.put(hrs[0]+":00", ( LastMonthSlots.get(hrs[0]+":00") + 1));
            }
        }

        for (Map.Entry<String, Integer> entry : yesterdaySlots.entrySet()){
            String key = entry.getKey();
            double [] zValues = new double[3];
            zValues[0] = ((entry.getValue()) - mean)/ std;
            zValues[1] = ((LastWeekSlots.get(key))-mean)/ std;
            zValues[2] = ((LastMonthSlots.get(key)) - mean) /std;

            double avgZ = (zValues[0]+zValues[1]+zValues[2])/3;

            if(avgZ>0){
                Prediction.put(key, true);
            } else {
                Prediction.put(key, false);
            }
        }
    }

    public void writeToFile(){

        FileOutputStream fOut = null;
        for (int i:this.month.keySet()
             ) {
            HashMap<String, ArrayList<String>> tempArrayWeek = month.get(i);
            for (String day: tempArrayWeek.keySet()
                 ) {
                try {
                    fOut = new FileOutputStream(new File(i+"week"+day+".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fOut);
                    objectOutputStream.writeObject(tempArrayWeek.get(day));
                    objectOutputStream.close();
                }catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void readFromFile() {

        for (int i:this.month.keySet()
                ) {
            HashMap<String, ArrayList<String>> tempArrayWeek = month.get(i);
            for (String day: tempArrayWeek.keySet()
                    ) {
                try {
                    FileInputStream fileInputStream  = new FileInputStream(i+"week"+day+".txt");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                    tempArrayWeek.replace(day, (ArrayList) objectInputStream.readObject());
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public static void main(String[] args){

        predict pr = new predict();
        for(int i=0; i<100; i++){
            pr.addSleepTime();
        }

        pr.writeToFile();
        HashMap<String, ArrayList<String>> week = pr.month.get(0);
        System.out.println(week.get("Saturday").toString());
        week.clear();
        pr.month.clear();

        pr = new predict();
        week = pr.month.get(0);
        try {
            System.out.println(week.get("Saturday").toString());
        }catch (Exception e){
            System.out.println("empty");
        }

        pr.readFromFile();
        week = pr.month.get(0);
        System.out.println(week.get("Saturday").toString());



    }

}
