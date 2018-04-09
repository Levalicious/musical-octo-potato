package core.types.block;

import java.util.ArrayList;

import static crypto.hash.Hash.getMerkleRoot;

public class Ancestors {
    private ArrayList<String> hashList;

    public Ancestors() {
        hashList = new ArrayList<String>();
    }

    public Ancestors(ArrayList<String> hashList) {
        this.hashList = hashList;
    }

    public String getRoot() {
        return getMerkleRoot(hashList);
    }

    public void add(String hash) {
        if (!hashList.contains(hash)) {
            hashList.add(hash);
        } else {
            System.out.println("Block hash already in hashlist.");
        }
    }
}
