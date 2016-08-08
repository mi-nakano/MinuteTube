package jp.mzkcreation.minutetube;

/**
 * Created by nakanomizuki on 2016/08/08.
 */
public class MyTime implements Comparable<MyTime>{
    private final int minute, second;

    public MyTime(int m, int s){
        if(s >= 60){
            m += (s / 60);
            s %= 60;
        }
        minute = m;
        second = s;
    }

    public int getMinute(){ return minute; }
    public int getSecond(){ return second; }

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
}
