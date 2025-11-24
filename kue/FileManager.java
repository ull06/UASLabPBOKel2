package kue;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class FileManager {

    private String productPath = "produk.txt";
    private String accountPath = "akun.txt";
    private String transactionPath = "transaksi.txt";
    private String invoicePath = "invoice.txt";
    private String norekPath = "rekening.txt";

    // =========================================================
    // PRODUCT
    // =========================================================
    public List<Produk> readProduk() {
        List<Produk> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(productPath))) {

            String line;
            while ((line = br.readLine()) != null) {

                // Format: id;nama;deskripsi;harga;stok
                String[] d = line.split(";");
                if (d.length < 5)
                    continue;

                String id = d[0];
                String nama = d[1];
                String des = d[2];
                double harga = Double.parseDouble(d[3]);
                int stok = Integer.parseInt(d[4]);

                list.add(new Produk(id, nama, des, harga, stok));
            }

        } catch (Exception e) {
            System.out.println("Gagal membaca produk: " + e.getMessage());
        }

        return list;
    }

    public void writeProducts(List<Produk> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(productPath))) {

            for (Produk p : list) {
                pw.println(
                        p.getId() + ";" +
                                p.getName() + ";" +
                                p.getDeskripsi() + ";" +
                                p.getPrice() + ";" +
                                p.getStok());
            }

        } catch (Exception e) {
            System.out.println("Gagal menulis produk: " + e.getMessage());
        }
    }

    // =========================================================
    // ACCOUNT
    // =========================================================
    public Map<String, Akun> readAccounts() {
        Map<String, Akun> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(accountPath))) {

            String line;
            while ((line = br.readLine()) != null) {
                // Format: username;password;fullname;role;alamat;phone;email
                String[] d = line.split(";");
                if (d.length < 7)
                    continue;

                String user = d[0];
                String pw = d[1];
                String name = d[2];
                String role = d[3];

                String alamat = d[4];
                String phone = d[5];
                String email = d[6];

                if (role.equalsIgnoreCase("ADMIN")) {
                    map.put(user, new Admin(user, pw, name));

                } else if (role.equalsIgnoreCase("CUSTOMER")) {
                    UserProfile up = new UserProfile(alamat, phone, email);
                    map.put(user, new Customer(user, pw, name, up));
                }
            }

        } catch (Exception e) {
            System.out.println("Gagal membaca akun: " + e.getMessage());
        }

        return map;
    }

    public void writeAccounts(Map<String, Akun> map) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(accountPath))) {

            for (Akun a : map.values()) {

                if (a instanceof Admin) {
                    pw.println(a.getUsername() + ";" +
                            a.getPassword() + ";" +
                            a.getFullName() + ";" +
                            a.getRole() + ";-;-;-");

                } else if (a instanceof Customer c) {
                    UserProfile up = c.getProfile();

                    pw.println(a.getUsername() + ";" +
                            a.getPassword() + ";" +
                            a.getFullName() + ";" +
                            a.getRole() + ";" +
                            up.getAddress() + ";" +
                            up.getPhoneNumber() + ";" +
                            up.getEmail());
                }
            }

        } catch (Exception e) {
            System.out.println("Gagal menulis akun: " + e.getMessage());
        }
    }

    // =========================================================
    // TRANSAKSI
    // =========================================================
    public List<Transaksi> readTransactions() {
        List<Transaksi> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(transactionPath))) {

            String line;
            while ((line = br.readLine()) != null) {

                // Format:
                // id;username;total;createdAt;accepted
                String[] d = line.split(";");
                if (d.length < 5)
                    continue;

                String id = d[0];
                String username = d[1];
                double total = Double.parseDouble(d[2]);
                LocalDateTime created = LocalDateTime.parse(d[3]);
                boolean accepted = Boolean.parseBoolean(d[4]);

                // Customer dummy → nanti dihubungkan runtime
                Customer c = new Customer(username, "", "", new UserProfile("-", "-", "-"));

                Transaksi t = new Transaksi(id, c, total, created, accepted, null);

                list.add(t);
            }

        } catch (Exception e) {
            System.out.println("Gagal membaca transaksi: " + e.getMessage());
        }

        return list;
    }

    public void writeTransactions(List<Transaksi> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(transactionPath))) {

            for (Transaksi t : list) {
                pw.println(
                        t.getId() + ";" +
                                t.getCustomer().getUsername() + ";" +
                                t.getTotal() + ";" +
                                t.getCreatedAt() + ";" +
                                t.isAccepted());
            }

        } catch (Exception e) {
            System.out.println("Gagal menulis transaksi: " + e.getMessage());
        }
    }

    // =========================================================
    // INVOICE
    // =========================================================
    public void appendInvoice(Invoice inv) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(invoicePath, true))) {

            pw.println(inv.toFileFormat());

        } catch (Exception e) {
            System.out.println("Gagal menulis invoice: " + e.getMessage());
        }
    }


    // =========================================================
    // REKENING BANK TOKO (lebih dari 1)
    // =========================================================
    public Map<String, String> readRekeningToko() {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(norekPath))) {

            String line;
            while ((line = br.readLine()) != null) {

                // Format: BANK;NOREK
                String[] d = line.split(";");
                if (d.length < 2)
                    continue;

                String bank = d[0].trim();
                String norek = d[1].trim();

                map.put(bank.toUpperCase(), norek);
            }

        } catch (Exception e) {
            System.out.println("Gagal membaca rekening bank: " + e.getMessage());
        }

        return map;
    }

    
}
