package edu.nju.alerp.util;

import edu.nju.alerp.common.ListResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 获取分页内容
 * @Author: qianen.yin
 * @CreateDate: 2019-12-20 21:21
 */
public class ListResponseUtils {

    public static <T> ListResponse getListResponse(List<T> responseList ,int pageIndex, int pageSize){
        ListResponse res = new ListResponse();
        // fixme:如果responseList.size()恰好是pageSize的整数倍，之类返回的totalPage就多了1
        int totalPage = responseList.size() / pageSize + 1;
        res.setPageIndex(pageIndex);
        res.setPageSize(pageSize);
        res.setTotalPages(totalPage);
        res.setResult(responseList.stream()
                .skip(pageSize * (pageIndex - 1))
                .limit(pageSize).collect(Collectors.toList()));
        return res;
    }

    public static ListResponse generateResponse(Page page, int pageIndex, int pageSize) {
        ListResponse listResponse = new ListResponse();
        listResponse.setTotalPages(page.getTotalPages());
        listResponse.setPageSize(pageSize);
        listResponse.setPageIndex(pageIndex);
        listResponse.setResult(page.getContent());
        return listResponse;
    }
}
