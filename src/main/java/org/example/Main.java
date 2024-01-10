package org.example;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String cachesPath = args[0];
        String targetPath = args[1];
        String scriptPath = args[2];
        File scriptFile = new File(scriptPath);
        File[] allCacheVideos = new File(cachesPath).listFiles(f -> !f.getName().contains(".") && !f.getName().equals("load_log"));
        FileUtils.writeStringToFile(scriptFile, "#! /bin/bash\n", Charset.defaultCharset(), true);
        for (int i = 0; i < allCacheVideos.length; i++) {
            File cacheVideoDir = allCacheVideos[i];
            File[] m4sFiles = cacheVideoDir.listFiles(f -> f.getName().contains(".m4s"));
            File[] videoInfo = cacheVideoDir.listFiles(f -> f.getName().contains(".videoInfo"));
            if (videoInfo == null || videoInfo.length == 0 || m4sFiles == null || m4sFiles.length == 0) {
                continue;
            }
            FileUtils.writeStringToFile(scriptFile, convertCache(videoInfo[0], m4sFiles, targetPath), Charset.defaultCharset(), true);
            FileUtils.writeStringToFile(scriptFile, progressBar(allCacheVideos.length - 1, i), Charset.defaultCharset(), true);
        }
        FileUtils.writeStringToFile(scriptFile, "echo -ne '\\n'", Charset.defaultCharset(), true);
    }

    private static String progressBar(int total, int index) {
        StringBuilder sb = new StringBuilder("echo -ne '");
        BigDecimal totalProgressLength = BigDecimal.valueOf(30);
        BigDecimal currentProgressIndex = BigDecimal.valueOf(index).divide(BigDecimal.valueOf(total), 2, RoundingMode.UP)
                .multiply(totalProgressLength);
        for (int i = 0; i < totalProgressLength.intValue(); i++) {
            if (i <= currentProgressIndex.intValue()) {
                sb.append("#");
            } else {
                sb.append(" ");
            }
        }
        sb.append(100 * index / total).append("%");
        sb.append("\\r'").append("\n");
        return sb.toString();
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
        String finalFileName = cleanStringForPath(dirName + videoInfo.getTitle() + "_" + videoInfo.getItemId()) + ".mp4";
        sb.append(ffmpegPath + " -loglevel error -y -i " + m4s1TmpFile1 + " -i " + m4s1TmpFile2 + " -codec copy " + finalFileName).append("\n");
        sb.append("rm " + m4s1TmpFile1).append("\n");
        sb.append("rm " + m4s1TmpFile2).append("\n");
        return sb.toString();
    }

    private static String cleanStringForPath(String originPath) {
        return originPath.replaceAll("__+", "_");
    }

}