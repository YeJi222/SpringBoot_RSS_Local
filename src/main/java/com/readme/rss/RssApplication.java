package com.readme.rss;

import static com.readme.rss.controller.UnzipController.deleteUnzipFiles;
import static com.readme.rss.controller.UnzipController.searchFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RssApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(RssApplication.class, args);

		// 쉘 명령어 실행을 위한 ProcessBuilder 사용
		ProcessBuilder builder = new ProcessBuilder();

		// unzipFiles 폴더 생성 - 압축풀기한 파일들을 저장하는 임시 폴더
		builder.command("mkdir", "unzipFiles");
		builder.start();

		File dirFile = new File("./unzipFiles");
		File[] fileList = dirFile.listFiles();

		if(fileList.length != 0){ // 기존에 압축풀기한 파일들이 존재하면 기존 파일들 삭제하고 시작
			System.out.println("기존 파일들 존재! 삭제하고 시작!");
			deleteUnzipFiles(builder);
		}

		// 파일 압축 풀기
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
		System.out.println("\n[압축 해제한 폴더 속 파일 리스트]");
		searchFiles(searchDirPath);

		// content data 보냈으므로, 압축풀기한 파일들 모두 삭제
		deleteUnzipFiles(builder);
	}
}
