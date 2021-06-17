package com.edusoho.kuozhi.v3.model.bal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/6/21.
 */

public class ClassroomStudentResult implements Serializable {
    public int        start;
    public int        limit;
    public int        total;
    public List<User> data;
}
