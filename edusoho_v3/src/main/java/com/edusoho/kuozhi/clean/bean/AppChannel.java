package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Cover;
import com.edusoho.kuozhi.clean.bean.innerbean.Price;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/5/22.
 */

public class AppChannel implements Serializable {

    public int             id;
    public String          title;
    public String          type;
    public int             categoryId;
    public String          orderType;
    public String          showCount;
    public int             actualCount;
    public List<Discovery> data;

    public static class Discovery implements Serializable {
        public int    id;
        public String type;
        public String title;
        public String subtitle;
        public String summary;
        public Cover  cover;
        public int    studentNum;
        public int    maxStudentNum;
        public float  discount;
        public float  price;
        public float  maxCoursePrice;
        public float  minCoursePrice;
        public Price  minCoursePrice2;
        public Price  maxCoursePrice2;
        public Price  price2;
        public String startDate;
        public String endDate;
        public String categoryName;
        public String startTime;
        public String enrollmentStartDate;
        public String enrollmentEndDate;
        public String endTime;
        public String name;
    }
}
