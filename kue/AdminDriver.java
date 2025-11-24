package kue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
//import java.util.Scanner;

public class AdminDriver extends Driver {
    private Admin admin;
    private ProdukService produkService;
    private TransactionService transactionService;
    //private Scanner input;

    public AdminDriver(Admin admin, ProdukService produkService, TransactionService transactionService) {
        this.admin = admin;
        this.produkService = produkService;
        this.transactionService = transactionService;
    }
   
    @Override
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== MENU ADMIN (" + admin.getFullName() + ") ===");
            System.out.println("1. Lihat produk");
            System.out.println("2. Tambah produk");
            System.out.println("3. Edit produk");
            System.out.println("4. Hapus produk");
            System.out.println("5. Lihat pending transaksi");
            System.out.println("6. Terima transaksi");
            System.out.println("7. Daftar transaksi");
            System.out.println("8. Logout");
            System.out.print("Pilih: ");

            String pilih = input.nextLine().trim();
            switch (pilih) {
                case "1" -> viewProduk();
                case "2" -> addProduk();
                case "3" -> editProduk();
                case "4" -> deleteProduk();
                case "5" -> viewPending();
                case "6" -> acceptTransaksi();
                case "7" -> viewRiwayatTransaksi();
                case "8" -> running = false;
                default -> System.out.println("Pilihan tidak dikenal");
            }
        }
    }


    private void viewProduk() {
        produkService.printAllProduk();
    }

    private void addProduk() {
        System.out.println("-- Tambah Produk --");

        // CEK ID DUL
        String id;
        while(true){
            System.out.print("ID: ");
            id = input.nextLine().trim();

            //Cek apakah ID sudah ada
            if (produkService.findById(id) != null) {
                System.out.println("ID sudah ada! Masukkan ID lain.");
            } else {
                break; // ID valid → lanjut
            }
        }
        //System.out.print("ID: ");
        //String id = input.nextLine().trim();
        // LANJUT INPUT DATA LAIN
        System.out.print("Nama: ");
        String nama = input.nextLine().trim();
        System.out.print("Deskripsi: ");
        String desk = input.nextLine().trim();
        System.out.print("Harga: ");
        double price = Double.parseDouble(input.nextLine().trim());
        System.out.print("Stok: ");
        int stok = Integer.parseInt(input.nextLine().trim());

        Produk p = new Produk(id, nama, desk, price, stok);
        boolean ok = produkService.addProduk(p);

        if(ok)
            System.out.println("Produk berhasil ditambah");
        else
            System.out.println("Gagal menyimpan produk (error yang tidak terduga)");
        System.out.println("Produk berhasil ditambahkan.");
    }

    private void editProduk() {
        System.out.println("-- Edit Produk --");
        System.out.print("Masukkan ID produk: ");
        String id = input.nextLine().trim();
        Produk p = produkService.findById(id);
        if (p == null) {
            System.out.println("Produk tidak ditemukan.");
            return;
        }
        System.out.println("Nama lama: " + p.getName());
        System.out.print("Nama baru (enter untuk tidak mengubah): ");
        String nama = input.nextLine().trim();
        if (!nama.isEmpty())
            p.setName(nama);
        System.out.println("Deskripsi lama: " + p.getDeskripsi());
        System.out.print("Deskripsi baru (enter untuk tidak mengubah): ");
        String desc = input.nextLine().trim();
        if (!desc.isEmpty())
            p.setDeskripsi(desc);
        System.out.println("Harga lama: " + p.getPrice());
        System.out.print("Harga baru (enter untuk tidak mengubah): ");
        String hp = input.nextLine().trim();
        if (!hp.isEmpty())
            p.setPrice(Double.parseDouble(hp));
        System.out.println("Stok lama: " + p.getStok());
        System.out.print("Stok baru (enter untuk tidak mengubah): ");
        String st = input.nextLine().trim();
        if (!st.isEmpty())
            p.setStok(Integer.parseInt(st));

        produkService.editProduk(p);
        System.out.println("Produk berhasil diperbarui.");
    }


    private void deleteProduk() {
        System.out.println("-- Hapus Produk --");
        System.out.print("Masukkan ID produk: ");
        String id = input.nextLine().trim();
        produkService.removeProduk(id);
        System.out.println("Jika ID valid, produk telah dihapus.");
    }

    private void viewPending() {
        System.out.println("-- Pending Transaksi --");
        List<Transaksi> pend = transactionService.getPendingTransaksi();
        if (pend.isEmpty()) {
            System.out.println("Tidak ada transaksi pending.");
            return;
        }
        for (Transaksi t : pend) {
            System.out.println(t.getId() + " | " + t.getCustomer().getUsername() + " | " + t.getTotal() + " | accepted=" + t.isAccepted());
        }
    }

    private void acceptTransaksi() {
        System.out.print("Masukkan ID transaksi yang akan diterima: ");
        String id = input.nextLine().trim();
        transactionService.acceptTransaction(id);
        System.out.println("Jika ID valid, transaksi telah diterima.");
    }

    private void viewRiwayatTransaksi() {
    System.out.println("-- Riwayat Transaksi --");
    List<Transaksi> riwayat = transactionService.getCompletedTransaksi();

    if (riwayat.isEmpty()) {
        System.out.println("Belum ada transaksi selesai.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("riwayat_transaksi.txt"))) {
        for (Transaksi t : riwayat) {
            String line = t.getId() + " | " + t.getCustomer().getUsername() 
                          + " | Total: " + t.getTotal() 
                          + " | Accepted: " + t.isAccepted();
            System.out.println(line);
            writer.write(line);
            writer.newLine();
        }
        System.out.println("Riwayat transaksi juga tersimpan di file riwayat_transaksi.txt");
    } catch (Exception e) {
        System.out.println("Gagal menyimpan riwayat transaksi ke file: " + e.getMessage());
    }
}


}
