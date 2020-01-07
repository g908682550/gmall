package com.npu.gmall.search;

import com.npu.gmall.vo.search.SearchParam;
import com.npu.gmall.vo.search.SearchResponse;

/**
 * 商品检索
 */
public interface SearchProductService {
    SearchResponse searchProduct(SearchParam searchParam);
}
