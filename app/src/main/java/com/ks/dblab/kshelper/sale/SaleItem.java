package com.ks.dblab.kshelper.sale;

/**
 * Created by Administrator on 2016-07-06.
 */
public class SaleItem {
    public String no;
    public String title;
    public String content;
    public String img;
    public String user;
    public String user_no;
    public String pwd;
    public String reg_date;
    public String comment_cnt;

    public SaleItem(String no, String title, String content, String img, String user, String user_no, String pwd, String reg_date, String comment_cnt) {
        this.no = no;
        this.title = title;
        this.content = content;
        this.img = img;
        this.user = user;
        this.user_no = user_no;
        this.pwd = pwd;
        this.reg_date = reg_date;
        this.comment_cnt = comment_cnt;
    }
}
