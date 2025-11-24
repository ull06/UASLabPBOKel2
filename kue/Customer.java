package kue;

public class Customer extends Akun {

    private Keranjang cart;
    private UserProfile profile;

    public Customer(String username, String pw, String fullName, UserProfile profile) {
        super(username, pw, fullName);
        this.profile = profile;
        this.cart = new Keranjang();
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public void showMenu() {
        //itangai oleh CustomerDriver
        System.out.println("Menu Customer dipanggil...");
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Keranjang getCart() {
        return cart;
    }
}
