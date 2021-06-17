package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/29.
 */

public class ActivityMembers implements Serializable {

    private PagingBean paging;
    private List<DataBean> data;

    public PagingBean getPaging() {
        return paging;
    }

    public void setPaging(PagingBean paging) {
        this.paging = paging;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class PagingBean implements Serializable {
        /**
         * total : 2
         * offset : 0
         * limit : 10
         */

        private int total;
        private int offset;
        private int limit;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public static class DataBean implements Serializable {
        /**
         * offlineActivityId : 1
         * user : {"id":"9","nickname":"iron4","title":"","postName":"","avatar":{"small":"http://dev.training.com/assets/img/default/avatar.png","middle":"http://dev.training.com/assets/img/default/avatar.png","large":"http://dev.training.com/assets/img/default/avatar.png"}}
         */

        private String offlineActivityId;
        private UserBean user;

        public String getOfflineActivityId() {
            return offlineActivityId;
        }

        public void setOfflineActivityId(String offlineActivityId) {
            this.offlineActivityId = offlineActivityId;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean implements Serializable {
            /**
             * id : 9
             * nickname : iron4
             * title :
             * postName :
             * avatar : {"small":"http://dev.training.com/assets/img/default/avatar.png","middle":"http://dev.training.com/assets/img/default/avatar.png","large":"http://dev.training.com/assets/img/default/avatar.png"}
             */

            private String id;
            private String nickname;
            private String title;
            private String postName;
            private AvatarBean avatar;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getPostName() {
                return postName;
            }

            public void setPostName(String postName) {
                this.postName = postName;
            }

            public AvatarBean getAvatar() {
                return avatar;
            }

            public void setAvatar(AvatarBean avatar) {
                this.avatar = avatar;
            }

            public static class AvatarBean implements Serializable {
                /**
                 * small : http://dev.training.com/assets/img/default/avatar.png
                 * middle : http://dev.training.com/assets/img/default/avatar.png
                 * large : http://dev.training.com/assets/img/default/avatar.png
                 */

                private String small;
                private String middle;
                private String large;

                public String getSmall() {
                    return small;
                }

                public void setSmall(String small) {
                    this.small = small;
                }

                public String getMiddle() {
                    return middle;
                }

                public void setMiddle(String middle) {
                    this.middle = middle;
                }

                public String getLarge() {
                    return large;
                }

                public void setLarge(String large) {
                    this.large = large;
                }
            }
        }
    }
}
