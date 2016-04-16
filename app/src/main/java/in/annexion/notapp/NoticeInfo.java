package in.annexion.notapp;

/**
 * Created by sarang on 1/1/16.
 */
public class NoticeInfo
{
    public int n_id;
    public String title;
    public String uploadedBy;
    public String uploadDate;
    public String exp;
    public String noticeBoard;
    public String link;
    public String md5;
    public String message="";
    public int isFav;
    public int isRead;

    public NoticeInfo()
    {
        this.n_id=0;
        this.title="";
        this.uploadDate="";
        this.exp="";
        this.link="";
        this.uploadedBy = "";
        this.noticeBoard="";
        this.md5="";
        this.isFav=0;
        this.isRead=0;
    }
}
