package com.experiment.reactiveclick;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Infernus on 05/11/15.
 */
public class ExcelBetterSumActivity extends Activity {

    private EditText commandEditorA;
    private EditText commandEditorB;
    private TextView countLabel;

    private Observable<Integer> sumStream;
    private CompositeSubscription compositeSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excel_better_sum_activity);

        initUI();

        compositeSubscription = new CompositeSubscription();
        sumStream = createSumStream();
        compositeSubscription.add(sumStream.subscribe(v -> countLabel.setText(String.valueOf(v))));
    }

    private Observable<Integer> createSumStream() {
        Observable<Integer> aStream = createCommandStream(ViewObservable.input(commandEditorA, false));
        Observable<Integer> bStream = createCommandStream(ViewObservable.input(commandEditorB, false));

        return Observable.combineLatest(aStream, bStream, (x, y) -> x + y);
    }

    private Observable<Integer> createCommandStream(Observable<String> sourceStream) {
        return sourceStream.map(s -> {
                                       if(s.equals("")) return 0;
                                       else return Integer.parseInt(s);
                                   });
    }

    private void initUI() {
        commandEditorA = (EditText) findViewById(R.id.excel_sum_editor_a);
        commandEditorB = (EditText) findViewById(R.id.excel_sum_editor_b);
        countLabel = (TextView) findViewById(R.id.excel_sum_count_label);
    }
}
