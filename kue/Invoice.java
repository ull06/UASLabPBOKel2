package kue;

import java.time.LocalDateTime;

public class Invoice {
    private Transaksi transaksi;
    private Pembayaran pembayaran;
    private LocalDateTime tanggal;

    public Invoice(Transaksi transaksi, Pembayaran pembayaran) {
        this.transaksi = transaksi;
        this.pembayaran = pembayaran;
        this.tanggal = LocalDateTime.now();
    }

    // --- BARIS TAMBAHAN UNTUK GUI (Getter) ---
    public Transaksi getTransaksi() { return transaksi; }
    public Pembayaran getPembayaran() { return pembayaran; }
    public LocalDateTime getTanggal() { return tanggal; }
    // ------------------------------------------

    public void printInvoice() {
        System.out.println("====== INVOICE ======");
        System.out.println("Tanggal   : " + tanggal);
        System.out.println("ID        : " + transaksi.getId());
        System.out.println("Customer  : " + transaksi.getCustomer().getFullName());

        System.out.println("\nItems:");
        for (CartItem item : transaksi.getItems()) {
            System.out.println("- " + item.getProduk().getName() +
                    " x" + item.getJumlah() + " = Rp" + item.getSubtotal());
        }

        System.out.println("\nTotal: Rp" + transaksi.getTotal());
        System.out.println("Metode Pembayaran: " + pembayaran.getMethodName());
    }

    public String toFileFormat() {

        StringBuilder sb = new StringBuilder();
        sb.append(transaksi.getId()).append(";")
          .append(tanggal).append(";")
          .append(pembayaran.getMethodName()).append(";");
    
        // encode items
        for (CartItem item : transaksi.getItems()) {
            sb.append(item.getProduk().getId())
              .append(",")
              .append(item.getJumlah())
              .append(",")
              .append(item.getSubtotal())
              .append("|");
        }
    
        // hapus '|' terakhir
        if (sb.charAt(sb.length() - 1) == '|')
            sb.deleteCharAt(sb.length() - 1);
    
        return sb.toString();
    }
    
}