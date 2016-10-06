package com.ks.dblab.kshelper.etc;

/**
 * Created by jojo on 2016-07-07.
 */
public class CommentItem {
    public String no;
    public String article_no;
    public String content;
    public String user;
    public String user_no;
    public String user_image;
    public String reg_date;

    public CommentItem(String no, String article_no, String content, String user, String user_no, String user_image, String reg_date){
        this.no = no;
        this.article_no = article_no;
        this.content = content;
        this.user = user;
        this.user_no = user_no;
        this.user_image = user_image;
        this.reg_date = reg_date;
    }
}
