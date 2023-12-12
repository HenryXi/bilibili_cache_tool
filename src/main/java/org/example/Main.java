package org.example;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String cachesPath = args[0];
        String targetPath = args[1];
        String scriptPath = args[2];
        File[] allCacheVideos = new File(cachesPath).listFiles(f -> !f.getName().contains(".") && !f.getName().equals("load_log"));
        for (File cacheVideoDir : allCacheVideos) {
            File[] m4sFiles = cacheVideoDir.listFiles(f -> f.getName().contains(".m4s"));
            File[] videoInfo = cacheVideoDir.listFiles(f -> f.getName().contains(".videoInfo"));
            if (videoInfo == null || videoInfo.length == 0) {
                continue;
            }
            FileUtils.writeStringToFile(new File(scriptPath), convertCache(videoInfo[0], m4sFiles, targetPath), Charset.defaultCharset(), true);
        }
    }

    private static String convertCache(File videoInfoFile, File[] m4sFiles, String targetPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<String> strings = Files.readAllLines(videoInfoFile.toPath());
        VideoInfo videoInfo = JsonUtil.fromJson(strings.get(0), VideoInfo.class);
        String dirName = targetPath + "/" + cleanStringForPath(videoInfo.getGroupTitle()) + "/";
        sb.append("mkdir " + dirName + " >/dev/null 2>&1").append("\n");
        String ffmpegPath = Resources.getResource("ffmpeg").getPath();
        String m4s1TmpFile1 = targetPath + "/" + videoInfo.getItemId() + "_1.m4s";
        String m4s1TmpFile2 = targetPath + "/" + videoInfo.getItemId() + "_2.m4s";
        sb.append("tail -c +10 " + m4sFiles[0] + " > " + m4s1TmpFile1).append("\n");
        sb.append("tail -c +10 " + m4sFiles[1] + " > " + m4s1TmpFile2).append("\n");
        String finalFileName = cleanStringForPath(dirName + videoInfo.getTitle()) + ".mp4";
        sb.append(ffmpegPath + " -loglevel error -i " + m4s1TmpFile1 + " -i " + m4s1TmpFile2 + " -codec copy " + finalFileName).append("\n");
        sb.append("rm " + m4s1TmpFile1).append("\n");
        sb.append("rm " + m4s1TmpFile2).append("\n");
        return sb.toString();
    }

    private static String cleanStringForPath(String originPath) {
        String cleanPath = originPath
                .replaceAll("__+", "_");
        if (cleanPath.contains("__")) {
            System.out.println(originPath);
        }
        return cleanPath;
    }

}