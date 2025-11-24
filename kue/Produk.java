package kue;

public class Produk {
    private final String id;
    private String name;
    private String deskripsi;
    private double price;
    private int stock;

    public Produk(String id, String name, String deskripsi, double price, int stock) {
        this.id = id;
        this.name = name;
        this.deskripsi = deskripsi;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public double getPrice() {
        return price;
    }

    public int getStok() {
        return stock;
    }

    public void setStok(int stok) {
        this.stock = stok;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // operasi stok
    public void kurangiStok(int qty) throws Exception {
        if (qty > stock)
            throw new Exception("Stok tidak cukup!");
        stock -= qty;
    }

    public void tambahStok(int qty) {
        stock += qty;
    }
}
