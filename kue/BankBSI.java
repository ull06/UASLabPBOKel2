package kue;

public class BankBSI extends TransferBank {
    @Override
    public String getBankName() {
        return "BSI";
    }
}