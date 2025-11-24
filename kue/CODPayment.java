package kue;

import java.util.Map;

public class CODPayment extends Pembayaran {

    private String alamatPengiriman;

    public CODPayment(String alamatPengiriman) {
        this.alamatPengiriman = alamatPengiriman;
    }

    @Override
    public PaymentResult pay(double totalBayar, Map<String, String> info) {
        String msg = "COD - Barang akan dikirim ke alamat: "
                + alamatPengiriman
                + ". Menunggu admin konfirmasi.";
        return new PaymentResult(true, msg);
    }

    @Override
    public String getMethodName() {
        return "COD";
    }
}
