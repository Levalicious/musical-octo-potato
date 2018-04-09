package core.types.pools;

import core.types.transaction.Transaction;

import java.util.concurrent.ConcurrentHashMap;

public interface TxPool {
    ConcurrentHashMap<String, Transaction> TxPool = new ConcurrentHashMap<String, Transaction>();
}
