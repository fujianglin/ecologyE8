package weaver.interfaces.sapsyn.bean;
/*- 供应商银行账号信息 -*/
public class SupplierBanks {
    private String BankAccount;
    private String BankAccountReferenceText;
    private String BankAccountHolderName;
    private String BankIdentification;
    private String BankName;

    public String getBankAccount() {
        return BankAccount;
    }

    public void setBankAccount(String bankAccount) {
        BankAccount = bankAccount;
    }

    public String getBankAccountReferenceText() {
        return BankAccountReferenceText;
    }

    public void setBankAccountReferenceText(String bankAccountReferenceText) {
        BankAccountReferenceText = bankAccountReferenceText;
    }

    public String getBankAccountHolderName() {
        return BankAccountHolderName;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        BankAccountHolderName = bankAccountHolderName;
    }

    public String getBankIdentification() {
        return BankIdentification;
    }

    public void setBankIdentification(String bankIdentification) {
        BankIdentification = bankIdentification;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    @Override
    public String toString() {
        return "SupplierBanks{" +
                "BankAccount='" + BankAccount + '\'' +
                ", BankAccountReferenceText='" + BankAccountReferenceText + '\'' +
                ", BankAccountHolderName='" + BankAccountHolderName + '\'' +
                ", BankIdentification='" + BankIdentification + '\'' +
                ", BankName='" + BankName + '\'' +
                '}';
    }
}
