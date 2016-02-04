package com.experiment.reactiveclick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Infernus on 05/11/15.
 */
public class ExcelSumActivity extends Activity {

    private View enterButton;
    private EditText commandEditor;
    private TextView countLabel;

    private Observable<Integer> sumStream;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excel_sum_activity);

        initUI();

        compositeSubscription = new CompositeSubscription();
        sumStream = createSumStream();
        compositeSubscription.add(sumStream.subscribe(v -> countLabel.setText(String.valueOf(v))));
    }

    private Observable<Integer> createSumStream() {
        Observable<CharSequence> commandInputStream = ViewObservable.clicks(enterButton, false)
                                                          .map(v -> {
                                                              CharSequence x = commandEditor.getText();
                                                              commandEditor.setText("");
                                                              return x;
                                                          });

        Observable<Integer> aStream = createCommandStream(commandInputStream, "a");
        Observable<Integer> bStream = createCommandStream(commandInputStream, "b");

        return Observable.combineLatest(aStream, bStream, (x, y) -> x + y);
    }

    private Observable<Integer> createCommandStream(Observable<CharSequence> sourceStream, String s) {
        String regex = "^" + s + ":\\d{1,3}$";
        Pattern pattern = Pattern.compile(regex);

        return sourceStream.map(pattern::matcher)
                           .filter(m -> m.matches())
                           .map(m -> m.group())
                           .map(ms -> ms.substring(2))
                           .map(ms -> Integer.parseInt(ms));
    }

    private void initUI() {
        enterButton = findViewById(R.id.excel_sum_enter_button);
        commandEditor = (EditText) findViewById(R.id.excel_sum_editor);
        countLabel = (TextView) findViewById(R.id.excel_sum_count_label);
    }
}
