import java.util.ArrayList;
import java.util.HashMap;


// static long nanoTime() : 현재 시간을 ns로 리턴한다 (1/1,000,000,000초)
// static long currentTimeMillis() : 현재 시간을 ms로 리턴(1/1,000초)

class TimeMeasure {
    public static void main(String[] args) {
        TimeMeasure timeMeasure = new TimeMeasure();
        for(int loop = 0; loop < 10; loop++) {
            timeMeasure.checkNanoTime();
            timeMeasure.checkCurrentTimeMillis();
        }
        
    }


    private DummyData dummy;

    public void checkCurrentTimeMillis() {
        long startTime = System.currentTimeMillis();
        dummy = makeObjects();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("milli=" + elapsedTime);

    }


    public void checkNanoTime() {
        long startTime = System.nanoTime();
        dummy = makeObjects();
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime)/1000000.0;
        System.out.println("nano = " + elapsedTime);

    }

    public DummyData makeObjects() {
        HashMap<String, String> map = new HashMap<String, String>(1000000);
        ArrayList<String> list = new ArrayList<String>(1000000);
        return new DummyData(map, list);
    }

}

class DummyData {

    HashMap<String, String> map;
    ArrayList<String> list;

    public DummyData(HashMap<String, String> map, ArrayList<String> list) {
        this.map = map;
        this.list = list;
    }
}