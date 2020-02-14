package com.npu.gmall.pms.service;

import com.npu.gmall.vo.search.SearchParam;
import com.npu.gmall.vo.search.SearchResponse;

/**
 * 商品检索
 */
public interface SearchProductService {
    SearchResponse searchProduct(SearchParam searchParam);
}
