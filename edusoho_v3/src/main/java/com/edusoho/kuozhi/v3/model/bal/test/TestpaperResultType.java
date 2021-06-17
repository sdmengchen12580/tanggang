package com.edusoho.kuozhi.v3.model.bal.test;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-9.
 */
public class TestpaperResultType {

    public LinkedTreeMap<QuestionType, ArrayList<QuestionTypeSeq>> items;
    public LinkedTreeMap<QuestionType, Accuracy> accuracy;
    public PaperResult paperResult;
    public Testpaper testpaper;
    public ArrayList<Integer> favorites;
}
