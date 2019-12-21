package edu.nju.alerp.Service;

import edu.nju.alerp.Dto.ProductDTO;
import edu.nju.alerp.Repo.ProductRepository;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAllByPage(Pageable pageable, String name, int type) {
        Specification<Product> sp = (Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            Predicate typeEquals = criteriaBuilder.equal(root.get("type").as(Integer.class), type);
            Predicate nameFuzzyMatch = criteriaBuilder.like(root.get("name").as(String.class), CommonUtils.fuzzyStringSplicing(name));
            Predicate shorthandFuzzyMatch = criteriaBuilder.like(root.get("shorthand").as(String.class),CommonUtils.fuzzyStringSplicing(name));
            return criteriaBuilder.and(typeEquals, criteriaBuilder.or(nameFuzzyMatch, shorthandFuzzyMatch));
        };
        return productRepository.findAll(sp, pageable);
    }

    @Override
    public Product findProductById(int id) {
        return productRepository.getOne(id);
    }

    @Override
    public int addOrUpdate(ProductDTO productDTO) {
        Product product = null;
        if (productDTO.getId() == null){
            product = Product.builder()
                    .create_at(new Date())
                    .update_at(new Date())
                    .density(productDTO.getDensity())
                    .name(productDTO.getName())
                    .shorthand(productDTO.getShorthand())
                    .type(Byte.valueOf(String.valueOf(productDTO.getType())))
                    .specification(productDTO.getSpecification()).build();
        }else {
            product = Product.builder()
                    .update_at(new Date())
                    .id(productDTO.getId())
                    .density(productDTO.getDensity())
                    .name(productDTO.getName())
                    .shorthand(productDTO.getShorthand())
                    .type(Byte.valueOf(String.valueOf(productDTO.getType())))
                    .specification(productDTO.getSpecification()).build();
        }
        return productRepository.saveAndFlush(product).getId();
    }
}
