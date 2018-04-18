package core.types.pools;

import core.types.transaction.TransactionOutput;

import java.util.concurrent.ConcurrentHashMap;

public interface UTXOPool {
    ConcurrentHashMap<byte[], TransactionOutput> UTXOPool = new ConcurrentHashMap<>();
}
