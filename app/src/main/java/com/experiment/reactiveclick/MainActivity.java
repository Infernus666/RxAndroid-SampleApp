package com.experiment.reactiveclick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Infernus on 04/11/15.
 */
public class MainActivity extends Activity {

    // VIEW LAYER
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        refreshDataWithFilter();



//        getIntsObservable().subscribe(
//                n -> Log.d("", "Reactive onNext for " + n),
//                e -> Log.d("", "Reactive onError"));

        findViewById(R.id.main_double_click).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DoubleClickActivity.class)));
        findViewById(R.id.main_excel_sum).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExcelSumActivity.class)));
        findViewById(R.id.main_excel_better_sum).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExcelBetterSumActivity.class)));
    }


    public interface MyCallback<T> {
        public void success(T t);
        public void failure();
    }


    private void showData(int n) {
        Log.d("", "Reactive onNext for " + n);
    }

    private void showError() {
        Log.d("", "Reactive onError");
    }
////////////////////////







    // PRESENTER LAYER
    private void refreshDataWithFilter() {
        getIntsObservable()
                .filter(i -> i % 2 != 0)
                .map(i -> {
                    Log.d("", "Reactive called for " + i);
                    return 2 * i;
                })
                .subscribe(
                        n -> showData(n),
                        e -> showError(),
                        () -> {
                        });

    }






    // DATA MANAGER
    private Observable<Integer> getIntsObservable() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                String[] strs = {"1", "2", "3", "4", "5"};

                for (int i = 0; i < strs.length; i++) {
                    if (subscriber.isUnsubscribed()) {
                        Log.d("", "Reactive subscriber unsubscribed");
                        return;
                    }

                    try {
                        int num = Integer.parseInt(strs[i]);
                        subscriber.onNext(num);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }



    private Observable<Integer> getIntsObservable2() {
        String[] strs = {"1", "2", "3", "v", "5"};
        ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strs));


        return Observable.from(strList)

                         .concatMap(str -> Observable.create(new Observable.OnSubscribe<Integer>() {
                             @Override
                             public void call(Subscriber<? super Integer> subscriber) {
                                 if (subscriber.isUnsubscribed()) {
                                     Log.d("", "Reactive subscriber unsubscribed");
                                     return;
                                 }

                                 try {
                                     int num = Integer.parseInt(str);
                                     subscriber.onNext(num);
                                 } catch (NumberFormatException e) {
                                     e.printStackTrace();
                                     subscriber.onError(e);
                                 }
                             }
                         }));
    }
}
