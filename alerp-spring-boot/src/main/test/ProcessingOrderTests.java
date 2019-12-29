import com.google.common.collect.ImmutableList;
import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.ProcessingOrderProductDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.service.ProcessOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProcessingOrderTests extends BaseTest{

    @Autowired
    private ProcessOrderService processOrderService;

    @Test
    public void findAll() {
        log.info("[{}]", processOrderService.findAll());
    }

    @Test
    public void addProcessingOrder() {
        ProcessingOrderDTO processingOrderDTO = new ProcessingOrderDTO();
        processingOrderDTO.setSalesman("yangguan02");
        ProcessingOrderProductDTO processingOrderProductDTO = new ProcessingOrderProductDTO();
        processingOrderProductDTO.setProductId(1);
        processingOrderProductDTO.setSpecification("10x10x10");
        processingOrderProductDTO.setQuantity(1);
        processingOrderProductDTO.setExpectedWeight(12.3);
        processingOrderDTO.setProducts(ImmutableList.of(processingOrderProductDTO));
        processOrderService.addProcessingOrder(processingOrderDTO);
    }

    @Test
    public void updateProcessingOrderProduct() {
        UpdateProcessProductDTO updateProcessProductDTO = new UpdateProcessProductDTO();

        updateProcessProductDTO.setId(2);
        updateProcessProductDTO.setProcessingOrderId(1);
        updateProcessProductDTO.setProductId(1);
        updateProcessProductDTO.setSpecification("12345");
        updateProcessProductDTO.setQuantity(10);
        updateProcessProductDTO.setExpectedWeight(15.5);
        processOrderService.addOrUpdateProcessProduct(updateProcessProductDTO);
    }

    @Test
    public void addProcessingOrderProduct() {
        UpdateProcessProductDTO updateProcessProductDTO = new UpdateProcessProductDTO();

        updateProcessProductDTO.setProcessingOrderId(1);
        updateProcessProductDTO.setProductId(1);
        updateProcessProductDTO.setSpecification("20x10x15");
        updateProcessProductDTO.setQuantity(10);
        updateProcessProductDTO.setExpectedWeight(23.1);

        processOrderService.addOrUpdateProcessProduct(updateProcessProductDTO);
    }

    @Test
    public void deleteProcessingOrderProduct() {
        try {
            processOrderService.deleteProcessProduct(3);
        }catch (Exception e) {
            log.error("has error", e);
        }

    }

}
