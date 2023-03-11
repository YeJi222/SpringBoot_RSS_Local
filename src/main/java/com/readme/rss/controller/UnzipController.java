package com.readme.rss.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UnzipController {
    private static void searchFiles(String searchDirPath) throws IOException {
        File dirFile = new File(searchDirPath);
        File[] fileList = dirFile.listFiles();

        for(int i = 0 ; i < fileList.length; i++) {
            if(fileList[i].isFile()) {
                System.out.println(fileList[i].getPath()); // Full Path

                // FileReader reader = new FileReader(fileList[i].getPath());
                Scanner reader = new Scanner(new File(fileList[i].getPath()));

                // 파일 내용들 출력(확인용)
                /*
                while (reader.hasNextLine()) {
                    String str = reader.nextLine();
                    System.out.println(str);
                }
                */

                // url 찾아서 파싱
                String findURL = "";
                while (reader.hasNextLine()) {
                    String str = reader.nextLine();
                    if(str.contains("url=")){
                        System.out.println("\n[찾는 content]");
                        findURL = str.substring(5, str.length()-1);
                        System.out.println(findURL);
                    }
                }

            } else if(fileList[i].isDirectory()) {
                searchFiles(fileList[i].getPath());  // 재귀함수 호출
            }
        }


    }

    public static void main(String[] args) throws IOException {
        /*
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("unzip", "unzipTest.zip", "-d", "./unzipFiles");
        var process = builder.start(); // upzip 실행

        // unzip 실행 후, 콘솔에 출력해주기
        try (var reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
            String commandResult;
            while ((commandResult = reader.readLine()) != null) {
                System.out.println(commandResult);
            }
        }
        */

        // 압축 푼 파일들 중에서 원하는 정보 찾기(ex. url 찾기)
        String searchDirPath = "./unzipFiles";
        System.out.println("[압축 해제한 폴더 속 파일 리스트]");
        searchFiles(searchDirPath);
    }
}
