package com.ks.dblab.kshelper.setup;

/**
 * Created by Administrator on 2016-07-20.
 */
public class NoticeItem {
    public String no;
    public String title;
    public String content;
    public String create_at;

    public NoticeItem(String no, String title, String content, String create_at){
        this.no = no;
        this.title = title;
        this.content = content;
        this.create_at = create_at;
    }
}
