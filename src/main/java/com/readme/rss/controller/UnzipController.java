package com.readme.rss.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Scanner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/sendZipFileContent")
public class UnzipController {
    static String findURL = "";
    static String findURL_format = "";

    private static void searchFiles(String searchDirPath) throws IOException {
        File dirFile = new File(searchDirPath);
        File[] fileList = dirFile.listFiles();

        if(fileList.length == 0){ // 압축풀기가 되지 않은 상태
            System.out.println("!!! 압축풀기 실패 !!!");
        } else{
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
                    while (reader.hasNextLine()) {
                        String str = reader.nextLine();
                        if (str.contains("url=")) {
                            System.out.println("\n[찾는 content]");
                            findURL = str.substring(5, str.length() - 1);
                            System.out.println(findURL);

                            // url로 넘겨질 포맷으로 변경(특수문자 '/' : %2F)
                            findURL_format = findURL.replaceAll("/", "%2F");
                            System.out.println("\n[url 특수문자 형식 변환]");
                            System.out.println(findURL_format);
                            System.out.println();

                            // uri 매개변수로 content 전송(using HTTP request)
                            URI uri = UriComponentsBuilder
                                .fromUriString("http://localhost:9070")
                                .path("/sendZipFileContent/url")
                                .queryParam("content", findURL_format) // key, value
                                .encode()
                                .build()
                                .toUri();

                            RestTemplate restTemplate = new RestTemplate();
                            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

                            String content = responseEntity.getBody().replaceAll("%2F", "/");
                            System.out.println("\n[HTTP 전송]");
                            System.out.println("status code : " + responseEntity.getStatusCode());
                            System.out.println("content : " + content);
                        }
                    }
                } else if(fileList[i].isDirectory()) {
                    searchFiles(fileList[i].getPath());  // 재귀함수 호출
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // 파일 압축 풀기
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

        // 압축 푼 파일들 중에서 원하는 정보 찾기(ex. url 찾기)
        String searchDirPath = "./unzipFiles";
        System.out.println("[압축 해제한 폴더 속 파일 리스트]");
        searchFiles(searchDirPath);
    }

    final String getMappingValue = "/url";

    // http://localhost:9070/sendZipFileContent/url?content=https:%2F%2Fgithub.com%2FYeJi222%2FSpringBoot_Sample_Structure.git
    // url로 넘겨진 content 매개변수 받아오기
    @GetMapping(value = getMappingValue)
    public String getSend(@RequestParam String content){
        return content;
    };
}
