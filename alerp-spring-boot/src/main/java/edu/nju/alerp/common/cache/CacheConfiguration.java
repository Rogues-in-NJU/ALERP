package edu.nju.alerp.common.cache;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 缓存bean配置类
 * @Author: yangguan
 * @CreateDate: 2019-12-24 14:50
 */
@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<Integer, String> productNameCache() {
        return buildStringCache("productNameCache", 1000000);
    }

    private Cache<Integer, String>  buildStringCache(String cacheName, long heapCacheLimit) {
        String cacheFile = ""+ cacheName;
        mkdir(cacheFile);
//        DB db = DBMaker.fileDB(cacheFile)
//                //.checksumHeaderBypass()
//                //.fileMmapEnableIfSupported()//1
//                //.fileMmapPreclearDisable()//2
//                .cleanerHackEnable()//3
//                .closeOnJvmShutdown()//4
//                //.transactionEnable()//5
//                .concurrencyScale(128)//6
//                .checksumHeaderBypass()
//                .fileChannelEnable()
//
        return new RealCache<>(new ConcurrentHashMap<>(), null, heapCacheLimit);
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
