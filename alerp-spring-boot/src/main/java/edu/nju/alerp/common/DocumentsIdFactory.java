package edu.nju.alerp.common;

import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.repo.IdGeneratorRepository;
import edu.nju.alerp.entity.IdGenerator;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.util.DateUtils;
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

    private Map<String, AtomicInteger> idGenerator = new ConcurrentHashMap<>();

    private Map<String, IdGenerator> generatorMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdGenerator> documents = idGeneratorRepository.findAll();
        documents.forEach(id -> {
            idGenerator.put(id.getDocuments(), buildCurrentCount(id));
            generatorMap.put(id.getDocuments(), id);
        });

    }

    public String generateNextCode(DocumentsType documentsType, CityEnum cityEnum) {
        int year = TimeUtil.getNowYear();
        int month = TimeUtil.getNowMonth();
        int day = TimeUtil.getNowDay();
        return String.format("%02d", cityEnum.getCode()) + String.valueOf(year) + String.format("%02d", month) +
                String.format("%02d", day) + String.format("%03d", getNext(documentsType, cityEnum));
    }

    private int getNext(DocumentsType documentsType, CityEnum cityEnum) {
        String documentsByCity = documentsType.getName() + "_" + cityEnum.getShorthand();
        AtomicInteger atomicInteger = idGenerator.get(documentsByCity);
        if (atomicInteger == null) {
            synchronized (this) {
                atomicInteger = idGenerator.get(documentsByCity);
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger();
                    idGenerator.put(documentsByCity, atomicInteger);
                }
            }
        }
        int nextCount = atomicInteger.incrementAndGet();

        IdGenerator generator = generatorMap.get(documentsByCity);
        if (generator == null) {
            synchronized (this) {
                generator = generatorMap.get(documentsByCity);
                if ( generator == null) {
                    generator = IdGenerator.builder().documents(documentsByCity).build();
                    generatorMap.put(documentsByCity, generator);
                }
            }
        }

        generator.increment();
        generator.setUpdateTime(DateUtils.getTodayAccurateToMinute());
        idGeneratorRepository.saveAndFlush(generator);

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
        long days = TimeUtil.getDays(DateUtils.getTodayAccurateToMinute(), idGenerator.getUpdateTime());
        if ( days == 0 )
            return new AtomicInteger(idGenerator.getCurrentCount());
        return new AtomicInteger();
    }
}
