package org.example.dto;

import java.util.Date;

public class Article_board {
    private Date regDate;
    private Date updateDate;
    private String title;
    private int memberId;
    private String body;
    private int hit;

    public Date getRegDate() {
        return regDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getTitle() {
        return title;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getBody() {
        return body;
    }

    public int getHit() {
        return hit;
    }



    public Article_board(Date regDate, Date updateDate, String title, int memberId, String body, int hit) {
        this.regDate = regDate;
        this.updateDate = updateDate;
        this.title = title;
        this.memberId = memberId;
        this.body = body;
        this.hit = hit;
    }
}
// regDate, updateDate, `title`, memberId, 'body', hit