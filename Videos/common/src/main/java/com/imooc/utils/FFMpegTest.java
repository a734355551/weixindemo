package com.imooc.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FFMpegTest
 * @Author fengzhenting
 * @Description //TODO
 * @Date 2019年06月13日 16:19
 **/
public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String videoOutPutPath) throws Exception {
        //
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutPutPath);
        for (String c : command) {
            System.out.print(c + " ");
        }
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream inputStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = "";
        while ((line=br.readLine())!=null){

        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }if(inputStream != null){
            inputStream.close();
        }
    }

    public static void main(String[] args) {
        FFMpegTest ffMpegTest = new FFMpegTest("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("F:\\ffmpeg\\bin\\song.mp4", "F:\\ffmpeg\\bin\\song5.avi");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
