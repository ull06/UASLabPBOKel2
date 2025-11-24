package kue;

public class Admin extends Akun {
    public Admin(String username, String pw, String fullName) {
        super(username, pw, fullName);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public void showMenu() {
        // Nanti dipanggil AdminDriver
        System.out.println("Admin Menu belum diimplementasi.");
    }
}
