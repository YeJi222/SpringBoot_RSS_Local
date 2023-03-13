package com.readme.rss.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin(origins = "http://localhost:3005")
@RestController
@RequestMapping("/readme")
public class UnzipController {
    public static String fileName = "";
    @PostMapping(value = "/test")
    public String getFileData(@RequestPart MultipartFile file) throws IOException {
        fileName = file.getOriginalFilename();
        System.out.println("\n입력받은 zip 파일 명 : " + fileName);
        Path savePath = Paths.get("./unzipTest.zip"); // unzipTest.zip이름으로 저장
        file.transferTo(savePath);

        return fileName;
    }

    // http://localhost:8090/readme/url?content=https:%2F%2Fgithub.com%2FYeJi222%2FSpringBoot_Sample_Structure.git
    // url로 넘겨진 content 매개변수 받아오기
    final String getMappingValue = "/url";
    @GetMapping(value = getMappingValue)
    public String getSend(@RequestParam String content){
        return content;
    };
    static String findURL = "";
    static String findURL_format = "";

    public static void searchFiles(String searchDirPath) throws IOException {
        File dirFile = new File(searchDirPath);
        File[] fileList = dirFile.listFiles();

        if(fileList.length == 0){ // 압축풀기가 되지 않은 상태
            System.out.println("!!! 압축풀기할 파일이 존재하지 않습니다 !!!");
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
                                .fromUriString("http://localhost:8090")
                                .path("/readme/url")
                                .queryParam("content", findURL_format) // key, value
                                .encode()
                                .build()
                                .toUri();

                            RestTemplate restTemplate = new RestTemplate();
                            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

                            String content = responseEntity.getBody().replaceAll("%2F", "/");
                            System.out.println("[HTTP 전송]");
                            System.out.println("uri : " + uri);
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

    public static void deleteUnzipFiles(ProcessBuilder builder) throws IOException {
        // upzip한 파일들, zip파일 모두 삭제
        builder.command("rm", "-rf", "./unzipFiles/");
        builder.start();
        builder.command("rm", "-rf", "./unzipTest.zip");
        builder.start();

        System.out.println("\n업로드된 zip파일, 압축풀기한 파일들 모두 삭제 완료!!");
    }
}
