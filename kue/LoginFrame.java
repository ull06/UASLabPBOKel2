package kue;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // service yang dibawa dari MainGUI
    private AuthService auth;
    private ProdukService produkService;
    private TransactionService transactionService;
    
    // **DEFINISIKAN WARNA PINK YANG LEBIH MUDA**
    private final Color palePink = new Color(255, 240, 245); 
    // **DEFINISIKAN WARNA COKLAT (e.g., Coklat Tua)**
    private final Color darkBrown = new Color(101, 67, 33); 

    // Konstruktor menerima 3 service
    public LoginFrame(AuthService auth, ProdukService ps, TransactionService ts) {
        this.auth = auth;
        this.produkService = ps;
        this.transactionService = ts;

        // ====== Pengaturan jendela GUI ======
        setTitle("Login - RASA.IN");
        setSize(450, 350); // Ukuran awal sedikit diperbesar
        setMinimumSize(new Dimension(350, 300)); // Tambahkan ukuran minimum
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ==========================
        // JUDUL RASA.IN
        // ==========================
        JLabel headerLabel = new JLabel("RASA.IN", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 34)); 
        headerLabel.setForeground(darkBrown); 
        headerLabel.setBackground(palePink); 
        headerLabel.setOpaque(true); 
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); 

        // ==========================
        // FORM INPUT (GridBagLayout untuk Kontrol Lebar & Posisi)
        // ==========================
        
        // GANTI DARI GridLayout ke GridBagLayout
        JPanel formContent = new JPanel(new GridBagLayout()); 
        formContent.setBackground(palePink); 
        formContent.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Kurangi padding luar

        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(darkBrown);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(darkBrown);

        // --- GridBagConstraints untuk Penataan ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5); // Padding vertikal antar baris

        // --- BARIS USERNAME ---
        int row = 0;
        
        // 1. Kolom Kiri Kosong (Menyerap Ruang Ekstra)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        formContent.add(new JPanel() {{ setOpaque(false); }}, gbc);

        // 2. Kolom Label (Username)
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST; // Rata kanan label
        formContent.add(userLabel, gbc);

        // 3. Kolom Text Field (Lebar Terbatas)
        gbc.gridx = 2; gbc.weightx = 0; // TIDAK ADA WEIGHT di sini agar text field tidak meregang
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUser.setPreferredSize(new Dimension(250, 30)); // Set lebar yang wajar
        formContent.add(txtUser, gbc);

        // 4. Kolom Kanan Kosong (Menyerap Ruang Ekstra)
        gbc.gridx = 3; gbc.weightx = 1.0; 
        formContent.add(new JPanel() {{ setOpaque(false); }}, gbc);

        // --- BARIS PASSWORD ---
        row++;
        
        // 1. Kolom Kiri Kosong
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formContent.add(new JPanel() {{ setOpaque(false); }}, gbc);

        // 2. Kolom Label (Password)
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formContent.add(passLabel, gbc);

        // 3. Kolom Text Field (Lebar Terbatas)
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPass.setPreferredSize(new Dimension(250, 30)); // Set lebar yang sama
        formContent.add(txtPass, gbc);

        // 4. Kolom Kanan Kosong
        gbc.gridx = 3; gbc.weightx = 1.0;
        formContent.add(new JPanel() {{ setOpaque(false); }}, gbc);

        // ==========================
        // PANEL UTAMA (Menggabungkan Header dan Form Input)
        // ==========================
        JPanel panelUtama = new JPanel(new BorderLayout()); 
        panelUtama.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12)); 
        panelUtama.setBackground(palePink); 
        
        panelUtama.add(headerLabel, BorderLayout.NORTH); 
        panelUtama.add(formContent, BorderLayout.CENTER); // FormContent yang sudah di-GridBagLayout

        // ==========================
        // Panel tombol login & register
        // ==========================
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15)); // FlowLayout sudah cukup
        tombol.setBackground(palePink);
        
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        
        btnLogin.setBackground(Color.WHITE); 
        btnLogin.setForeground(darkBrown);
        btnRegister.setBackground(Color.WHITE);
        btnRegister.setForeground(darkBrown);

        tombol.add(btnLogin);
        tombol.add(btnRegister);

        // Finalisasi JFrame
        getContentPane().setBackground(palePink);
        add(panelUtama, BorderLayout.CENTER);
        add(tombol, BorderLayout.SOUTH);

        // ==========================
        // EVENT LISTENER (Logika tidak diubah)
        // ==========================
        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword()).trim();

            Akun akun = auth.login(u, p);

            if (akun == null) { 
                JOptionPane.showMessageDialog(this, "Login gagal!");
                return; 
            }

            dispose(); 
            if (akun instanceof Admin admin) {
                new AdminFrame(admin, produkService, transactionService, auth).setVisible(true);
            } else {
                new CustomerFrame((Customer) akun, produkService, transactionService, auth).setVisible(true);
            }
        });

        btnRegister.addActionListener(e -> {
            dispose();
            new WelcomeWindow(auth, produkService, transactionService).setVisible(true);
        });
    }
}
//loginframe.java
