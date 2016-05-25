package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.svm.src.svm_main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cody_local on 2016/5/25.
 */
public class Judger {
    ////todo 评判依据
    List<Boolean> judgeState;

    public Judger() {
        this.judgeState = new ArrayList<>();
    }

    public Boolean simpleJudge(String line,String appName) throws IOException{
        svm_main m = new svm_main();
        Boolean result = m.predict(line, appName);
        return result;
    }
    public Boolean judge(String line, String appName) throws IOException {
        svm_main m = new svm_main();
        Boolean result = m.predict(line, appName);
        if (judgeState.size() <= 5) {
            judgeState.add(result);
        } else {
            judgeState.remove(0);
            judgeState.add(result);
        }
        return checkState();
    }

    public Boolean checkState() {
        if (judgeState.size() < 5) {
            return true;
        }
        int falseCount = 0;
        for (Boolean state : judgeState) {
            if (!state) {
                falseCount++;
            }
        }
        if (falseCount > 3) {
            return false;
        }
        return true;
    }
}
