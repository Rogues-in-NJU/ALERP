package edu.nju.alerp.common;

import edu.nju.alerp.repo.IdGeneratorRepository;
import edu.nju.alerp.entity.IdGenerator;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 单据Id生成器
 * @Author: yangguan
 * @CreateDate: 2019-12-22 17:26
 */
@Configuration
@Slf4j
@EnableScheduling
public class DocumentsIdFactory implements InitializingBean {

    @Autowired
    private IdGeneratorRepository idGeneratorRepository;

    private Map<DocumentsType, AtomicInteger> idGenerator = new ConcurrentHashMap<>();

    private Map<DocumentsType, IdGenerator> generatorMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdGenerator> documents = idGeneratorRepository.findAll();
        documents.forEach(id -> {
            idGenerator.put(DocumentsType.of(id.getDocuments()), buildCurrentCount(id));
            generatorMap.put(DocumentsType.of(id.getDocuments()), id);
        });

    }

    public String generateNextCode(DocumentsType documentsType) {
        int year = TimeUtil.getNowYear();
        int month = TimeUtil.getNowMonth();
        int day = TimeUtil.getNowDay();
        return String.valueOf(year) + String.format("%02d", month) + String.format("%02d", day) + String.format("%03d", getNext(documentsType));
    }

    private int getNext(DocumentsType documentsType) {
        AtomicInteger atomicInteger = idGenerator.get(documentsType) == null ? new AtomicInteger() : idGenerator.get(documentsType);
        int nextCount = atomicInteger.incrementAndGet();
        IdGenerator idGenerator = generatorMap.get(documentsType) == null ?
                IdGenerator.builder().documents(documentsType.getName()).build() : generatorMap.get(documentsType);

        idGenerator.setCurrentCount(nextCount);
        idGenerator.setUpdateTime(TimeUtil.dateFormat(new Date()));
//        idGeneratorRepository.updateIdByDocumentsType(nextCount, TimeUtil.dateFormat(new Date()), documentsType.getName());
        idGeneratorRepository.saveAndFlush(idGenerator);
        return nextCount;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    private void resetCount() {
        List<IdGenerator> ids = new ArrayList<>();
        for (DocumentsType item : DocumentsType.values()) {
            ids.add(IdGenerator.builder().documents(item.getName())
                                        .currentCount(0)
                                        .updateTime(TimeUtil.dateFormat(new Date()))
                                        .build());
        }
        idGeneratorRepository.saveAll(ids);
        idGeneratorRepository.flush();
        idGenerator.forEach( (d, c) -> c.set(0));
    }

    private AtomicInteger buildCurrentCount(IdGenerator idGenerator) {
        long days = TimeUtil.getDays(TimeUtil.dateFormat(new Date()), idGenerator.getUpdateTime());
        if ( days == 0 )
            return new AtomicInteger(idGenerator.getCurrentCount());
        return new AtomicInteger();
    }
}
