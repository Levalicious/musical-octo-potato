package core.types.transaction;

public class TransactionInput {
    private String txOutputHash;
    private TransactionOutput UTXO;

    public TransactionInput(String txOutputHash) {
        this.txOutputHash = txOutputHash;
    }

    public String getParentHash() {
        return txOutputHash;
    }

    public void setUTXO(TransactionOutput in) {
        this.UTXO = in;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public String toString() {
        return "{" + txOutputHash + "," + UTXO.toString() + "}";
    }
}
