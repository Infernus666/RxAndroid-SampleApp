package com.experiment.reactiveclick;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Infernus on 05/11/15.
 */
public class DoubleClickActivity extends Activity {
    private static final int TIME_OUT = 250;
    private static final int CLICK_SIZE = 2;

    private Button button;
    private TextView counterText;

    private Observable doubleClickObservable;
    private CompositeSubscription compositeSubscription;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_click_activity);

        initUI();

        Observable<Integer> buttonClickStream = ViewObservable.clicks(button, false).map(v -> 1);

        doubleClickObservable = buttonClickStream.buffer(buttonClickStream.debounce(TIME_OUT, TimeUnit.MILLISECONDS))
                                                 .map(list -> list.size())
                                                 .filter(l -> l >= CLICK_SIZE)
                                                 .observeOn(AndroidSchedulers.mainThread());

        compositeSubscription = new CompositeSubscription();

        compositeSubscription.add(doubleClickObservable.subscribe(e -> increaseCounter()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

    private void initUI() {
        button = (Button) findViewById(R.id.double_click_button);
        counterText = (TextView) findViewById(R.id.double_click_count_text);

        updateCounter();
    }

    private void increaseCounter() {
        counter++;
        updateCounter();
    }

    private void updateCounter() {
        counterText.setText(String.valueOf(counter));
    }
}
