package org.example.controller;

import org.example.Container;

import org.example.dto.Article_board;
import org.example.service.ArticleService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainArticleController {
    static List<Article_board> board = new ArrayList<>();


    public void run() {
        Date regDate = new Date();
        Date updateDate = new Date();
        int hit = 0;
        while (true) {
            System.out.println("게시판 보기 / 상세보기 / 글쓰기 / 돌아가기");
            System.out.printf("명령어) ");
            String command = Container.getsc().nextLine().trim();
            if (command.equals("글쓰기")) {
                System.out.printf("제목 : ");
                String title = Container.getsc().nextLine().trim();
                System.out.printf("내용 : ");
                String body = Container.getsc().nextLine().trim();
                System.out.printf("유저이름 : ");
                int memberId = Container.getsc().nextInt();
                board.add(new Article_board(regDate, updateDate, title, memberId, body, hit));
                insertDb(regDate, updateDate, title, memberId, body, hit);
            } else if (command.equals("돌아가기")) {
                Container.moveController.move();

            } else if (command.equals("상세보기")) {
                ArticleController.Findboard();
                break;
            }
            else if(command.equals("게시판 보기")){
                ArticleController.boardList();
                break;
            }

        }

    }

    public void insertDb(Date regDate, Date updateDate, String title, int memberId, String body, int hit) {
        Connection conn = null;
        //PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://127.0.0.1:3306/LSS?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

            conn = DriverManager.getConnection(url, "root", "");
            //String time = String.format("%tY년 %<tm월 %<td일 %<tH시 %<tM분 %<tS초", regDate);

            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO article ( regDate, updateDate, title, memberId, body, hit ) " +
                            " values (?, ?, ?, ?, ?, ? )");

            java.sql.Date now = new java.sql.Date( regDate.getTime() );
            pstmt.setDate( 1, now );
            java.sql.Date update = new java.sql.Date( updateDate.getTime() );
            pstmt.setDate( 2, update );
            pstmt.setString( 3, title );
            pstmt.setInt( 4, memberId);
            pstmt.setString( 5, body);
            pstmt.setInt( 6, hit);

            //String sql = "INSERT INTO article ";
            //sql += "(regDate, updateDate, `title`, memberId, 'body', hit)";
            //sql += String.format("VALUES (%t, %t,'%s',%d,'%s', %d)", regDate, regDate, title, memberId, body, hit);
            //pstmt = conn.prepareStatement(sql);

            int affectedRows = pstmt.executeUpdate();

            System.out.println("affectedRows : " + affectedRows);
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패");
        } catch (SQLException e) {
            System.out.println("에러: " + e);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

