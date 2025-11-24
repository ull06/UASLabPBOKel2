package kue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDriver extends Driver {
    private Customer customer;
    private ProdukService produkService;
    private TransactionService transactionService;

    public CustomerDriver(Customer customer, ProdukService produkService, TransactionService transactionService) {
        this.customer = customer;
        this.produkService = produkService;
        this.transactionService = transactionService;
    }

    @Override
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== MENU CUSTOMER (" + customer.getFullName() + ") ===");
            System.out.println("1. Lihat Produk");
            System.out.println("2. Tambah ke Keranjang");
            System.out.println("3. Lihat Keranjang");
            System.out.println("4. Checkout");
            System.out.println("5. Lihat Riwayat Transaksi");
            System.out.println("6. Logout");
            System.out.print("Pilih: ");

            String pilih = input.nextLine().trim();
            switch (pilih) {
                case "1" -> viewProducts();
                case "2" -> addToCart();
                case "3" -> viewCart();
                case "4" -> checkout();
                case "5" -> viewHistory();
                case "6" -> running = false;
                default -> System.out.println("Pilihan tidak dikenal");
            }
        }
    }

    private void viewProducts() {
        produkService.printAllProduk();
    }

    private void addToCart() {
        System.out.print("Masukkan ID produk: ");
        String id = input.nextLine().trim();
        Produk p = produkService.findById(id);
        if (p == null) {
            System.out.println("Produk tidak ditemukan.");
            return;
        }
        System.out.print("Jumlah: ");
        int qty = Integer.parseInt(input.nextLine().trim());
        try {
            customer.getCart().addProduk(p, qty);
            System.out.println("Berhasil ditambahkan ke keranjang.");
        } catch (Exception e) {
            System.out.println("Gagal menambahkan ke keranjang: " + e.getMessage());
        }
    }

    
    private void viewCart() {
        System.out.println("-- Keranjang --");
        List<CartItem> items = customer.getCart().getItems();
        if (items.isEmpty()) {
            System.out.println("Keranjang kosong.");
            return;
        }
        for (CartItem ci : items) {
            System.out.println(ci.getProduk().getId() + " - " + ci.getProduk().getName() + " x" + ci.getJumlah()
                    + " = Rp" + ci.getSubtotal());
        }
        System.out.println("Total: Rp" + customer.getCart().totalHarga());
    }

    
    private void checkout() {
        if (customer.getCart().getItems().isEmpty()) {
            System.out.println("Keranjang kosong.");
            return;
        }

        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. QRIS");
        System.out.println("2. COD");
        System.out.println("3. Transfer Bank");
        System.out.print("Pilih: ");
        String pilih = input.nextLine().trim();

        Pembayaran bayar = null;
        Map<String, String> info = new HashMap<>();

        switch (pilih) {
            case "1" -> bayar = new QRISPayment();
            case "2" -> {
                bayar = new CODPayment(customer.getProfile().getAddress());
            }
            case "3" -> {
                System.out.println("Pilih bank: 1.BSI 2.MANDIRI 3.ACEH");
                String pbank = input.nextLine().trim();
                switch (pbank) {
                    case "1" -> bayar = new BankBSI();
                    case "2" -> bayar = new BankMandiri();
                    case "3" -> bayar = new BankAceh();
                    default -> {
                        System.out.println("Bank tidak dikenal");
                        return;
                    }
                }
            }
            default -> {
                System.out.println("Metode tidak valid");
                return;
            }
        }

        try {
            Transaksi t = transactionService.createTransaksi(customer, customer.getCart(), bayar);
            System.out.println("Checkout sukses! ID: " + t.getId());
            System.out.println("Silakan tunggu verifikasi admin.");
        } catch (Exception e) {
            System.out.println("Checkout gagal: " + e.getMessage());
        }
    }


        private void viewHistory() {
        System.out.println("-- Riwayat Transaksi --");
        List<Transaksi> hist = transactionService.getHistory(customer);
        if (hist.isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        for (Transaksi t : hist) {
            System.out.println(
                    t.getId() + " | " + t.getTotal() + " | " + t.getCreatedAt() + " | accepted=" + t.isAccepted());
        }
    }
    
}

