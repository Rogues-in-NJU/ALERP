package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.dto.ProductDTO;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.repo.ProductRepository;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
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
        products.forEach(product -> productNameCache.put(product.getId(), product));
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
            if (type != null)
                sp.add(ConditionFactory.equal("type", type));
            List<Condition> fuzzyMatch = new ArrayList<>();
            if (name != null) {
                fuzzyMatch.add(ConditionFactory.like("name", name));
                fuzzyMatch.add(ConditionFactory.like("shorthand", name));
                sp.add(ConditionFactory.or(fuzzyMatch));
            }
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
        Product product = Product.builder()
                .updateAt(DateUtils.getToday())
                .updateBy(CommonUtils.getUserId())
                .density(productDTO.getDensity())
                .name(productDTO.getName())
                .shorthand(productDTO.getShorthand())
                .type(Byte.valueOf(String.valueOf(productDTO.getType())))
                .specification(productDTO.getSpecification()).build();
        if (productDTO.getId() != null){
            product.setId(productDTO.getId());
            Product originProduct = (Product) productNameCache.get(productDTO.getId());
            if (!productDTO.getUpdateTime().equals(originProduct.getUpdateAt())) {
                throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "商品信息已变更，请重新更新");
            }
        }else {
            product.setCreateAt(DateUtils.getToday());
            product.setCreateBy(CommonUtils.getUserId());
        }
        product = productRepository.saveAndFlush(product);
        productNameCache.put(product.getId(), product);
        return product.getId();
    }
}