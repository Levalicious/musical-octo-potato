package core.types.pools;

import core.types.transaction.Transaction;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static util.Hex.fromHex;

public class TxPool {
    public static ConcurrentHashMap<byte[], Transaction> TxPool = new ConcurrentHashMap<byte[], Transaction>();

    public static void clear() {
        TxPool = null;
        TxPool = new ConcurrentHashMap<byte[], Transaction>();
    }

    public static void put(Transaction tx) {
        TxPool.put(fromHex(tx.grabHash()), tx);
    }

    public static void putAll(ConcurrentHashMap<byte[], Transaction> txList) {
        TxPool.putAll(txList);
    }

    public static Set<Map.Entry<byte[], Transaction>> entrySet() {
        return TxPool.entrySet();
    }

    public static int size() {
        return TxPool.size();
    }

    public static ArrayList<Transaction> getTxs() {
        ArrayList<Transaction> temp = new ArrayList<>();
        for(Map.Entry<byte[], Transaction> tx : TxPool.entrySet()) {
            temp.add(tx.getValue());
            TxPool.remove(tx.getKey());
        }

        return temp;
    }
}
