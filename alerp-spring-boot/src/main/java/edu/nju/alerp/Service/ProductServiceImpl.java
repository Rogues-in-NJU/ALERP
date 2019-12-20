package edu.nju.alerp.Service;

import edu.nju.alerp.Repo.ProductRepository;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Collectors;

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
    public ListResponse findAllByPage(int pageIndex, int pageSize, String name, int type) {
        List<Product> allProducts = findAll();
        List<Product> allPages = allProducts.stream().filter(p -> p.getType() == type && (p.getName().contains(name) || p.getShorthand().contains(name)))
                                                    .collect(Collectors.toList());

        int totalPage = allPages.size() / pageSize + 1;
        ListResponse res = new ListResponse();
        res.setPageIndex(pageIndex);
        res.setPageSize(pageSize);
        res.setTotalPages(totalPage);
        res.setResult(allPages.stream().skip(pageSize * (pageIndex - 1))
                .limit(pageSize).collect(Collectors.toList()));
        return res;
    }

    @Override
    public Product findProductById(int id) {
        return productRepository.getOne(id);
    }
}
