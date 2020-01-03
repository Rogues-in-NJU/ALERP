import com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: Id生成器测试类
 * @Author: yangguan
 * @CreateDate: 2019-12-28 09:33
 */
@Slf4j
public class IdGeneratorTests extends BaseTest{

    @Autowired
    private DocumentsIdFactory documentsIdFactory;

    private ExecutorService poolExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS,
                                                                    new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder().setNameFormat("id-generate-thread" + "-%d").build());

    private final String message = "DocumentType is %s, current id is %s, current time is %s .";

    @Test
    public void parallelIdGenerateTest() {
        List<Future> futureList = new ArrayList<>();
        for (int i = 0 ; i < 100; i++) {
            FutureTask<String> task = new FutureTask<>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return generate(DocumentsType.PROCESSING_ORDER);
                }
            });
            futureList.add(task);
            poolExecutor.submit(task);
        }
        for (Future future : futureList) {
            try {
                log.info( (String) future.get());
            }catch (Exception e) {
                log.error("has error", e);
            }
        }
    }

    @Test
    public void testAtomic() {
        AtomicInteger atomicInteger = new AtomicInteger();
        log.info(atomicInteger.get()+"");
        log.info(atomicInteger.getAndIncrement()+"");
        log.info(atomicInteger.get()+"");
        log.info(atomicInteger.incrementAndGet()+"");
    }

    private String generate(DocumentsType documentsType) {
        String id = documentsIdFactory.generateNextCode(documentsType, CityEnum.SZ);
//        log.info(String.format(message,
//                documentsType.getName(), id, TimeUtil.dateFormat(new Date())));
        return String.format(message,
                documentsType.getName(), id, TimeUtil.dateFormat(new Date()));
    }

}
