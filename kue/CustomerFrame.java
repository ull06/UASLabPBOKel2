package kue;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;
import java.time.format.DateTimeFormatter; 

public class CustomerFrame extends JFrame {

    private Customer customer;
    private ProdukService produkService;
    private TransactionService transactionService;
    private AuthService authService;
    
    private JPanel productButtonPanel; 
    private JPanel cartButtonPanel; 
    
    private JLabel labelInfo; 
    private JPanel rightContentWrapper;

    // Helper untuk format Rupiah
    private static final NumberFormat IDR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    // Helper untuk format tanggal 
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CustomerFrame(Customer c, ProdukService ps, TransactionService ts, AuthService auth) {
        this.customer = c;
        this.produkService = ps;
        this.transactionService = ts;
        this.authService = auth;

        setTitle("RASA.IN - Customer: " + customer.getFullName());
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 238, 240));

        JLabel title = new JLabel("Menu Customer - RASA.IN", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));

        // TOMBOL MENU CUSTOMER 
        JButton btnView = new JButton("Lihat Produk");
        JButton btnCart = new JButton("Lihat Keranjang");
        JButton btnCheckout = new JButton("Checkout");
        JButton btnHistory = new JButton("Lihat Riwayat Transaksi");
        JButton btnLogout = new JButton("Logout");
        
        btnLogout.setBackground(new Color(255, 100, 100));
        btnLogout.setForeground(Color.BLACK); 

        JPanel menu = new JPanel(new GridLayout(5, 1, 8, 8)); 
        menu.setOpaque(false);
        menu.add(btnView);
        menu.add(btnCart);
        menu.add(btnCheckout);
        menu.add(btnHistory);
        menu.add(btnLogout);
        
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(menu, BorderLayout.CENTER);

        // DAFTAR PRODUK 
        productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        productButtonPanel.setBackground(new Color(245, 245, 245)); 
        productButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        cartButtonPanel.setBackground(new Color(245, 245, 245)); 
        cartButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        JScrollPane productScrollView = new JScrollPane(productButtonPanel);
    
        productScrollView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        productScrollView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
        productScrollView.getViewport().setBackground(productButtonPanel.getBackground());
        
        rightContentWrapper = new JPanel(new BorderLayout());
        rightContentWrapper.add(productScrollView, BorderLayout.CENTER);
        
        refreshProductButtons(); 
        
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        
        labelInfo = new JLabel("Daftar Produk: (Klik 'Detail' untuk melihat info & beli)"); 
        labelInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        right.add(labelInfo, BorderLayout.NORTH);
        right.add(rightContentWrapper, BorderLayout.CENTER); 
        

        setLayout(new BorderLayout(10, 10));
        add(title, BorderLayout.NORTH);
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);


        // EVENT LISTENERS
        btnView.addActionListener(e -> {
            refreshProductButtons(); 
            showProductView(); 
        });
        
        btnCart.addActionListener(e -> showCartView());
        btnHistory.addActionListener(e -> showHistoryView()); 
        btnCheckout.addActionListener(e -> showCheckoutView());

        // OBJEK CUSTOMER (DAN KERANJANGNYA) TETAP TERSIMPAN DI MEMORY
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Anda yakin ingin logout?", 
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Asumsi kelas WelcomeWindow ada dan memiliki konstruktor ini
                new WelcomeWindow(authService, produkService, transactionService).setVisible(true);
            }
        });
    }

    // METODE: MENAMPILKAN VIEW PRODUK
    private void showProductView() {
        rightContentWrapper.removeAll();
        
        JScrollPane productScrollView = new JScrollPane(productButtonPanel);
        
        productScrollView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        productScrollView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); 
        productScrollView.getViewport().setBackground(productButtonPanel.getBackground());
        
        rightContentWrapper.add(productScrollView, BorderLayout.CENTER);
        
        labelInfo.setText("Daftar Produk: (Klik 'Detail' untuk melihat info & beli)");
        
        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    // METODE: MENAMPILKAN VIEW KERANJANG
    private void showCartView() {
        Keranjang cart = customer.getCart();
        
        rightContentWrapper.removeAll();
        labelInfo.setText("Keranjang Belanja:");
        
        JPanel cartDisplayPanel = new JPanel(new BorderLayout(10, 10));
        cartDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (cart.getItems().isEmpty()) {
            JLabel emptyLabel = new JLabel("Keranjang belanja Anda kosong.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);
            centerWrapper.add(emptyLabel);
            cartDisplayPanel.add(centerWrapper, BorderLayout.CENTER);
            
        } else {
            refreshCartButtons(); 
            
            JScrollPane cartScrollPane = new JScrollPane(cartButtonPanel);
            
            cartScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            cartScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            cartScrollPane.getViewport().setBackground(cartButtonPanel.getBackground());
            
            JPanel bottomPanel = new JPanel(new BorderLayout(0, 5));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            JLabel totalLabel = new JLabel(String.format("TOTAL HARGA: %s", IDR_FORMAT.format(cart.totalHarga())), SwingConstants.RIGHT);
            totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            
            JButton btnClearCart = new JButton("Hapus Semua Item dari Keranjang");
            
            bottomPanel.add(totalLabel, BorderLayout.NORTH);
            bottomPanel.add(btnClearCart, BorderLayout.SOUTH);
            
            cartDisplayPanel.add(cartScrollPane, BorderLayout.CENTER);
            cartDisplayPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            btnClearCart.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(this, 
                    "Yakin ingin menghapus semua item dari keranjang?",
                    "Konfirmasi Hapus Semua", JOptionPane.YES_NO_OPTION);
                    
                if (confirmation == JOptionPane.YES_OPTION) {
                    cart.getItems().clear(); 
                    showCartView(); 
                    refreshProductButtons(); 
                    JOptionPane.showMessageDialog(this, "Semua item berhasil dihapus dari keranjang.");
                }
            });
        }
        
        rightContentWrapper.add(cartDisplayPanel, BorderLayout.CENTER);

        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    // METODE: REFRESH TOMBOL KERANJANG
    private void refreshCartButtons() {
        cartButtonPanel.removeAll(); 
        
        final int BOX_SIZE = 180; 
        Keranjang cart = customer.getCart();
        List<CartItem> items = cart.getItems();
        
        for (int i = 0; i < items.size(); i++) {
            CartItem ci = items.get(i);
            
            JPanel cartItemBox = new JPanel();
            cartItemBox.setLayout(new BoxLayout(cartItemBox, BoxLayout.Y_AXIS)); 
            
            cartItemBox.setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            cartItemBox.setMaximumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            cartItemBox.setMinimumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            
            cartItemBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 255), 2), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5) 
            ));
            cartItemBox.setBackground(new Color(230, 240, 255)); 
            
            JLabel nameLabel = new JLabel(ci.getProduk().getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel qtyLabel = new JLabel("Jumlah: x" + ci.getJumlah(), SwingConstants.CENTER);
            qtyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            qtyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel subtotalLabel = new JLabel("Subtotal:", SwingConstants.CENTER);
            subtotalLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            subtotalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel priceLabel = new JLabel(IDR_FORMAT.format(ci.getSubtotal()), SwingConstants.CENTER);
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            priceLabel.setForeground(new Color(0, 100, 0)); 
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton btnRemove = new JButton("Hapus Item");
            btnRemove.setFont(new Font("SansSerif", Font.BOLD, 10));
            btnRemove.setBackground(new Color(255, 180, 180));
            btnRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnRemove.setMaximumSize(new Dimension(BOX_SIZE - 10, 25)); 
            
            btnRemove.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(
                    this, 
                    "Yakin ingin menghapus " + ci.getProduk().getName() + " x" + ci.getJumlah() + " dari keranjang?",
                    "Konfirmasi Hapus Item", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirmation == JOptionPane.YES_OPTION) {
                    cart.getItems().remove(ci); 
                    showCartView(); 
                    refreshProductButtons(); 
                    JOptionPane.showMessageDialog(this, "Item berhasil dihapus.");
                }
            });

            cartItemBox.add(Box.createRigidArea(new Dimension(0, 5)));
            cartItemBox.add(nameLabel);
            cartItemBox.add(qtyLabel);
            cartItemBox.add(Box.createVerticalGlue()); 
            cartItemBox.add(subtotalLabel);
            cartItemBox.add(priceLabel);
            cartItemBox.add(Box.createRigidArea(new Dimension(0, 10)));
            cartItemBox.add(btnRemove); 
            cartItemBox.add(Box.createRigidArea(new Dimension(0, 5)));

            cartButtonPanel.add(cartItemBox);
        }
        
        cartButtonPanel.revalidate(); 
        cartButtonPanel.repaint(); 
    }
    
    // METODE: MENAMPILKAN VIEW CHECKOUT
    private void showCheckoutView() {
        Keranjang cart = customer.getCart();
        
        rightContentWrapper.removeAll();
        labelInfo.setText("Pilih Item yang Akan Di-Checkout:");

        if (cart.getItems().isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout()); 
            JLabel emptyLabel = new JLabel("Keranjang kosong. Tidak bisa Checkout.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);
            centerWrapper.add(emptyLabel);
            
            rightContentWrapper.add(centerWrapper, BorderLayout.CENTER);
            
            rightContentWrapper.revalidate();
            rightContentWrapper.repaint();
            return; 
        }
        
        //Panel Utama Checkout
        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Detail Item dan Total
        // List untuk melacak checkbox agar total bisa dihitung
        List<JCheckBox> itemCheckboxes = new ArrayList<>();
        List<CartItem> cartItems = cart.getItems();

        JPanel itemSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        itemSelectionPanel.setBackground(new Color(245, 245, 245));
        
        // Hitung total awal
        double initialTotal = 0;

        for (CartItem ci : cartItems) {
            JPanel box = createCheckoutBox(ci);
            
            // Cari checkbox di dalam box yang baru dibuat
            JCheckBox cb = (JCheckBox) ((JPanel) box.getComponent(0)).getComponent(0);
            itemCheckboxes.add(cb);
            
            itemSelectionPanel.add(box);
            initialTotal += ci.getSubtotal();
        }

        JScrollPane itemScrollPane = new JScrollPane(itemSelectionPanel);
        itemScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        itemScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemScrollPane.getViewport().setBackground(itemSelectionPanel.getBackground());

        JLabel totalLabel = new JLabel(String.format("TOTAL HARGA: %s", IDR_FORMAT.format(initialTotal)), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        // Listener untuk memperbarui total saat checkbox dicentang/tidak
        for (JCheckBox cb : itemCheckboxes) {
            cb.addActionListener(e -> {
                double newTotal = 0;
                for (JCheckBox currentCb : itemCheckboxes) {
                    if (currentCb.isSelected()) {
                        CartItem ci = (CartItem) currentCb.getClientProperty("cartItem");
                        newTotal += ci.getSubtotal();
                    }
                }
                totalLabel.setText(String.format("TOTAL HARGA: %s", IDR_FORMAT.format(newTotal)));
            });
        }
        
        JPanel detailWrapper = new JPanel(new BorderLayout());
        detailWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        detailWrapper.add(itemScrollPane, BorderLayout.CENTER);
        
        
        //Panel Metode Pembayaran
        JPanel paymentPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Pilih Metode Pembayaran"));
        
        JRadioButton rbQris = new JRadioButton("QRIS");
        JRadioButton rbCod = new JRadioButton("COD (Cash On Delivery - " + customer.getProfile().getAddress() + ")");
        JRadioButton rbTransfer = new JRadioButton("Transfer Bank");
        
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(rbQris);
        paymentGroup.add(rbCod);
        paymentGroup.add(rbTransfer);
        rbQris.setSelected(true); 
        
        paymentPanel.add(rbQris);
        paymentPanel.add(rbCod);
        paymentPanel.add(rbTransfer);
        
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(paymentPanel, BorderLayout.NORTH);
        topPanel.add(detailWrapper, BorderLayout.CENTER);

        //Tombol Konfirmasi Checkout
        JButton btnConfirmCheckout = new JButton("Konfirmasi dan Bayar");
        btnConfirmCheckout.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JPanel bottomWrapper = new JPanel(new BorderLayout(0, 10));
        bottomWrapper.add(totalLabel, BorderLayout.NORTH);
        bottomWrapper.add(btnConfirmCheckout, BorderLayout.SOUTH);
        
        // Menyusun Panel
        checkoutPanel.add(topPanel, BorderLayout.CENTER); 
        checkoutPanel.add(bottomWrapper, BorderLayout.SOUTH);
        
        rightContentWrapper.add(checkoutPanel, BorderLayout.CENTER);
        
        //Aksi Konfirmasi
        btnConfirmCheckout.addActionListener(e -> {
            
            //Ambil item yang dicentang
            List<CartItem> selectedItems = new ArrayList<>();
            for (JCheckBox cb : itemCheckboxes) {
                if (cb.isSelected()) {
                    selectedItems.add((CartItem) cb.getClientProperty("cartItem"));
                }
            }
            
            if (selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih minimal satu item untuk di-checkout!");
                return;
            }
            
            //Buat Keranjang/Transaksi sementara dari item terpilih
            Keranjang checkoutCart = new Keranjang(); 
            checkoutCart.getItems().addAll(selectedItems); // Asumsi Keranjang memiliki getter/setter items
            
            Pembayaran pembayaran = null;
            
            try {
                if (rbQris.isSelected()) {
                    pembayaran = new QRISPayment();
                } else if (rbCod.isSelected()) {
                    // Asumsi CODPayment menerima alamat
                    pembayaran = new CODPayment(customer.getProfile().getAddress());
                } else if (rbTransfer.isSelected()) {
                    Object[] banks = {"BSI", "MANDIRI", "ACEH"};
                    int b = JOptionPane.showOptionDialog(this, "Pilih bank:", "Transfer Bank",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, banks, banks[0]);
                    
                    // Asumsi class BankBSI, BankMandiri, BankAceh ada
                    if (b == 0) pembayaran = new BankBSI();
                    else if (b == 1) pembayaran = new BankMandiri();
                    else if (b == 2) pembayaran = new BankAceh();
                    else return; 
                } else {
                    JOptionPane.showMessageDialog(this, "Pilih metode pembayaran terlebih dahulu!");
                    return;
                }
                
                Transaksi t = transactionService.createTransaksi(customer, checkoutCart, pembayaran);
                JOptionPane.showMessageDialog(this, "Checkout sukses! ID: " + t.getId());
                
                //HAPUS item yang sudah dibeli dari keranjang customer yang asli
                customer.getCart().getItems().removeAll(selectedItems);
                
                showCartView(); // Refresh tampilan keranjang
                refreshProductButtons(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Checkout gagal: " + ex.getMessage());
            }
        });

        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }
    
    // METODE BARU: MEMBUAT KOTAK CHECKOUT ITEM
    private JPanel createCheckoutBox(CartItem ci) {
        final int BOX_SIZE_WIDTH = 250; 
        final int BOX_SIZE_HEIGHT = 120; 

        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(BOX_SIZE_WIDTH, BOX_SIZE_HEIGHT)); 
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 150), 2), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5) 
        ));
        box.setBackground(Color.WHITE); 
        
        // Checkbox di bagian Utara (NORTH)
        JCheckBox cb = new JCheckBox("", true); // Default dicentang
        cb.putClientProperty("cartItem", ci); // Simpan objek CartItem di properti komponen

        JPanel cbPanel = new JPanel(new BorderLayout());
        cbPanel.setOpaque(false);
        cbPanel.add(cb, BorderLayout.WEST);
        
        // Detail di bagian Tengah (CENTER)
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        JLabel nameLabel = new JLabel(ci.getProduk().getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel qtyLabel = new JLabel("Jumlah: x" + ci.getJumlah());
        qtyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(IDR_FORMAT.format(ci.getSubtotal()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 120, 0)); 
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailPanel.add(nameLabel);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailPanel.add(qtyLabel);
        detailPanel.add(Box.createVerticalGlue());
        detailPanel.add(new JLabel("Subtotal:"));
        detailPanel.add(priceLabel);
        
        box.add(cbPanel, BorderLayout.NORTH);
        box.add(detailPanel, BorderLayout.CENTER);
        
        return box;
    }
    
    // METODE: MENAMPILKAN VIEW RIWAYAT TRANSAKSI (TABEL)
    private void showHistoryView() {
        rightContentWrapper.removeAll();
        
        List<Transaksi> hist = transactionService.getHistory(customer);
        
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(new Color(255, 255, 255));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        labelInfo.setText("Riwayat Transaksi:");
        
        if (hist.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada riwayat transaksi.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);
            centerWrapper.add(emptyLabel);
            historyPanel.add(centerWrapper, BorderLayout.CENTER);
            
        } else {
            //Setup Model Tabel
            String[] columnNames = {"ID Transaksi", "Total Harga", "Tgl Transaksi", "Status"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Tidak bisa diedit
                }
            };
            
            //Isi Data ke Model
            for (Transaksi t : hist) {
                String status = t.isAccepted() ? "DITERIMA" : "PENDING";
                
                model.addRow(new Object[]{
                    t.getId(), 
                    IDR_FORMAT.format(t.getTotal()), 
                    String.valueOf(t.getCreatedAt()), 
                    status
                });
            }
            
            //Buat JTable
            JTable historyTable = new JTable(model);
            historyTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
            historyTable.setRowHeight(25);
            historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
            historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            //Renderer untuk pewarnaan Status (Rata Tengah)
            historyTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(CENTER);

                    if (value instanceof String) {
                        String status = (String) value;
                        if (status.equals("DITERIMA")) {
                            c.setForeground(new Color(0, 150, 0)); // Hijau
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (status.equals("PENDING")) {
                            c.setForeground(Color.ORANGE.darker()); // Jingga Gelap
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    }
                    if (isSelected) {
                        c.setBackground(new Color(255, 238, 240));
                        c.setForeground(Color.BLACK); 
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    }
                    return c;
                }
            });

            // Renderer untuk Total Harga
            historyTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(RIGHT);
                    return c;
                }
            });
            
            // Pengaturan Lebar Kolom
            historyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            historyTable.getColumnModel().getColumn(2).setPreferredWidth(150);

            JScrollPane historyScrollPane = new JScrollPane(historyTable);
            historyPanel.add(historyScrollPane, BorderLayout.CENTER);
            historyTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) { // Double click
                        int row = historyTable.getSelectedRow();
                        if (row != -1) {
                            String id = (String) historyTable.getValueAt(row, 0);
                            Transaksi t = hist.stream().filter(trans -> trans.getId().equals(id)).findFirst().orElse(null);
                            
                            if (t != null) {
                                showTransactionDetailDialog(t);
                            }
                        }
                    }
                }
            });
        }
        
        rightContentWrapper.add(historyPanel, BorderLayout.CENTER);

        rightContentWrapper.revalidate();
        rightContentWrapper.repaint();
    }

    // DIALOG UNTUK DETAIL TRANSAKSI
    private void showTransactionDetailDialog(Transaksi t) {
        JDialog detailDialog = new JDialog(this, "Detail Transaksi: " + t.getId(), true);
        detailDialog.setLayout(new BorderLayout(10, 10));
        detailDialog.setSize(400, 450);
        detailDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header Info
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        infoPanel.add(new JLabel("ID Transaksi:"));
        infoPanel.add(new JLabel(t.getId()));
        infoPanel.add(new JLabel("Tanggal:"));
        infoPanel.add(new JLabel(String.valueOf(t.getCreatedAt()))); 
        infoPanel.add(new JLabel("Status:"));
        
        JLabel statusLabel = new JLabel(t.isAccepted() ? "DITERIMA" : "PENDING");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        statusLabel.setForeground(t.isAccepted() ? new Color(0, 150, 0) : Color.ORANGE.darker());
        infoPanel.add(statusLabel);

        // Detail Item
        String[] columns = {"Produk", "Qty", "Harga"};
        DefaultTableModel itemModel = new DefaultTableModel(columns, 0);
        
        for (CartItem ci : t.getItems()) {
            itemModel.addRow(new Object[]{
                ci.getProduk().getName(),
                ci.getJumlah(),
                IDR_FORMAT.format(ci.getSubtotal())
            });
        }
        
        JTable itemTable = new JTable(itemModel);
        itemTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        
        // Total Harga
        JLabel totalLabel = new JLabel("TOTAL: " + IDR_FORMAT.format(t.getTotal()), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        contentPanel.add(infoPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        contentPanel.add(totalLabel, BorderLayout.SOUTH);
        
        detailDialog.add(contentPanel, BorderLayout.CENTER);
        detailDialog.setVisible(true);
    }
    
    // REFRESH TOMBOL PRODUK (GRID/PETAK) 
    private void refreshProductButtons() {
        productButtonPanel.removeAll(); 
        
        final int BOX_SIZE = 150; 

        List<Produk> produkList = produkService.getAll();
        Keranjang cart = customer.getCart();
        
        for (Produk p : produkList) {
            
            JPanel productBox = new JPanel();
            productBox.setLayout(new BoxLayout(productBox, BoxLayout.Y_AXIS)); 
            
            productBox.setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            productBox.setMaximumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            productBox.setMinimumSize(new Dimension(BOX_SIZE, BOX_SIZE)); 
            
            productBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5) 
            ));
            productBox.setBackground(Color.WHITE); 
            
            JLabel imagePlaceholder = new JLabel(" RASA.IN ", SwingConstants.CENTER);
            imagePlaceholder.setMinimumSize(new Dimension(BOX_SIZE, BOX_SIZE / 2));
            imagePlaceholder.setPreferredSize(new Dimension(BOX_SIZE, BOX_SIZE / 2)); 
            imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePlaceholder.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); 

            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel detailLabel = new JLabel(IDR_FORMAT.format(p.getPrice()) + " | Stok: " + p.getStok(), SwingConstants.CENTER);
            detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton btnDetail = new JButton("Lihat Detail");
            btnDetail.setFont(new Font("SansSerif", Font.BOLD, 10));
            btnDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnDetail.setMaximumSize(new Dimension(BOX_SIZE - 10, 25)); 

            if (p.getStok() <= 0) {
                btnDetail.setText("HABIS");
                btnDetail.setEnabled(false);
            }
            
            productBox.add(imagePlaceholder);
            productBox.add(nameLabel);
            productBox.add(detailLabel);
            productBox.add(Box.createVerticalGlue()); 
            productBox.add(btnDetail); 
            productBox.add(Box.createRigidArea(new Dimension(0, 5))); 

            btnDetail.addActionListener(e -> {
                showProductDetailDialog(p, cart);
            });
            
            productButtonPanel.add(productBox);
        }
        
        productButtonPanel.revalidate(); 
        productButtonPanel.repaint(); 
    }
    
    // DIALOG UNTUK DETAIL PRODUK & BELI 
    private void showProductDetailDialog(Produk p, Keranjang cart) {
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        JLabel header = new JLabel("Detail Produk: " + p.getName());
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(header, gbc);
        
        gbc.gridy = row++; contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = row++; contentPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; contentPanel.add(new JLabel(p.getId()), gbc);

        gbc.gridx = 0; gbc.gridy = row++; contentPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; contentPanel.add(new JLabel(p.getDeskripsi()), gbc);

        gbc.gridx = 0; gbc.gridy = row++; contentPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1; contentPanel.add(new JLabel(IDR_FORMAT.format(p.getPrice())), gbc);

        gbc.gridx = 0; gbc.gridy = row++; contentPanel.add(new JLabel("Stok Tersedia:"), gbc);
        JLabel stokLabel = new JLabel(String.valueOf(p.getStok()));
        if (p.getStok() <= 5 && p.getStok() > 0) {
            stokLabel.setForeground(Color.RED);
            stokLabel.setText(p.getStok() + " (Stok Hampir Habis!)");
        } else if (p.getStok() <= 0) {
             stokLabel.setForeground(Color.GRAY);
             stokLabel.setText("HABIS");
        }
        gbc.gridx = 1; contentPanel.add(stokLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        
        JTextField qtyField = new JTextField("1", 5);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.add(new JLabel("Jumlah Beli:"));
        inputPanel.add(qtyField);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        contentPanel.add(inputPanel, gbc);
        
        JButton btnBuyNow = new JButton("Tambah ke Keranjang");
        btnBuyNow.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        if (p.getStok() <= 0) {
            btnBuyNow.setText("Stok Habis");
            btnBuyNow.setEnabled(false);
            qtyField.setEnabled(false);
        }
        
        JDialog detailDialog = new JDialog(this, "Detail Produk: " + p.getName(), true);
        detailDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(contentPanel);
        
        wrapper.add(centerWrapper, BorderLayout.CENTER);
        wrapper.add(btnBuyNow, BorderLayout.SOUTH);
        
        detailDialog.add(wrapper, BorderLayout.CENTER);

        btnBuyNow.addActionListener(event -> {
            try {
                int qty = Integer.parseInt(qtyField.getText().trim());
                if (qty <= 0) throw new NumberFormatException();
                
                if (qty > p.getStok()) {
                     JOptionPane.showMessageDialog(
                        detailDialog,
                        "Jumlah pembelian melebihi stok yang tersedia (" + p.getStok() + ")!"
                    );
                    return; 
                }
                
                // Asumsi metode addProduk(p, qty) ada di Keranjang
                cart.addProduk(p, qty); 
                JOptionPane.showMessageDialog(
                        detailDialog,
                        p.getName() + " x" + qty + " berhasil ditambahkan ke keranjang!"
                );
                
                refreshProductButtons();
                detailDialog.dispose(); 
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        detailDialog,
                        "Jumlah harus berupa angka positif!"
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        detailDialog,
                        "Gagal tambah: " + ex.getMessage()
                );
            }
        });
        
        detailDialog.setSize(450, 400); 
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }
    
 

}
