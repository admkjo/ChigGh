package gh.chig.com.myapplication;

import com.google.gson.annotations.SerializedName;

public class RetroUsers  {

    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("location")
    private String location;
    @SerializedName("code")
    private String code;

    public RetroUsers(String name, String phone, String email, String location, String code) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
