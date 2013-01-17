package com.mama100.android.member.db;

import android.net.Uri;

/****************************************
         * 消息历史信息表
         * aihua.yan 2012-11-20
         ****************************************/


public class MessageHistoryTable {

    public static final String TABLE_NAME = "user_table";

    /**
     * 定义表结构schema
     */

    //Android标准的id格式
    public static final String ID = "_id";
    //消息id
    public static final String MESSAGE_ID = "msg_id";
    //用户妈网id
    public static final String MAMA100_ID = "mid";
    //消息来源 
    public static final String FROM = "msg_from"; 
    
    /****************************************
         * 消息主体格式 --START
         ****************************************/
    
    //消息主题 
    public static final String TITLE = "title";
    //消息简述
    public static final String SHORT_DESC = "short_desc";
    //消息状态
    public static final String READ_STATUS = "read_status";
    //消息创建时间
    public static final String CREATED_TIME = "created_time";
    //消息读取时间
    public static final String READ_TIME = "read_time";
    /****************************************
     * 消息主体格式 --END
     ****************************************/
  

    /**
     * 定义内容和URI(统一资源识别符)
     */
    // THE CONTENT URI TO OUR PROVIDER
    public static final Uri CONTENT_URI = Uri.parse("content://" + Mama100ContentProvider.AUTHORITY + "/msg");

    // MIME("Multipurpose Internet Mail Extensions") TYPE FOR GROUP OF MESSAGES
    //多功能国际邮件扩展协议 MIME类型 , 类似于 text/html
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mama100.msg";

    // MIME TYPE FOR SINGLE MESSAGES
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mama100.msg";

    // 从第几个 位置获取消息的 id， Android默认都是 1.
    public static final int MSGID_PATH_POSITION = 1;

}
