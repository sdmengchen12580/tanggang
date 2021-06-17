package com.edusoho.kuozhi.v3.model.bal.test;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-23.
 */
public class TestpaperFullResult {

    public PaperResult testpaperResult;
    public Testpaper testpaper;
    public LinkedTreeMap<QuestionType, ArrayList<QuestionTypeSeq>> items;
}
