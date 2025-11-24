package kue;

import javax.swing.*;

// import kue.*;
// import gui.LoginFrame;

public class MainGUI {
    public static void main(String[] args) {

        // siapkan semua objek service dan file

        // FileManager = kelas untuk baca / tulis file
        FileManager fm = new FileManager();

        // ListProduk = struktur data penyimpan produk
        ListProduk lp = new ListProduk();

        // ProdukService = otak pengelolaan produk
        // digabung dengan ListProduk & FileManager
        ProdukService ps = new ProdukService(lp, fm);

        // Load semua produk dari file ke memori
        ps.loadProduk();

        // AuthService = untuk login & register user
        AuthService auth = new AuthService(fm);
        auth.loadAccounts();

        // TransactionService = proses checkout / pembayaran / pending
        TransactionService ts = new TransactionService(fm, ps);
        ts.loadTransaksi();

        // Kirim semua service ke GUI agar bisa dipakai nanti
        //new LoginFrame(auth, ps, ts).setVisible(true);
        
        // buka halaman welcome dulu
        SwingUtilities.invokeLater(() -> new WelcomeWindow(auth, ps, ts).setVisible(true));
    }
}
