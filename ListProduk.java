package kue;

import java.util.ArrayList;

public class ListProduk {
    private ArrayList<Produk> produkList;

    public ListProduk() {
        produkList = new ArrayList<>();
    }

    public void addProduk(Produk p) {
        produkList.add(p);
    }

    public void removeProduk(String id) {
        produkList.removeIf(p -> p.getId().equals(id));
    }

    public int getTotal() {
        return produkList.size();
    }

    public void printListProduk() {
        for (Produk p : produkList) {
            System.out.println(p.getId() + " - " + p.getName() + " - Rp" + p.getPrice());
        }
    }

    public ArrayList<Produk> getAll() {
        return produkList;
    }

    public Produk findById(String id) {
        for (Produk p : produkList) {
            if (p.getId().equals(id))
                return p;
        }
        return null;
    }
}
