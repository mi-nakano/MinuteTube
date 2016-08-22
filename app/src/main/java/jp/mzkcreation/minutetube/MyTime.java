package jp.mzkcreation.minutetube;

/**
 * Created by nakanomizuki on 2016/08/08.
 */
public class MyTime implements Comparable<MyTime>{
    private final int hour, minute, second;

    public MyTime(int h,int m, int s){
        hour = h;
        minute = m;
        second = s;
    }

    public int getMinute(){ return minute; }
    public int getSecond(){ return second; }

    public static MyTime make(String time){
        int hour = 0, minute = 0, second = 0;
        String tmp = time.substring(2); // 最初の2文字は必ずPT

        int index = tmp.indexOf('H');
        int length = tmp.length();
        if(index != -1){
            hour = Integer.valueOf(tmp.substring(0, index));
            tmp = tmp.substring(index+1, length);
            length = tmp.length();
        }
        index = tmp.indexOf('M');
        length = tmp.length();
        if(index != -1){
            minute = Integer.valueOf(tmp.substring(0, index));
            tmp = tmp.substring(index+1, length);
            length = tmp.length();
        }
        if(length > 0 && tmp.charAt(length - 1) == 'S'){
            second = Integer.valueOf(tmp.substring(0, length - 1));
        }
        return new MyTime(hour, minute, second);
    }

    @Override
    public int compareTo(MyTime m){
        if(minute > m.minute) {
            return 1;
        }
        if(minute == m.minute){
            if(second == m.second) {
                return 0;
            }
            if(second > m.second) {
                return 1;
            }
        }
        return -1;
    }

    @Override
    public String toString(){
        return String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
    }
}
