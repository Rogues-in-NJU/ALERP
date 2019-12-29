package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.dto.ProductDTO;
import edu.nju.alerp.repo.ProductRepository;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService, InitializingBean {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Cache<Integer, Object> productNameCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Product> products = findAll();
        products.forEach(product -> productNameCache.put(product.getId(), product.getName()));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAllByPage(Pageable pageable, String name, Integer type) {
//        Specification<Product> sp = (Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
//            Predicate typeEquals = criteriaBuilder.equal(root.get("type").as(Integer.class), type);
//            Predicate nameFuzzyMatch = criteriaBuilder.like(root.get("name").as(String.class), CommonUtils.fuzzyStringSplicing(name));
//            Predicate shorthandFuzzyMatch = criteriaBuilder.like(root.get("shorthand").as(String.class),CommonUtils.fuzzyStringSplicing(name));
//            return criteriaBuilder.and(typeEquals, criteriaBuilder.or(nameFuzzyMatch, shorthandFuzzyMatch));
//        };
        QueryContainer<Product> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("type", type));
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("name", name));
            fuzzyMatch.add(ConditionFactory.like("shorthand", name));
            sp.add(ConditionFactory.or(fuzzyMatch));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        if (sp.isEmpty())
            return productRepository.findAll(pageable);

        return productRepository.findAll(sp, pageable);
    }

    @Override
    public Product findProductById(int id) {
        Product pro = (Product) productNameCache.get(id);
        if (pro == null)
            pro = productRepository.getOne(id);
        return pro;
    }

    @Override
    public String findProductNameById(int id) {
        Product res = (Product) productNameCache.get(id);
        String name = null;
        if (res == null) {
            res = findProductById(id);
            if (res != null) {
                name = res.getName();
                productNameCache.put(id, res);
            }
        }else {
            name = res.getName();
        }
        return name;
    }

    @Override
    public int addOrUpdate(ProductDTO productDTO) {
        HttpSession session = CommonUtils.getHttpSession();
        Product product = Product.builder()
                .createAt(TimeUtil.dateFormat(new Date()))
                .createBy(session.getAttribute("userId") == null ? 0 : (int) session.getAttribute("userId"))
                .updateAt(TimeUtil.dateFormat(new Date()))
                .updateBy(session.getAttribute("userId") == null ? 0 : (int) session.getAttribute("userId"))
                .density(productDTO.getDensity())
                .name(productDTO.getName())
                .shorthand(productDTO.getShorthand())
                .type(Byte.valueOf(String.valueOf(productDTO.getType())))
                .specification(productDTO.getSpecification()).build();
        if (productDTO.getId() != null){
            product.setId(productDTO.getId());
        }
        product = productRepository.saveAndFlush(product);
        productNameCache.put(product.getId(), product);
        return product.getId();
    }
}