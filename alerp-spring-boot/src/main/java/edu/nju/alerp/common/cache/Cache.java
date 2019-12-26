package edu.nju.alerp.common.cache;

import java.util.Collection;
import java.util.Map;

/**
 * @Description: 缓存接口
 * @Author: yangguan
 * @CreateDate: 2019-12-24 14:28
 */
public interface Cache<K, V> {

    int size();

    V get(K key);

    void put(K key, V value);

    void putAll(Map<K, V> m);

    Map<K, V> getAll(Collection<K> ks);

    Map<K, V> getHeapAll();
}
