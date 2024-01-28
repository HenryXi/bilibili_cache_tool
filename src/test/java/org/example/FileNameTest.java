package org.example;

public class FileNameTest {
    public static void main(String[] args) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setTitle("哈佛大学《cs50:商业专业计算机科学2017|Computer Science for Business Professionals》中英字幕");
        System.out.println(videoInfo.getTitle());
    }
}
