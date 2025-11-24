package kue;

import java.util.ArrayList;

public class Keranjang {

    private ArrayList<CartItem> items;

    public Keranjang() {
        items = new ArrayList<>();
    }

    public void addProduk(Produk p, int qty) throws Exception {
        // jika produk tidak cukup
        if (qty > p.getStok())
            throw new Exception("Stok produk tidak cukup!");

        // cek apakah item sudah ada
        for (CartItem ci : items) {
            if (ci.getProduk().getId().equals(p.getId())) {
                ci.setJumlah(ci.getJumlah() + qty);
                return;
            }
        }

        items.add(new CartItem(p, qty));
    }

    public void removeProduk(String id) {
        items.removeIf(ci -> ci.getProduk().getId().equals(id));
    }

    public double totalHarga() {
        double total = 0;
        for (CartItem ci : items)
            total += ci.getSubtotal();
        return total;
    }

    public void clear() {
        items.clear();
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }
}
