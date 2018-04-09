package dev;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.stream.IntStream;

import static crypto.hash.Hash.*;

public class BloomFilter {
    private BitSet filter;
    private long m;
    private long k;

    public BloomFilter(double n) {
        //One false positive every p items
        double p = 1.0 / 15000.0;
        m = (long) Math.ceil(-1.0 * (n * Math.log(p)) / Math.pow(Math.log(2.0), 2.0));
        k = (long) Math.ceil((Math.log(2.0) * ((double)m)) / n);
        m = (long)Math.ceil((k * n) / Math.log(2.0));
        filter = new BitSet((int)(m));

        // System.out.println();
        // System.out.println("Initialized filter with " + m + " positions and " + k + " hash functions.");
    }

    public int getSize() {
        return filter.size();
    }

    public void add(String in) {
        for(long i = 0; i < k; i++) {
            filter.set((int)(getHash(in, i)), true);
        }
    }

    public boolean check(String in) {
        for(long i = 0; i < k; i++) {
            if(!filter.get((int)getHash(in, i))) {
                return false;
            }
        }

        return true;
    }

    private long getHash(String in, long i) {
        long hash1 = murmur64(in);
        long hash2 = new BigInteger(blake256(in + Long.toString(i)), 16).longValue();

        long hashi = (hash1 + hash2 * i + (int)Math.pow(i, 2)) % m;

        if(hashi < 0) {
            hashi = hashi * -1;
        }

        return hashi;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder((int)m);
        IntStream.range(0, (int)m).mapToObj(i -> filter.get(i) ? '1' : '0').forEach(buffer::append);
        return buffer.toString();
    }
}
