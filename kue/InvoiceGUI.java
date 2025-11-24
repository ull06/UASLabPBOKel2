package kue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class InvoiceGUI extends JDialog { 

    private static final NumberFormat IDR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    private final Color palePink = new Color(255, 240, 245); 
    private final Color darkBrown = new Color(101, 67, 33);

    public InvoiceGUI(Frame owner, Invoice invoice) { 
        super(owner, "INVOICE - " + invoice.getTransaksi().getId(), true);
        
        getContentPane().setBackground(palePink);
        setLayout(new BorderLayout(10, 10)); 

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(palePink);
        
        // 1. Judul
        JLabel title = new JLabel("INVOICE PEMBELIAN", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(darkBrown);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // 2. Panel Header (Detail Transaksi)
        mainPanel.add(createHeaderPanel(invoice));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Label Detail Produk
        JLabel productLabel = new JLabel("Detail Produk:");
        productLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(productLabel);
        
        // 4. Tabel Items
        mainPanel.add(createItemsTable(invoice));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 5. Panel Summary (Total dan Metode Pembayaran)
        mainPanel.add(createSummaryPanel(invoice));
        
        // 6. Tombol OK
        JButton btnOk = new JButton("OK");
        btnOk.setBackground(Color.WHITE);
        btnOk.setForeground(darkBrown);
        btnOk.addActionListener(e -> dispose());
        
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setBackground(palePink);
        buttonWrapper.add(btnOk);
        
        mainPanel.add(buttonWrapper); 
        
        add(mainPanel, BorderLayout.CENTER);
        pack(); 
        setSize(480, getHeight()); 
        setLocationRelativeTo(owner);
    }
    
    private JPanel createHeaderPanel(Invoice invoice) {
        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        headerPanel.setBackground(palePink);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        headerPanel.add(new JLabel("Tanggal:"));
        headerPanel.add(new JLabel(invoice.getTanggal().format(formatter)));

        headerPanel.add(new JLabel("ID Transaksi:"));
        headerPanel.add(new JLabel(invoice.getTransaksi().getId()));

        headerPanel.add(new JLabel("Customer:"));
        headerPanel.add(new JLabel(invoice.getTransaksi().getCustomer().getFullName()));

        return headerPanel;
    }

    private JScrollPane createItemsTable(Invoice invoice) {
        String[] columnNames = {"Produk", "Jumlah", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (CartItem item : invoice.getTransaksi().getItems()) {
            model.addRow(new Object[]{
                    item.getProduk().getName(),
                    item.getJumlah(),
                    IDR_FORMAT.format(item.getSubtotal())
            });
        }

        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(450, 150));
        table.setFillsViewportHeight(true);

        return new JScrollPane(table);
    }

    private JPanel createSummaryPanel(Invoice invoice) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        summaryPanel.setBackground(palePink);

        Font boldFont = new Font("SansSerif", Font.BOLD, 14);

        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(boldFont);
        summaryPanel.add(totalLabel);

        JLabel totalValue = new JLabel(IDR_FORMAT.format(invoice.getTransaksi().getTotal()));
        totalValue.setFont(boldFont);
        totalValue.setHorizontalAlignment(SwingConstants.RIGHT); 
        summaryPanel.add(totalValue);

        summaryPanel.add(new JLabel("Metode Pembayaran:"));
        summaryPanel.add(new JLabel(invoice.getPembayaran().getMethodName()));

        return summaryPanel;
    }
}