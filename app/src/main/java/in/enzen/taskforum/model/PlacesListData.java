package in.enzen.taskforum.model;

/**
 * Created by Rupesh on 2/10/2018.
 */
@SuppressWarnings("ALL")
public class PlacesListData {

    private String Name;
    private String Vicinity;
    private String Phone;
    private double Lat;
    private double Lng;

    public PlacesListData(String Name, String Vicinity, String Phone, double Lat, double Lng) {
        this.Name = Name;
        this.Vicinity = Vicinity;
        this.Phone = Phone;
        this.Lat = Lat;
        this.Lng = Lng;
    }

    public String getName() {
        return Name;
    }

    public String getVicinity() {
        return Vicinity;
    }

    public String getPhone() {
        return Phone;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }
}
