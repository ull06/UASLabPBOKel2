package kue;

import java.time.LocalDateTime;
import java.util.Map;

//Template dasar untk metode pembayarn
public abstract class Pembayaran {

    protected Boolean status; // apakah sudah lunas
    protected String idPembayaran;
    protected LocalDateTime tanggal; // kapan pembayaran dilakukan

    public Pembayaran() {
        this.status = false; // awalnya belum lunas
        this.tanggal = LocalDateTime.now();
    }

    // method abstrak → semua turunan wajib implement
    public abstract PaymentResult pay(double totalBayar, Map<String, String> info)
            throws PaymentException;

    public abstract String getMethodName();

    public void setLunas() {
        this.status = true;
    }

    public Boolean getLunas() {
        return status;
    }  
}
