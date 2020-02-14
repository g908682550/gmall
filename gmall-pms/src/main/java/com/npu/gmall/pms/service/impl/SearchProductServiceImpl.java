package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.npu.gmall.constant.EsConstant;
import com.npu.gmall.search.SearchProductService;
import com.npu.gmall.to.es.EsProduct;
import com.npu.gmall.vo.search.SearchParam;
import com.npu.gmall.vo.search.SearchResponse;
import com.npu.gmall.vo.search.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Search build = new Search.Builder(dsl).addIndex(EsConstant.PRODUCT_ES_INDEX).addType(EsConstant.PRODUCT_INFO_ES_TYPE).build();

        SearchResult execute=null;
        try {
            //进行检索
            execute = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将返回的SearchResult转为SearchResponse
        SearchResponse searchResponse=buildSearchResponse(execute);
        searchResponse.setPageNum(searchParam.getPageNum());
        searchResponse.setPageSize(searchParam.getPageSize());
        return searchResponse;
    }

    private SearchResponse buildSearchResponse(SearchResult execute) {

        SearchResponse searchResponse = new SearchResponse();

        MetricAggregation aggregations = execute.getAggregations();

        //总数
        searchResponse.setTotal(execute.getTotal());

        //==================分析聚合品牌的信息
        TermsAggregation brand_agg = aggregations.getTermsAggregation("brand_agg");
        List<String> brandNames=new ArrayList<>();
        brand_agg.getBuckets().forEach(bucket-> {
            brandNames.add(bucket.getKeyAsString());
        }
        );
        SearchResponseAttrVo brand_attrVo = new SearchResponseAttrVo();
        brand_attrVo.setName("品牌");
        brand_attrVo.setValue(brandNames);
        searchResponse.setBrand(brand_attrVo);

        //=================分析分类聚合信息
        TermsAggregation category_agg = aggregations.getTermsAggregation("category_agg");

        List<String> categoryValues=new ArrayList<>();

        category_agg.getBuckets().forEach(bucket->{
            String categoryName = bucket.getKeyAsString();
            TermsAggregation categoryId_agg = bucket.getTermsAggregation("categoryId_agg");
            String categoryId = categoryId_agg.getBuckets().get(0).getKeyAsString();
            Map<String,String> map=new HashMap<>();
            map.put("id",categoryId);
            map.put("name", categoryName);
            String cateInfo = JSON.toJSONString(map);
            categoryValues.add(cateInfo);
        });
        SearchResponseAttrVo category_attrVo=new SearchResponseAttrVo();
        category_attrVo.setName("分类");
        category_attrVo.setValue(categoryValues);
        searchResponse.setCatelog(category_attrVo);

        //==============分析属性聚合信息
        TermsAggregation attrName_agg = aggregations.getChildrenAggregation("attr_agg").getTermsAggregation("attrName_agg");
        List<SearchResponseAttrVo> attrVoList=new ArrayList<>();
        attrName_agg.getBuckets().forEach(bucket->{
            SearchResponseAttrVo vo=new SearchResponseAttrVo();
            //当前属性的名字
            String attr_Name = bucket.getKeyAsString();
            //属性的id
            TermsAggregation attrId_agg = bucket.getTermsAggregation("attrId_agg");
            String attrId = attrId_agg.getBuckets().get(0).getKeyAsString();
            //属性的所涉及的所有值
            List<String> attrValues=new ArrayList<>();
            TermsAggregation attrValue_agg = bucket.getTermsAggregation("attrValue_agg");
            attrValue_agg.getBuckets().forEach(attrValue->{
                attrValues.add(attrValue.getKeyAsString());
            });
            vo.setName(attr_Name);
            vo.setProductAttributeId(Long.parseLong(attrId));
            vo.setValue(attrValues);
            attrVoList.add(vo);
        });
        searchResponse.setAttrs(attrVoList);
        //========================提取检索到的商品信息
        List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
        List<EsProduct> products=new ArrayList<>();
        hits.forEach(hit->{
            EsProduct source = hit.source;
            //提取到高亮结果
            String title = hit.highlight.get("skuProductInfos.skuTitle").get(0);
            //设置高亮结果
            source.setName(title);
            products.add(source);
        });
        searchResponse.setProducts(products);

        return searchResponse;
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
        if(!StringUtils.isEmpty(searchParam.getKeyword())){

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuProductInfos.skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            builder.highlighter(highlightBuilder);
        }
        //3、聚合,品牌聚合，分类聚合，属性聚合
        //按照品牌的
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandName.keyword");
        brand_agg.subAggregation(AggregationBuilders.terms("brandId").field("brandId"));
        builder.aggregation(brand_agg);
        //分类聚合
        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg").field("productCategoryName");
        category_agg.subAggregation(AggregationBuilders.terms("categoryId_agg").field("productCategoryId"));
        builder.aggregation(category_agg);
        //属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrValueList");
        TermsAggregationBuilder attrName_agg = AggregationBuilders.terms("attrName_agg").field("attrValueList.name");
        attrName_agg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrValueList.value"));
        attrName_agg.subAggregation(AggregationBuilders.terms("attrId_agg").field("attrValueList.productAttributeId"));
        attr_agg.subAggregation(attrName_agg);
        builder.aggregation(attr_agg);
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
