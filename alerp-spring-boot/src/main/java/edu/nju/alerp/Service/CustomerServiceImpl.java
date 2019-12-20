package edu.nju.alerp.Service;

import edu.nju.alerp.Dto.CustomerDTO;
import edu.nju.alerp.Dto.SpecialPricesDTO;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.Repo.CustomerRepository;
import edu.nju.alerp.Repo.SpecialPricesRepository;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrices;
import edu.nju.alerp.util.ListResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 客户服务层接口实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:44
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpecialPricesRepository specialPricesRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private ListResponseUtils listResponseUtils = new ListResponseUtils();

    @Override
    public boolean addCustomer(CustomerDTO customerDTO) {
        Customer customer = Customer.builder()
                .created_at(sdf.format(new Date()))
                .build();
        BeanUtils.copyProperties(customerDTO, customer);
        //前台需要传操作人信息，记录创建者是谁
        List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
        for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
            SpecialPrices specialPrices = SpecialPrices.builder()
                    .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                    .build();
            BeanUtils.copyProperties(specialPricesDTO, specialPrices);
            specialPricesRepository.save(specialPrices);
        }
        customerRepository.save(customer);
        return false;
    }

    @Override
    public boolean updateCustomer(CustomerDTO customerDTO) {
        Customer customer = Customer.builder()
                .updated_at(sdf.format(new Date()))
                .build();
        BeanUtils.copyProperties(customerDTO, customer);
        List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
        for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
            SpecialPrices specialPrices = specialPricesRepository.getOne(specialPricesDTO.getId());
            if(specialPrices == null){
                specialPrices = SpecialPrices.builder()
                        .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                        .build();
                BeanUtils.copyProperties(specialPricesDTO, specialPrices);
            }
            else{
                specialPrices.setUpdateAt(sdf.format(new Date()));
//                specialPrices.setUpdateById();//待获取用户id
            }
            specialPricesRepository.save(specialPrices);
        }
        customerRepository.save(customer);

        return false;
    }

    @Override
    public Customer getCustomer(int id) {
        return customerRepository.getOne(id);
    }

    @Override
    public List<SpecialPrices> getSpecialPricesListByCustomerId(int id) {
        return specialPricesRepository.findByCustomerId(id);
    }

    @Override
    public boolean deleteCustomer(int id) {
        Customer customer = getCustomer(id);
        if (customer == null) {
            return false;
        }
        customer.setDeleted_at(sdf.format(new Date()));
        customerRepository.save(customer);
        //考虑到客户为懒删除，建议暂存特惠价格数据
        return false;
    }

    @Override
    public List<Customer> getCustomerList() {
        return customerRepository.findAll();
    }

    @Override
    public ListResponse getCustomerListByName(int pageIndex, int pageSize, String name) {
        List<Customer> customerList = getCustomerList().stream().filter(c -> c.getName().contains(name) || c.getShorthand().contains(name)).collect(Collectors.toList());
        return listResponseUtils.getListResponse(customerList, pageIndex, pageSize);
    }
}
