package com.fengjr.sauron.dao.mongodb;

/**
 * Created by bingquan.an@fengjr.com on 2015/9/7.
 */
public class MongoEnum {

    public enum Collection{

        collection_nginx(1,"nginx","nginx源"),
        collection_user(2,"user","用户源"),
        collection_statistics(3,"statistics","统计");

        private int id;
        private String name;
        private String desc;

        Collection(int id, String name, String desc) {
            this.id = id;
            this.name = name;
            this.desc = desc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
