package core.types.block;

import java.io.Serializable;
import java.util.ArrayList;

import static crypto.hash.Hash.getMerkleRoot;
import static util.Hex.fromHex;

public class Ancestors implements Serializable {
    private ArrayList<byte[]> hashList;

    public Ancestors() {
        hashList = new ArrayList<byte[]>();
    }

    public Ancestors(ArrayList<byte[]> hashList) {
        this.hashList = hashList;
    }

    public byte[] getRoot() {
        return fromHex(getMerkleRoot(hashList));
    }

    public void add(byte[] hash) {
        if(!hashList.contains(hash)) {
            hashList.add(hash);
        } else {
            System.out.println("Block hash already in hashlist.");
        }
    }
}
