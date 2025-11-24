package kue;

public abstract class Akun {

    // atribut
    private String username;
    private String password;
    private String fullName;

    // constructor
    public Akun(String username, String pw, String fullName) {
        this.username = username;
        this.password = pw;
        this.fullName = fullName;
    }

    // GETTER
    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    // METHOD LOGIN / CHECK PW
    public boolean checkPw(String input) {
        return this.password.equals(input);
    }

    // SET PW
    protected void setPassword(String pwd) {
        this.password = pwd;
    }

    // ABSTRACT METHOD
    public abstract String getRole();

    public abstract void showMenu();
}
