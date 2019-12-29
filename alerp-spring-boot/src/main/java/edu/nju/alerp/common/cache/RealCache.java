package edu.nju.alerp.common.cache;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 缓存接口实现类，包括堆缓存和堆外缓存
 * @Author: yangguan
 * @CreateDate: 2019-12-24 14:28
 */
public class RealCache<K, V> implements Cache<K, V> {

    private long maxSize;

    private Map<K, V> heapCache;

    private Map<K, V> offHeapCache;

    private ExecutorService cachePool = new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000), new ThreadFactoryBuilder()
            .setNameFormat("async-cache-put" + "-%d").build());

    private boolean useHeap;

    private boolean useOffHeap;

    public RealCache(Map<K, V> heapCache, Map<K, V> offHeapCache, long maxHeapCacheSize){
        this.heapCache = heapCache;
        this.offHeapCache = offHeapCache;
        this.maxSize = maxHeapCacheSize;
        this.useHeap = !(heapCache == null);
        this.useOffHeap = !(offHeapCache == null);
    }

    @Override
    public int size() {
        if (useHeap)
            return heapCache.size();
        return 0;
    }

    @Override
    public V get(K key) {
        V v = null;
        if (useHeap) {
            v = heapCache.get(key);
        }
        if (v == null) {
            if (useOffHeap) {
                v = offHeapCache.get(key);
                if (useHeap && v != null && heapCache.size() < maxSize) {
                    heapCache.put(key, v);
                }
            }
        }
        return v;
    }

    @Override
    public void put(K key, V value) {
        if (useHeap) {
            heapCache.put(key, value);
        }
        if (useOffHeap) {
            cachePool.submit( () -> offHeapCache.put(key, value));
        }
    }

    @Override
    public void putAll(Map<K, V> m) {
        if (useHeap) {
            heapCache.putAll(m);
        }
        if (useOffHeap) {
            cachePool.submit(() -> offHeapCache.putAll(m));
        }
    }

    @Override
    public void remove(K key) {
        if (useHeap) {
            heapCache.remove(key);
        }

        if (useOffHeap) {
            cachePool.submit( () -> offHeapCache.remove(key));
        }
    }

    @Override
    public Map<K, V> getAll(Collection<K> ks) {
        return ks.parallelStream()
                .map(k -> new MutablePair<>(k, get(k)))
                .filter(pair -> pair.getRight() != null)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public Map<K, V> getHeapAll() {
        if (useHeap) {
            return heapCache;
        }
        return null;
    }
}
