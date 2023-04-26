package org.example.controller;


import org.example.Container;
import org.example.dto.Article;
import org.example.dto.Article_board;
import org.example.service.ArticleService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.Container.articleController;
import static org.example.Container.rq;

public class ArticleController {

    int hit = 0;
    private ArticleService articleService;

    public ArticleController() {
        articleService = Container.articleService;
    }

    public void write() {
        if (Container.session.isLogined() == false) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        System.out.println("== 게시물 등록 ==");
        System.out.printf("제목 : ");
        String title = Container.scanner.nextLine();
        System.out.printf("내용 : ");
        String body = Container.scanner.nextLine();

        int memberId = Container.session.loginedMemberId;
        int id = articleService.write(memberId, title, body, hit);

        System.out.printf("%d번 게시물이 등록되었습니다.\n", id);
    }

    public void showList() {
        System.out.println("== 게시물 리스트 ==");
        int page = rq.getIntParam("page", 1);
        String searchKeyword = rq.getParam("searchKeyword", "");
        int pageItemCount = 10;

        // 임시
        pageItemCount = 5;

        List<Article> articles = articleService.getForPrintArticleById(page, pageItemCount, searchKeyword);

        if (articles.isEmpty()) {
            System.out.println("게시물이 존재하지 않습니다.");
            return;
        }

        System.out.println("번호 / 작성날짜 / 작성자 / 제목");

        for (Article article : articles) {
            System.out.printf("%d / %s / %s / %s\n", article.id, article.regDate, article.extra__writerName, article.title);
        }
    }

    public void showDetail() {
        int id = Container.scanner.nextInt();

        if (id == 0) {
            System.out.println("id를 올바르게 입력해주세요.");
            return;
        }

        articleService.increaseHit(id);
        Article article = articleService.getArticleById(id);

        if (article == null) {
            System.out.printf("%d번 게시글은 존재하지 않습니다.\n", id);
            return;
        }

        System.out.printf("번호 : %d\n", article.id);
        System.out.printf("등록날짜 : %s\n", article.regDate);
        System.out.printf("수정날짜 : %s\n", article.updateDate);
        System.out.printf("작성자 : %s\n", article.extra__writerName);
        System.out.printf("조회수 : %d\n", article.hit);
        System.out.printf("제목 : %s\n", article.title);
        System.out.printf("내용 : %s\n", article.body);
    }

    public void modify() {
        if (Container.session.isLogined() == false) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        int id = rq.getIntParam("id", 0);

        if (id == 0) {
            System.out.println("id를 올바르게 입력해주세요.");
            return;
        }

        Article article = articleService.getArticleById(id);

        boolean articleExists = articleService.articleExists(id);

        if (articleExists == false) {
            System.out.printf("%d번 게시글은 존재하지 않습니다.\n", id);
            return;
        }

        if (article.memberId != Container.session.loginedMemberId) {
            System.out.println("권한이 없습니다");
            return;
        }

        System.out.printf("새 제목 : ");
        String title = Container.scanner.nextLine();
        System.out.printf("새 내용 : ");
        String body = Container.scanner.nextLine();

        articleService.update(id, title, body);

        System.out.printf("%d번 게시물이 수정되었습니다.\n", id);
        System.out.println("-".repeat(30));
    }

    public void delete() {
        if (Container.session.isLogined() == false) {
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        int id = rq.getIntParam("id", 0);

        if (id == 0) {
            System.out.println("id를 올바르게 입력해주세요.");
            return;
        }

        System.out.printf("== %d번 게시글 삭제 ==\n", id);

        Article article = articleService.getArticleById(id);

        boolean articleExists = articleService.articleExists(id);

        if (articleExists == false) {
            System.out.printf("%d번 게시글은 존재하지 않습니다.\n", id);
            return;
        }

        if (article.memberId != Container.session.loginedMemberId) {
            System.out.println("권한이 없습니다");
            return;
        }

        articleService.delete(id);

        System.out.printf("%d번 게시물이 삭제되었습니다.\n", id);
    }

    public void run() {
        while (true) {
            System.out.println("-".repeat(30));
            System.out.println("게시판 보기 / 상세보기 / 글쓰기 / 돌아가기");
            System.out.printf("게시판 명령어: ");
            String command = Container.scanner.nextLine();
            if (command.equals("게시판 보기")) {
                ArticleController.boardList();
            } else if (command.equals("상세보기")) {
                ArticleController.Findboard();
            } else if (command.equals("글쓰기")) {
                articleController.write();
            } else if (command.equals("돌아가기")) {
                break;
            }
        }
    }

    public static void boardList() {
        System.out.println("글번호 / 제목 / 등록날짜");
        System.out.println("-".repeat(30));
// 분류 title 제목 body / regDate
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int count = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://127.0.0.1:3306/LSS?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

            conn = DriverManager.getConnection(url, "root", "");

            String sql = "SELECT * FROM article";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery(sql);
            int id = 1;
            Date now = null;
            Date update = null;
            int memberId = 0;
            String title = null;
            String body = null;
            int hit = 0;

            while (rs.next()) {
                id =  rs.getInt("id");
                now = rs.getDate("regDate");
                update = rs.getDate("updateDate");
                title = rs.getString("title");
                memberId = rs.getInt("memberId");
                body = rs.getString("body");
                hit = rs.getInt("hit");
                count++;
                System.out.println(id + " / " + title + " / " + body + " / " + now);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("총 등록된 ID 갯수" + count);
    }

    //lll
    public static void Findboard() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        System.out.printf("글번호 : ");
        int id = Container.getsc().nextInt();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://127.0.0.1:3306/LSS?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

            conn = DriverManager.getConnection(url, "root", "");

            String sql = "SELECT * FROM article";
            sql += " WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("아이디가 없습니다");

            } else {
                String title = rs.getString("title");
                System.out.println("제목 : " + title);
                System.out.println("=".repeat(30));
                System.out.println("");
                String body = rs.getString("body");
                System.out.println(body);
                System.out.println("");
                System.out.println("");
                System.out.printf("수정");
                System.out.printf(" ".repeat(23));
                System.out.println("삭제");
                System.out.println("=".repeat(30));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

