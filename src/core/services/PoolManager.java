package core.services;

import core.types.pools.LeafPool;
import core.types.pools.TxPool;
import core.types.pools.UTXOPool;
import core.types.transaction.Transaction;
import core.types.tree.*;
import user.Wallet;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class PoolManager implements TxPool, UTXOPool, LeafPool, Serializable {
    public static void clearPool() {
        TxPool.clear();
    }

    public static Transaction getTx(String hash) {
        for(TreeNode<Transaction> node : leaves.values()) {
            for(Transaction tx : node.getValues()) {
                if(tx.grabHash().equals(hash)) return tx;
            }
        }

        return null;
    }

    public static void fillPool(long txNum) throws UnsupportedEncodingException {
        Wallet wallet = new Wallet();
        for(long i = 0; i < txNum; i++) {
            String print = "[";
            for(int j = 0; j < ((((double)(i)/(double)txNum)) * (double)50) - 1; j++) {
                print = print + "=";
            }
            print = print + ">";

            String temp = String.format("%-51s", print);
            temp = temp + "]  |  ";

            System.out.print("\r");
            System.out.print(temp);
            System.out.printf("%.2f",(((double)(i + 1)/(double)txNum)) * (double)100);
            wallet.newWallet();
            Transaction tx = new Transaction(wallet.getPubAddress(), "1348rnwexLA8ifeEaD78uhWCHVeV4FWXWTtayFD1wTMHKxGgfmYSM3rXLHUx4PRLkVBRbjC1pPo3yujzNNkzzK1q5", "10", null, wallet.getPrivKey());
            TxPool.put(tx.calcHash(), tx);
        }
    }

    public static void writePool(String fileName) {
        try {
            FileOutputStream fout = new FileOutputStream((fileName + ".ser"));
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(TxPool);
            out.close();
            fout.close();
            System.out.println("TxPool saved!");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static void readPool(String fileName) {
        try {
            FileInputStream fin = new FileInputStream(fileName + ".ser");
            ObjectInputStream in = new ObjectInputStream(fin);
            ConcurrentHashMap<String, Transaction> temp = ((ConcurrentHashMap<String, Transaction>) in.readObject());
            TxPool.putAll(temp);
            temp = null;
            in.close();
            fin.close();
        }catch(Exception c) {
            System.out.println("TxPool file not found.");
            c.printStackTrace();
        }
    }

    public static int getPoolSize() {
        return TxPool.size();
    }

    public static void groupPool(TxTree tree) {
        for(ConcurrentHashMap.Entry tx : TxPool.entrySet()) {
            tree.add((Transaction)tx.getValue());
        }
        clearPool();
    }

    public static void trimPool(int x) {
        TxPool.entrySet().removeIf(e-> (TxPool.size() > x));
    }

    public static void addNode(TreeNode node) {
        if(leaves.containsKey(node.getPrefix())) {
            System.out.println("Warning! Overwriting existing node in TreePool!");
            leaves.replace(node.getPrefix(), node);
        }else {
            leaves.put(node.getPrefix(), node);
        }
    }

    public static void removeNode(String prefix) {
        leaves.remove(prefix);
    }
}