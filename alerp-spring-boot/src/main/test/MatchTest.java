import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Pattern;

@Slf4j
public class MatchTest {

    @Test
    public void testMatch() {

        String pattern = "/api/product/" + "([1-9]\\d*)?";
        String test1 = "/api/product/";
        String test2 = "/api/product";
        String test3 = "/api/product/1";

        log.info(Pattern.matches(pattern, test1)+"");
        log.info(Pattern.matches(pattern, test2)+"");
        log.info(Pattern.matches(pattern, test3)+"");
        log.info(Pattern.matches(test2, test2)+"");
    }
}
