package core.types.block;

import com.google.gson.GsonBuilder;
import core.types.transaction.Transaction;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Block {
    private Header header;
    private Payload payload;
    private Ancestors ancestors;

    /* Constructs empty block just because */
    public Block() {
        this.header = new Header();
        this.payload = new Payload();
        this.ancestors = new Ancestors();
    }

    public Block(Header header, Payload payload, Ancestors ancestors) {
        this.header = header;
        this.payload = payload;
        this.ancestors = ancestors;
    }

    public Block(long height, String prefix, String wif, Payload payload, Ancestors ancestors) {
        this.header = new Header(height, prefix, payload.getRoot(), payload.countFees(), ancestors.getRoot(), wif);
    }

    public boolean checkBlock() throws UnsupportedEncodingException {
        if(!header.check()) return false;

        if(!payload.check()) return false;

        return true;
    }

    public void processBlock() throws UnsupportedEncodingException {
        this.payload.process();
    }

    public void setHeader(long height, String prefix, String txRoot, String fees, String hashRoot, String key) {
        this.header = new Header(height, prefix, txRoot, fees, hashRoot, key);
    }

    public void setPayload(ArrayList<Transaction> txList) {
        this.payload = new Payload(txList);
    }

    public void setAncestors(ArrayList<String> ancestors) {
        this.ancestors = new Ancestors(ancestors);
    }

    public String getTxRoot() {
        return this.payload.getRoot();
    }

    public String getAncestorRoot() {
        return this.ancestors.getRoot();
    }

    public String getBlockHash() {
        return this.header.getBlockHash();
    }

    public String getPrefix() {
        return this.header.getPrefix();
    }

    public String getFees() {
        return this.payload.countFees();
    }
}