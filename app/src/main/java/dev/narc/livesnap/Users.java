package dev.narc.livesnap;


public class Users {

    public String FullName, Verify, License;

    public Users(){

    }

    public String getName() {
        return FullName;
    }

    public void setName(String FullName) {
        this.FullName = FullName;
    }

    public String getVerify() {
        return Verify;
    }

    public void setVerify(String Verify) {
        this.Verify = Verify;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String License) {
        this.License = License;
    }
    public Users(String FullName, String Verify, String License) {
        this.FullName = FullName;
        this.Verify = Verify;
        this.License = License;
    }
}
