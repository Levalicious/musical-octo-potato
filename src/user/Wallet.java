package user;
import core.services.PoolManager;
import core.types.transaction.Transaction;
import core.types.transaction.TransactionInput;
import core.types.transaction.TransactionOutput;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static util.wallet.Sign.*;
import static util.wallet.Private.*;
import static util.wallet.Public.*;

public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet() {
        Security.addProvider(new BouncyCastleProvider());
        newWallet();
        refreshKeyPair();
    }

    private Wallet(String wif) {
        this.privateKey = stringToPrivateKey(wif);
        refreshKeyPair();
    }

    public static Wallet importWallet(String wif) {
        Security.addProvider(new BouncyCastleProvider());
        return new Wallet(wif);
    }

    @Override
    public String toString() {
        refreshKeyPair();
        String s = "+====+ Public Address +====+\n";
        s = s + publicKeyToAddress(this.publicKey) + "\n";
        s = s + "\n";
        s = s + "+====+ WIF +====+\n";
        s = s + privateKeyToString(this.privateKey) + "\n";
        s = s + "\n";
        return s;
    }

    public String getPubAddress() {
        refreshKeyPair();
        return publicKeyToAddress(this.publicKey);
    }

    public String getPubKey() {
        refreshKeyPair();
        return publicKeyToString(this.publicKey);
    }

    public String signMessage(String message) {
        return signString(privateKey,message);
    }


    public String getPrivKey() {
        return privateKeyToString(this.privateKey);
    }

    private void refreshKeyPair() {
        this.publicKey = privateKeyToPublicKey(this.privateKey);
    }

    public void newWallet() {
        this.privateKey = generateNewPrivateKey();
    }

    public String getBalance() {
        BigInteger temp = BigInteger.ZERO;

        for(Map.Entry<String,TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput utxo = item.getValue();

            if(utxo.checkOwner(getPubKey())) {
                UTXOs.put(utxo.getHash(), utxo);
                temp.add(utxo.getValue());
            }
        }

        return temp.toString();
    }

    public Transaction genTx(String recipient, String value) throws UnsupportedEncodingException {
        /*
        if(Integer.valueOf(getBalance()) < Integer.valueOf(value)) {
            System.out.println("Not enough funds to send transaction.");
            return null;
        }
        */

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        BigInteger total = BigInteger.ZERO;

        for(Map.Entry<String,TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput utxo = item.getValue();
            total.add(utxo.getValue());
            inputs.add(new TransactionInput(utxo.getHash()));
        }

        Transaction newTx = new Transaction(getPubKey(), recipient, value, inputs, getPrivKey());

        for(TransactionInput input : inputs) {
            UTXOs.remove(input.getParentHash());
        }

        return newTx;
    }
}
