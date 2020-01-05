package edu.nju.alerp.common.cache;


import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 缓存bean配置类
 * @Author: yangguan
 * @CreateDate: 2019-12-24 14:50
 */
@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<Integer, Object> productNameCache() {
        return buildObjectCache("productNameCache", 100000);
    }

    @Bean
    public Cache<Integer, String> supplierNameCache() {
        return buildStringCache("supplierNameCache", 100000);
    }

    @Bean
    public Cache<Integer, Object> userCache() {
        return buildObjectCache("userCache", 10000);
    }

    private Cache<Integer, Object>  buildObjectCache(String cacheName, long heapCacheLimit) {
//        String cacheFile = CacheConfiguration.class.getResource("/").getPath() + "/mapdb/" + cacheName;
        String cacheFile = "../../mapdb/"+ cacheName;
        //todo 文件路径要配置好
        mkdir(cacheFile);
        DB db = DBMaker.fileDB(cacheFile)
                //.checksumHeaderBypass()
                //.fileMmapEnableIfSupported()//1
                //.fileMmapPreclearDisable()//2
                .cleanerHackEnable()//3
                .closeOnJvmShutdown()//4
                //.transactionEnable()//5
                .concurrencyScale(128)//6
                .checksumHeaderBypass()
                .fileChannelEnable()
                .make();

        Map<Integer, Object> diskCache = db.hashMap(cacheName)
                .keySerializer(Serializer.INTEGER)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
//
        return new RealCache<>(new ConcurrentHashMap<>(), diskCache, heapCacheLimit);
    }

    private Cache<Integer, String>  buildStringCache(String cacheName, long heapCacheLimit) {
        String cacheFile = "../../mapdb/"+ cacheName;
        //todo 文件路径要配置好
        mkdir(cacheFile);
        DB db = DBMaker.fileDB(cacheFile)
                //.checksumHeaderBypass()
                //.fileMmapEnableIfSupported()//1
                //.fileMmapPreclearDisable()//2
                .cleanerHackEnable()//3
                .closeOnJvmShutdown()//4
                //.transactionEnable()//5
                .concurrencyScale(128)//6
                .checksumHeaderBypass()
                .fileChannelEnable()
                .make();

        Map<Integer, String> diskCache = db.hashMap(cacheName)
                .keySerializer(Serializer.INTEGER)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();
//
        return new RealCache<>(new ConcurrentHashMap<>(), diskCache, heapCacheLimit);
    }

    private static void mkdir(String file) {
        File newFile = new File(file);
        if (!newFile.exists()) {
            File dir = newFile.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
        }
    }
}
