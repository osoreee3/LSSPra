package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
  public void run() {
    System.out.println(" == Lss 시스템을 시작합니다 == ");
    Container.scanner = new Scanner(System.in);
    Container.init();

    while (true) {
      System.out.println("회원가입 / 로그인 / 이동 / 게시판 / 종료");
      System.out.printf("명령어) ");
      String cmd = Container.scanner.nextLine();

      // DB 연결
      Connection conn = null;

      try {
        Class.forName("com.mysql.jdbc.Driver");
      } catch (ClassNotFoundException e) {
        System.out.println("예외 : MySQL 드라이버 로딩 실패");
        System.out.println("프로그램을 종료합니다.");
        break;
      }

      String url = "jdbc:mysql://127.0.0.1:3306/LSS?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

      try {
        conn = DriverManager.getConnection(url, "root", "");

        Container.conn = conn;

        // action 메서드 실행
        action(cmd);

      } catch (SQLException e) {
        System.out.println("예외 : MySQL 드라이버 로딩 실패");
        System.out.println("프로그램을 종료합니다.");
        break;
      } finally {
        try {
          if (conn != null && !conn.isClosed()) {
            conn.close();
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      // DB 연결 끝
    }
    Container.scanner.close();
  }

  private void action(String cmd) {
    if (cmd.equals("회원가입")) {
      Container.memberController.join();
    } else if (cmd.equals("로그인")) {
      Container.memberController.login();
    } else if (cmd.equals("로그아웃")) {
      Container.memberController.logout();
    } else if (cmd.equals("회원정보")) {
      Container.memberController.whoami();
    } else if (cmd.equals("이동")) {
      Container.moveController.move();
    }else if (cmd.equals("게시판")) {
      Container.mainArticleController.run();
    }
    else if (cmd.equals("system exit")) {
      System.out.println("시스템 종료");
      System.exit(0);
    } else {
      System.out.println("명령어를 확인해주세요.");
    }
    return;
  }

}
