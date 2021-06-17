package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/30.
 */

public class SurveyModel implements Serializable {


    private SurveyBean                   survey;
    private SurveyResultBean             surveyResult;
    private List<QuestionnaireItemsBean> questionnaireItems;

    public SurveyBean getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyBean survey) {
        this.survey = survey;
    }

    public SurveyResultBean getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(SurveyResultBean surveyResult) {
        this.surveyResult = surveyResult;
    }

    public List<QuestionnaireItemsBean> getQuestionnaireItems() {
        return questionnaireItems;
    }

    public void setQuestionnaireItems(List<QuestionnaireItemsBean> questionnaireItems) {
        this.questionnaireItems = questionnaireItems;
    }

    public static class SurveyBean {
        /**
         * title : 大学生体质调查
         * description : 大学生体质情况调查
         * isResultVisible : 1
         * isAnonymous : 0
         * startTime : 1516118400
         * endTime : 1516463999
         * allCount : 2
         * finishedCount : 2
         */

        private String title;
        private String type;
        private String description;
        private String isResultVisible;
        private String isAnonymous;
        private String startTime;
        private String endTime;
        private int    allCount;
        private int    finishedCount;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIsResultVisible() {
            return isResultVisible;
        }

        public void setIsResultVisible(String isResultVisible) {
            this.isResultVisible = isResultVisible;
        }

        public String getIsAnonymous() {
            return isAnonymous;
        }

        public void setIsAnonymous(String isAnonymous) {
            this.isAnonymous = isAnonymous;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getAllCount() {
            return allCount;
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }

        public int getFinishedCount() {
            return finishedCount;
        }

        public void setFinishedCount(int finishedCount) {
            this.finishedCount = finishedCount;
        }
    }

    public static class SurveyResultBean {
        /**
         * id : 2
         */

        private String id;
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class QuestionnaireItemsBean {

        private String             id;
        private String             type;
        private String             stem;
        private String             seq;
        private String             questionnaireId;
        private String             parentId;
        private String             isOptional;
        private String             score;
        private String             createdUserId;
        private String             createdTime;
        private String             updatedTime;
        private List<MetasBean>    metas;
        private List<List<String>> statistics;
        private String             blankContent;

        public String getBlankContent() {
            return blankContent;
        }

        public void setBlankContent(String blankContent) {
            this.blankContent = blankContent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStem() {
            return stem;
        }

        public void setStem(String stem) {
            this.stem = stem;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getQuestionnaireId() {
            return questionnaireId;
        }

        public void setQuestionnaireId(String questionnaireId) {
            this.questionnaireId = questionnaireId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getIsOptional() {
            return isOptional;
        }

        public void setIsOptional(String isOptional) {
            this.isOptional = isOptional;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getCreatedUserId() {
            return createdUserId;
        }

        public void setCreatedUserId(String createdUserId) {
            this.createdUserId = createdUserId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public List<MetasBean> getMetas() {
            return metas;
        }

        public void setMetas(List<MetasBean> metas) {
            this.metas = metas;
        }

        public List<List<String>> getStatistics() {
            return statistics;
        }

        public void setStatistics(List<List<String>> statistics) {
            this.statistics = statistics;
        }

        public static class MetasBean {
            /**
             * name : 男
             * order : 0
             * statistics : {"count":2,"percent":100,"order":0}
             */

            private String         name;
            private int            order;
            private StatisticsBean statistics;
            private boolean        isSelected;
            private int            selectOrder;

            public int getSelectOrder() {
                return selectOrder;
            }

            public void setSelectOrder(int selectOrder) {
                this.selectOrder = selectOrder;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getOrder() {
                return order;
            }

            public void setOrder(int order) {
                this.order = order;
            }

            public StatisticsBean getStatistics() {
                return statistics;
            }

            public void setStatistics(StatisticsBean statistics) {
                this.statistics = statistics;
            }

            public static class StatisticsBean {
                /**
                 * count : 2
                 * percent : 100
                 * order : 0
                 */

                private int   count;
                private float percent;
                private int   order;

                public int getCount() {
                    return count;
                }

                public void setCount(int count) {
                    this.count = count;
                }

                public float getPercent() {
                    return percent;
                }

                public void setPercent(float percent) {
                    this.percent = percent;
                }

                public int getOrder() {
                    return order;
                }

                public void setOrder(int order) {
                    this.order = order;
                }
            }
        }
    }
}
