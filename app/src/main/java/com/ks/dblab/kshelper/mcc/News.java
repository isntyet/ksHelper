package com.ks.dblab.kshelper.mcc;

/**
 * Created by jojo on 2016-06-24.
 */
public class News  {

    public String subject;
    public String href;
    public String context;
    public String image_url;
    public String date;

    News(String subject, String href, String context, String image_url, String date){
        this.subject = subject;
        this.href = href;
        this.context = context;
        this.image_url = image_url;
        this.date = date;
    }

}
