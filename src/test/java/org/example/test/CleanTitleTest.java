package org.example.test;

import org.example.VideoInfo;

public class CleanTitleTest {
    public static void main(String[] args) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setGroupTitle("【操作系统导论】威斯康星大学 CS-537: Introduction to Operating Systems");
        System.out.println(videoInfo.getGroupTitle());
    }
}
