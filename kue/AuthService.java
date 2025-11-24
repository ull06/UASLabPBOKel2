package kue;

import java.util.Map;
import java.util.HashMap;

public class AuthService {

    private Map<String, Akun> akunMap; // penyimpan semua akun (ADMIN + CUSTOMER)
    private FileManager fileManager; // untuk baca/tulis file akun

    public AuthService(FileManager fileManager) {
        this.fileManager = fileManager;
        this.akunMap = new HashMap<>();
    }

    // ==========================================================
    // LOAD AKUN DARI FILE → dipanggil saat program mulai
    // ==========================================================
    public void loadAccounts() {
        try {
            akunMap = fileManager.readAccounts();
        } catch (Exception e) {
            System.out.println("Gagal memuat akun: " + e.getMessage());
        }
    }

    // ==========================================================
    // SAVE AKUN KE FILE → untuk register atau update password
    // ==========================================================
    public void saveAccounts() {
        try {
            fileManager.writeAccounts(akunMap);
        } catch (Exception e) {
            System.out.println("Gagal menyimpan akun: " + e.getMessage());
        }
    }

    // ==========================================================
    // LOGIN → cek username + password
    // ==========================================================
    public Akun login(String username, String password) {
        if (!akunMap.containsKey(username))
            return null;

        Akun a = akunMap.get(username);

        // cek password menggunakan method checkPw milik Akun
        if (a.checkPw(password)) {
            return a;
        }
        return null;
    }

    // ==========================================================
    // REGISTER CUSTOMER BARU
    // ==========================================================
    public boolean registerCustomer(Customer c) {

        if (akunMap.containsKey(c.getUsername())) {
            return false; // username sudah dipakai
        }

        // langsung simpan customer yang sudah lengkap
        akunMap.put(c.getUsername(), c);

        // simpan ke file
        saveAccounts();

        return true;
    }


    //REGISTER ADMIN BARU
    public boolean registerAdmin(Admin a) {
        if (akunMap.containsKey(a.getUsername())) {
            return false; // username sudah dipakai
        }

        akunMap.put(a.getUsername(), a);
        saveAccounts(); // simpan ke file
        return true;
    }
}
