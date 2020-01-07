package com.npu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.npu.gmall.constant.EsConstant;
import com.npu.gmall.search.SearchProductService;
import com.npu.gmall.vo.search.SearchParam;
import com.npu.gmall.vo.search.SearchResponse;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.soap.Node;
import java.io.IOException;

@Slf4j
@Service
@Component
public class SearchProductServiceImpl implements SearchProductService {

    @Autowired
    JestClient jestClient;

    @Override
    public SearchResponse searchProduct(SearchParam searchParam) {

        //构建检索条件
        String dsl=buildDsl(searchParam);

        log.error("商品检索的详细数据{}",dsl);

        Search build = new Search.Builder("").addIndex(EsConstant.PRODUCT_ES_INDEX).addType(EsConstant.PRODUCT_INFO_ES_TYPE).build();

        SearchResult execute=null;
        try {
            //进行检索
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将返回的SearchResult转为SearchResponse
        SearchResponse searchResponse=buildSearchResponse(execute);
        return searchResponse;
    }

    private SearchResponse buildSearchResponse(SearchResult execute) {
        return null;
    }

    private String buildDsl(SearchParam searchParam) {

        SearchSourceBuilder builder = new SearchSourceBuilder();

        //1、查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            //1.1、检索
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("skuProductInfos.skuTitle", searchParam.getKeyword());
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("skuProductInfos", matchQuery, ScoreMode.None);
            boolQuery.must(nestedQuery);
        }
            //1.2、过滤
            //按照3级分类的条件过滤
        if(searchParam.getCatelog3()!=null&&searchParam.getCatelog3().length>0){
            boolQuery.filter(QueryBuilders.termsQuery("productCategoryId",searchParam.getCatelog3()));
        }
            //按照品牌过滤
        if(searchParam.getBrand()!=null&&searchParam.getBrand().length>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandName.keyword",searchParam.getBrand()));
        }
                //1.2.1、按照属性过滤、按照品牌过滤、按照分类过滤
        if(searchParam.getProps()!=null&&searchParam.getProps().length>0){
            //按照所有的筛选属性进行过滤
            String[] props = searchParam.getProps();
            for(String prop:props){
                String[] split=prop.split(":");
                BoolQueryBuilder must = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("attrValueList.productAttributeId", split[0]))
                        .must(QueryBuilders.termsQuery("attrValueList.value", split[1].split("-")));
                NestedQueryBuilder queryBuilder = QueryBuilders.nestedQuery("attrValueList", must, ScoreMode.None);
                boolQuery.filter(queryBuilder);
            }
        }
        //价格区间过滤
        if(searchParam.getPriceFrom()!=null||searchParam.getPriceTo()!=null){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            if(searchParam.getPriceFrom()!=null){
                rangeQueryBuilder.gte(searchParam.getPriceFrom());
            }
            if(searchParam.getPriceTo()!=null){
                rangeQueryBuilder.lte(searchParam.getPriceTo());
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        builder.query(boolQuery);
        //2、高亮
        if(StringUtils.isEmpty(searchParam.getKeyword())){

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuProductInfos.skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            builder.highlighter(highlightBuilder);
        }
        //3、聚合
//
//        builder.aggregation();
        //4、分页
        builder.from((searchParam.getPageNum()-1)*searchParam.getPageSize());
        builder.size(searchParam.getPageSize());
        //5、排序
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();
            String[] split = order.split(":");
            if(split[0].equals("0")){
                //0:综合排序，默认顺序 1:销量 2：价格
                //order=1:asc
            }
            if(split[0].equals("1")){
                //销量排序
                FieldSortBuilder sale = SortBuilders.fieldSort("sale");
                if(split[1].equalsIgnoreCase("asc")){
                    sale.order(SortOrder.ASC);
                }else{
                    sale.order(SortOrder.DESC);
                }
                builder.sort(sale);
            }
            if(split[0].equals(2)) {
                //价格
                FieldSortBuilder price = SortBuilders.fieldSort("price");
                if (split[1].equalsIgnoreCase("asc")) {
                    price.order(SortOrder.ASC);
                } else {
                    price.order(SortOrder.DESC);
                }
                builder.sort(price);
            }
        }
        return builder.toString();
    }
}
