import edu.nju.alerp.Repo.ProductRepository;
import edu.nju.alerp.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ProductTest extends BaseTest{

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void productWriteTests() {
        List<Product> products = productRepository.findAll();
        log.info("[{}]", products);
    }
}
