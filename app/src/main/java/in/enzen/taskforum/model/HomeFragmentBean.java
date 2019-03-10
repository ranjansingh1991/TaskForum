package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 04-12-2017.
 */

public class HomeFragmentBean {

    private int image;
    private String name;

    public HomeFragmentBean(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
