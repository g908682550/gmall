package com.npu.gmall.search;

import com.npu.gmall.vo.search.SearchParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Autowired
    SearchProductService searchProductService;

    @Test
    void dslTest(){
        SearchParam searchParam = new SearchParam();
        searchParam.setKeyword("手机");
        String[] brand=new String[]{"苹果"};
        searchParam.setBrand(brand);
        String[] cate=new String[]{"19","20"};
        searchParam.setCatelog3(cate);
        searchParam.setPriceFrom(5000);
        searchParam.setPriceTo(10000);
        String[] props=new String[]{"45:4.7","46:4G"};
        searchParam.setProps(props);
        searchProductService.searchProduct(searchParam);
    }


    @Test
    void contextLoads() {

        try {
            Search build = new Search.Builder("").addIndex("product").addType("info").build();
            SearchResult execute = jestClient.execute(build);
            System.out.println(execute.getTotal());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
