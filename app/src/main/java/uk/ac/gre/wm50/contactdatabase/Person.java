package uk.ac.gre.wm50.contactdatabase;

public class Person {
    private String name;
    private String phone;
    private String birthDate;
    private String email;
    //drawable name of the avatar, e.g. "avatar_3"
    private String avatar;
    private int id;

    public Person(String name, String phone, String birthDate, String email, String avatar) {
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.email = email;
        this.avatar = avatar;
    }

    public Person(int id, String name, String phone, String birthDate, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.email = email;
        this.avatar = avatar;
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
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
