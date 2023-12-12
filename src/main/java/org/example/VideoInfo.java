package org.example;

public class VideoInfo {
    private String groupId;
    private String itemId;
    private String title;
    private String groupTitle;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return clean(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupTitle() {
        return clean(groupTitle);
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    private String clean(String title){
        return title.replaceAll("\\s+", "_")
                .replaceAll("\\.", "_")
                .replaceAll("-", "_")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\+", "_")
                .replaceAll(",", "_")
                .replaceAll("\\|","_")
                .replaceAll("&","_")
                .replaceAll("/","_");
    }

}
