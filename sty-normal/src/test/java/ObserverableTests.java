


import lombok.Getter;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/2/15.
 */

public class ObserverableTests {


    @Test
    public void test1() {
        ExampleObservable example = new ExampleObservable();
        example.addObserver(new ExampleObserver());//给example这个被观察者添加观察者，允许添加多個观察者
        example.setData(2);
        example.setData(-5);
        example.setData(9999);
    }


    @Getter
    public static class ExampleObservable extends Observable {
        int data;
        public void setData(int data) {
            this.data = data;
            this.setChanged();
            this.notifyObservers();
        }
    }

    public static class ExampleObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof ExampleObservable) {
                ExampleObservable exampleObservable = (ExampleObservable) o;
                System.out.println("example.data changed, the new value of data is " + exampleObservable.getData());
            }
        }
    }
}


