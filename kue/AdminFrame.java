package kue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class AdminFrame extends JFrame {
    // Asumsi: Kelas Admin, Produk, Transaksi, ProdukService, TransactionService, AuthService, 
    // dan LoginFrame/WelcomeWindow ada.

    private Admin admin;
    private ProdukService produkService;
    private TransactionService transaksiService;
    private AuthService auth;
    private JPanel productButtonPanel; 
    private JPanel rightContentWrapper; 
    private JLabel labelInfo; 
    private enum ProductActionMode { VIEW, EDIT, DELETE }
    private ProductActionMode currentMode = ProductActionMode.VIEW;

    public AdminFrame(Admin admin, ProdukService ps, TransactionService ts, AuthService auth) {

        this.admin = admin;
        this.produkService = ps;
        this.transaksiService = ts;
        this.auth = auth;

        setTitle("RASA.IN: Menu Admin - " + admin.getFullName());
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 238, 240));

        JLabel title = new JLabel("Menu Admin - RASA.IN (" + admin.getFullName() + ")", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));

        // TOMBOL MENU ADMIN (LEFT - SAMA RATA)
        
        Dimension menuButtonSize = new Dimension(200, 35); 
        
        JButton btnList = new JButton("Daftar Produk");
        btnList.setPreferredSize(menuButtonSize);
        JButton btnAdd = new JButton("Tambah Produk");
        btnAdd.setPreferredSize(menuButtonSize);
        JButton btnEdit = new JButton("Edit Produk");
        btnEdit.setPreferredSize(menuButtonSize);
        JButton btnDelete = new JButton("Hapus Produk");
        btnDelete.setPreferredSize(menuButtonSize);
        JButton btnPending = new JButton("Lihat & Terima Pesanan"); 
        btnPending.setPreferredSize(menuButtonSize);
        JButton btnHistory = new JButton("Riwayat Transaksi");
        btnHistory.setPreferredSize(menuButtonSize);
        
        JButton btnLogout = new JButton("Logout"); 
        btnLogout.setPreferredSize(menuButtonSize);
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setForeground(Color.BLACK); 
        
        JPanel menu = new JPanel(new GridLayout(7, 1, 8, 8)); 
        menu.setOpaque(false);
        menu.add(btnList);
        menu.add(btnAdd);
        menu.add(btnEdit);
        menu.add(btnDelete);
        menu.add(btnPending);
        menu.add(btnHistory);
        menu.add(btnLogout);
        
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        left.add(menu, BorderLayout.CENTER); 

        // KONTEN KANAN (RIGHT) - INITIAL SETUP
        
        rightContentWrapper = new JPanel(new BorderLayout()); 
        
        labelInfo = new JLabel("Daftar Produk:"); 
        labelInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        right.add(labelInfo, BorderLayout.NORTH);
        right.add(rightContentWrapper, BorderLayout.CENTER); 
        
        showProductListView();

        
        // LAYOUT UTAMA
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; 
        
        //JUDUL
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0; 
        gbc.weighty = 0.0; 
        add(title, gbc);

        //PANEL KIRI (Tombol Menu)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; 
        gbc.weightx = 0.2; 
        gbc.weighty = 1.0; 
        add(left, gbc);

        //PANEL KANAN (Konten)
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.8; 
        gbc.weighty = 1.0; 
        add(right, gbc);

        // EVENT LISTENERS         
        btnList.addActionListener(e -> showProductListView());
        btnAdd.addActionListener(e -> showAddProductView());
        btnEdit.addActionListener(e -> showActionProductView(ProductActionMode.EDIT));
        btnDelete.addActionListener(e -> showActionProductView(ProductActionMode.DELETE));
        btnPending.addActionListener(e -> showPendingTransactionsView());
        btnHistory.addActionListener(e -> showHistoryView());
        
        // LOGOUT LISTENER 
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Anda yakin ingin logout?", 
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                //Tutup AdminFrame saat ini
                dispose();
                
                //Tampilkan Frame Login/Welcome yang baru
                try {
                    new WelcomeWindow(this.auth, this.produkService, this.transaksiService).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: Tidak bisa memuat frame login. Pastikan kelas LoginFrame/WelcomeWindow sudah ada dan memiliki konstruktor yang benar.", "Logout Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        this.revalidate();
        this.repaint();
    }

    // METODE VIEW PRODUK (Daftar, Edit, Hapus)
    private void showProductListView() {
        currentMode = ProductActionMode.VIEW; 
        rightContentWrapper.removeAll();
        labelInfo.setText("Daftar Produk: (Klik 'Detail' untuk melihat info)");
        
        productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        productButtonPanel.setBackground(new Color(245, 245, 245)); 
        productButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        refreshProductButtons(); 

        JScrollPane scroll = new JScrollPane(productButtonPanel);
        scroll.getViewport().setBackground(productButtonPanel.getBackground());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        rightContentWrapper.add(scroll, BorderLayout.CENTER);
        
        rightContentWrapper.revalidate(); 
        rightContentWrapper.repaint(); 
    }
    
    private void showActionProductView(ProductActionMode mode) {
        currentMode = mode; 
        rightContentWrapper.removeAll();
        
        String action = (mode == ProductActionMode.EDIT) ? "EDIT" : "HAPUS";
        labelInfo.setText("Pilih Produk untuk di" + action.toUpperCase() + ":");
        
        productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        productButtonPanel.setBackground(new Color(245, 245, 245)); 
        productButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        refreshProductButtons(); 

        JScrollPane scroll = new JScrollPane(productButtonPanel);
        scroll.getViewport().setBackground(productButtonPanel.getBackground());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        rightContentWrapper.add(scroll, BorderLayout.CENTER);
        
        rightContentWrapper.revalidate(); 
        rightContentWrapper.repaint(); 
    }

    private void refreshProductButtons() {
        productButtonPanel.removeAll(); 
        final int BOX_SIZE = 150; 

        List<Produk> produkList = produkService.getAll();
        
        for (Produk p : produkList) {
            JPanel productBox = new JPanel(new BorderLayout()); 
            productBox.setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            productBox.setMaximumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            productBox.setMinimumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            
            productBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5) 
            ));
            productBox.setBackground(Color.WHITE); 
            
            //Panel Detail Produk
            JPanel detailPanel = new JPanel();
            detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
            detailPanel.setOpaque(false); 

            JLabel imagePlaceholder = new JLabel(" [Gambar Produk] ", SwingConstants.CENTER);
            imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); 

            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel detailLabel = new JLabel("Rp" + String.format("%,.0f", p.getPrice()) + " | Stok: " + p.getStok(), SwingConstants.CENTER);
            detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            detailPanel.add(imagePlaceholder);
            detailPanel.add(nameLabel);
            detailPanel.add(detailLabel);
            detailPanel.add(Box.createVerticalGlue()); 
            
            //Tombol Aksi (SOUTH)
            JButton actionButton;
            
            if (currentMode == ProductActionMode.VIEW) {
                actionButton = new JButton("Detail");
            } else if (currentMode == ProductActionMode.EDIT) {
                actionButton = new JButton("EDIT");
                actionButton.setBackground(new Color(255, 230, 150)); 
            } else { 
                actionButton = new JButton("HAPUS");
                actionButton.setBackground(new Color(255, 150, 150)); 
            }
            
            actionButton.setFont(new Font("SansSerif", Font.BOLD, 10));
            
            //Tambahkan Listener
            if (currentMode == ProductActionMode.VIEW) {
                   actionButton.addActionListener(e -> showProductDetail(p));
            } else if (currentMode == ProductActionMode.EDIT) {
                actionButton.addActionListener(e -> showEditProductForm(p));
            } else {
                actionButton.addActionListener(e -> handleDeletion(p));
            }
            
            JPanel buttonWrapper = new JPanel(new BorderLayout());
            buttonWrapper.setOpaque(false);
            buttonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2)); 
            buttonWrapper.add(actionButton, BorderLayout.CENTER);
            
            productBox.add(detailPanel, BorderLayout.CENTER); 
            productBox.add(buttonWrapper, BorderLayout.SOUTH);
            
            productButtonPanel.add(productBox);
        }
        
        productButtonPanel.revalidate(); 
        productButtonPanel.repaint(); 
    }
    
    private void showProductDetail(Produk p) {
        JOptionPane.showMessageDialog(
            this,
            "ID: " + p.getId() + 
            "\nNama: " + p.getName() + 
            "\nDeskripsi: " + p.getDeskripsi() +
            "\nHarga: Rp" + String.format("%,.0f", p.getPrice()) + 
            "\nStok: " + p.getStok(),
            "Detail Produk: " + p.getName(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void handleDeletion(Produk p) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus produk '" + p.getName() + "' (ID: " + p.getId() + ")?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            
            produkService.removeProduk(p.getId());
            JOptionPane.showMessageDialog(this, "Produk terhapus.");
            showActionProductView(ProductActionMode.DELETE); 
        }
    }


    // --- Metode-metode Form (showAddProductView) ---
    
    private void showAddProductView() {
        currentMode = ProductActionMode.VIEW; 
        rightContentWrapper.removeAll();
        labelInfo.setText("Tambah Produk Baru:");

        JTextField txtId = new JTextField(20); 
        JTextField txtNama = new JTextField(20);
        JTextField txtDes = new JTextField(20);
        JTextField txtHarga = new JTextField(20);
        JTextField txtStok = new JTextField(20);
        JButton btnSimpan = new JButton("Simpan Produk Baru");
        
        JPanel formPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.weightx = 0.0; 
        formPanel.add(new JLabel("ID Produk:"), gbc);
        gbc.gridx = 1; 
        gbc.weightx = 1.0; 
        formPanel.add(txtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtNama, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtDes, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtHarga, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtStok, gbc);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0.0; formPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridx = 0; gbc.gridy = row+1; gbc.gridwidth = 2; gbc.weightx = 1.0; formPanel.add(btnSimpan, gbc);
        
        
        JPanel centerWrapper = new JPanel(new GridBagLayout()); 
        centerWrapper.setOpaque(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0; 
        c.weighty = 1.0; 
        c.fill = GridBagConstraints.NONE; 
        centerWrapper.add(formPanel, c);
        
        rightContentWrapper.add(centerWrapper, BorderLayout.CENTER);
        
        btnSimpan.addActionListener(e -> {
            try {
                // Asumsi kelas Produk memiliki konstruktor ini
                Produk pNew = new Produk(
                        txtId.getText().trim(),
                        txtNama.getText().trim(),
                        txtDes.getText().trim(),
                        Double.parseDouble(txtHarga.getText().trim()),
                        Integer.parseInt(txtStok.getText().trim()));

                // Asumsi metode addProduk(p) ada di ProdukService
                boolean sukses = produkService.addProduk(pNew);

                if (!sukses) {
                    JOptionPane.showMessageDialog(this, "ID sudah dipakai!", "Gagal", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
                    showProductListView(); 
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid! Pastikan ID, harga, dan stok terisi dengan benar (harga dan stok harus angka).");
            }
        });

        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    private void showEditProductForm(Produk old) {
        currentMode = ProductActionMode.VIEW; 
        rightContentWrapper.removeAll();
        labelInfo.setText("Edit Produk ID: " + old.getId());

        JTextField txtNama = new JTextField(old.getName(), 20);
        JTextField txtDes = new JTextField(old.getDeskripsi(), 20);
        JTextField txtHarga = new JTextField(String.valueOf(old.getPrice()), 20);
        JTextField txtStok = new JTextField(String.valueOf(old.getStok()), 20);
        JButton btnSimpan = new JButton("Simpan Perubahan");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Nama baru:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtNama, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtDes, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtHarga, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.weightx = 0.0; formPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtStok, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0.0; formPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridx = 0; gbc.gridy = row+1; gbc.gridwidth = 2; gbc.weightx = 1.0; formPanel.add(btnSimpan, gbc);

        JPanel centerWrapper = new JPanel(new GridBagLayout()); 
        centerWrapper.setOpaque(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0; 
        c.weighty = 1.0; 
        c.fill = GridBagConstraints.NONE; 
        centerWrapper.add(formPanel, c);
        
        rightContentWrapper.add(centerWrapper, BorderLayout.CENTER);
        
        btnSimpan.addActionListener(e -> {
            try {
                // Asumsi kelas Produk memiliki konstruktor ini
                Produk newP = new Produk(
                        old.getId(),
                        txtNama.getText().trim(),
                        txtDes.getText().trim(),
                        Double.parseDouble(txtHarga.getText().trim()),
                        Integer.parseInt(txtStok.getText().trim()));

                // Asumsi metode editProduk(p) ada di ProdukService
                produkService.editProduk(newP);
                JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!");
                showProductListView(); 

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid! Pastikan harga dan stok adalah angka.");
            }
        });
        
        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    // METODE VIEW: TRANSAKSI PENDING (DIUBAH KE BENTUK KOTAK)
    private void showPendingTransactionsView() {
        currentMode = ProductActionMode.VIEW; 
        rightContentWrapper.removeAll();
  
        labelInfo.setText("Transaksi Pending (Klik 'Terima' untuk menyetujui):");
        
        // Asumsi metode getPendingTransaksi() ada di TransactionService
        List<Transaksi> pend = transaksiService.getPendingTransaksi();
        
        JPanel pendingPanel = new JPanel(new BorderLayout(10, 10));
        pendingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        if (pend.isEmpty()) {
            JLabel emptyLabel = new JLabel("Tidak ada transaksi pending.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0; c.gridy = 0;
            c.weightx = 1.0; c.weighty = 1.0; 
            centerWrapper.add(emptyLabel, c);
            
            rightContentWrapper.add(centerWrapper, BorderLayout.CENTER);
            
        } else {
            
            JPanel pendingBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            pendingBoxPanel.setBackground(new Color(245, 245, 245)); 

            for (Transaksi t : pend) {
                pendingBoxPanel.add(createTransactionBox(t));
            }

            JScrollPane scroll = new JScrollPane(pendingBoxPanel);
            
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
            scroll.getViewport().setBackground(pendingBoxPanel.getBackground());
            
            pendingPanel.add(scroll, BorderLayout.CENTER);
            
            rightContentWrapper.add(pendingPanel, BorderLayout.CENTER);
        }
        
        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    //Metode Baru: Membuat Kotak Transaksi Pending
    private JPanel createTransactionBox(Transaksi t) {
        final int BOX_SIZE_WIDTH = 250; 
        final int BOX_SIZE_HEIGHT = 150; 

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS)); 
        box.setPreferredSize(new Dimension(BOX_SIZE_WIDTH, BOX_SIZE_HEIGHT)); 
        box.setMaximumSize(new Dimension(BOX_SIZE_WIDTH, BOX_SIZE_HEIGHT)); 
        
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 200), 2), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        ));
        box.setBackground(new Color(230, 245, 255)); 

        // Asumsi t.getTotal(), t.getId(), t.getCustomer().getUsername(), t.getCreatedAt() tersedia
        String totalFormatted = "Rp" + String.format("%,.0f", t.getTotal()); 
        
        JLabel idLabel = new JLabel("ID: " + t.getId());
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel userLabel = new JLabel("Customer: " + t.getCustomer().getUsername());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Tanggal: " + t.getCreatedAt());
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel totalLabel = new JLabel(totalFormatted);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 120, 0)); 
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btnAccept = new JButton("Terima Pesanan");
        btnAccept.setBackground(new Color(150, 255, 150));
        btnAccept.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAccept.setMaximumSize(new Dimension(BOX_SIZE_WIDTH - 20, 30)); 

        // ActionListener untuk tombol 'Terima Pesanan'
        btnAccept.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Terima transaksi ID: " + t.getId() + " dari " + t.getCustomer().getUsername() + "?", 
                "Konfirmasi Penerimaan", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Asumsi metode acceptTransaction(id) ada di TransactionService
                    transaksiService.acceptTransaction(t.getId());
                    JOptionPane.showMessageDialog(this, "Transaksi ID " + t.getId() + " berhasil diterima!");
                    // Refresh view
                    showPendingTransactionsView(); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menerima transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        box.add(idLabel);
        box.add(userLabel);
        box.add(dateLabel);
        box.add(Box.createVerticalGlue());
        box.add(totalLabel);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        box.add(btnAccept); 

        return box;
    }
    
    // METODE VIEW: RIWAYAT TRANSAKSI (TIDAK DIUBAH)
    private void showHistoryView() {
        currentMode = ProductActionMode.VIEW; 
        rightContentWrapper.removeAll();
        labelInfo.setText("Riwayat Transaksi (Sudah Diterima):");
        
        // Asumsi metode getCompletedTransaksi() ada di TransactionService
        List<Transaksi> riwayat = transaksiService.getCompletedTransaksi();
        
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (riwayat.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada transaksi yang diterima.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);
            
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0; c.gridy = 0;
            c.weightx = 1.0; c.weighty = 1.0; 
            centerWrapper.add(emptyLabel, c);
            
            rightContentWrapper.add(centerWrapper, BorderLayout.CENTER);
            
        } else {
            
            String[] columnNames = {"ID Transaksi", "Customer", "Total Harga", "Tanggal"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Transaksi t : riwayat) {
                String totalFormatted = String.format("Rp%,.0f", t.getTotal()); 
                
                Object[] rowData = {
                    t.getId(), 
                    t.getCustomer().getUsername(), 
                    totalFormatted, 
                    t.getCreatedAt()
                };
                model.addRow(rowData);
            }
            
            JTable historyTable = new JTable(model);
            historyTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
            historyTable.setAutoCreateRowSorter(true); 
            historyTable.setRowHeight(25);
            historyTable.setFillsViewportHeight(true);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            
            for (int i = 0; i < historyTable.getColumnModel().getColumnCount(); i++) {
                historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
            historyTable.getColumnModel().getColumn(0).setPreferredWidth(100); 
            historyTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
            historyTable.getColumnModel().getColumn(2).setPreferredWidth(120); 
            historyTable.getColumnModel().getColumn(3).setPreferredWidth(150); 
            
            historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
            
            JScrollPane scroll = new JScrollPane(historyTable);
            
            JButton btnExport = new JButton("Export ke riwayat_transaksi.txt");
            
            JPanel buttonWrapper = new JPanel(new BorderLayout());
            buttonWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); 
            buttonWrapper.add(btnExport, BorderLayout.CENTER); 
            
            historyPanel.add(scroll, BorderLayout.CENTER);
            historyPanel.add(buttonWrapper, BorderLayout.SOUTH);

            btnExport.addActionListener(e -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("riwayat_transaksi.txt"))) {
                    writer.write("ID Transaksi | Customer | Total Harga | Tanggal");
                    writer.newLine();
                    writer.write("-------------------------------------------------");
                    writer.newLine();

                    for (Transaksi t : riwayat) {
                        String line = t.getId() + " | " + t.getCustomer().getUsername() + " | " + t.getTotal() + " | " + t.getCreatedAt();
                        writer.write(line);
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(this, "Riwayat transaksi berhasil disimpan ke file 'riwayat_transaksi.txt'!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            rightContentWrapper.add(historyPanel, BorderLayout.CENTER);
        }
        
        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }

}
