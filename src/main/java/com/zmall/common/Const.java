package com.zmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USER_NAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    public interface orderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED = 1;//购物车中选中
        int UN_CHECKED=0;//未选中

        String  LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String  LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

    }

    public enum productStatusEbnum{
        ON_SALE("Online",1);

        private String value;
        private int code;

        productStatusEbnum (String value,int code){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
