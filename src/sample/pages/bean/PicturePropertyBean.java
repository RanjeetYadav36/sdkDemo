package sample.pages.bean;

/**
 * @description:
 * @date: 2018/6/19 0019 18:11:47
 * @author: jiahang
 */
public class PicturePropertyBean extends BaseProgramPropertyBean {
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;
    private String picturePath;

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
