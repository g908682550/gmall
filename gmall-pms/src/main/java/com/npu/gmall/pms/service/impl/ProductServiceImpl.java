package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.constant.EsConstant;
import com.npu.gmall.pms.entity.*;
import com.npu.gmall.pms.mapper.*;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.to.es.EsProduct;
import com.npu.gmall.to.es.EsProductAttributeValue;
import com.npu.gmall.to.es.EsSkuProductInfo;
import com.npu.gmall.vo.PageInfoVo;
import com.npu.gmall.vo.product.PmsProductParam;
import com.npu.gmall.vo.product.PmsProductQueryParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-02
 *
 * 查询要多试几次，增删改快速失败。
 */
@Slf4j
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    JestClient jestClient;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    SkuStockMapper skuStockMapper;

    //当前线程共享同样的数据,同一次调用，上面的方法下面要用，就可以用ThreadLocal共享数据
    ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 改掉默认的mapping信息；
     * 1）、改掉不分词的字段
     * @param ids
     * @param publishStatus
     */
    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        if(publishStatus==0){
            //下架
            ids.forEach(id->{
                //改数据库状态
                setProductPublishStatus(publishStatus, id);
                //删es
                deleteProductFromEs(id);
            });
        }else{
            //上架
            ids.forEach(id->{
                //改数据库状态
                setProductPublishStatus(publishStatus, id);
                //加es
                saveProductToEs(id);
            });
        }
    }

    private void deleteProductFromEs(Long id) {
        Delete delete = new Delete.Builder(id.toString()).index(EsConstant.PRODUCT_ES_INDEX).type(EsConstant.PRODUCT_INFO_ES_TYPE).id(id.toString()).build();
        try {
            DocumentResult execute = jestClient.execute(delete);
            if(execute.isSucceeded()) log.error("商品：{}==》ES下架完成",id);
            else {
//                deleteProductFromEs(id);
                log.error("商品：{}==》ES下架失败",id);
            }
        } catch (IOException e) {
//            deleteProductFromEs(id);
            log.error("商品：{}==》ES下架失败",id);
        }
    }

    /**
     * 给数据库插入数据
     * 1）dubbo远程调入插入数据服务，可能经常超时。dubbo默认会重试，导致这个方法会被调用多次，可能导致数据库同样的数据有多个。
     * 2）dubbo有自己默认的集群容错 failover/failfast/failsafe/failback/forking
     * 给数据库做数据的，最好用dubbo的快速失败模式。我们手工重试
     * @param id
     */
    private void saveProductToEs(Long id) {
        //查出商品基本信息
        Product productInfo = productInfo(id);
        //1、复制基本信息
        EsProduct esProduct=new EsProduct();
        BeanUtils.copyProperties(productInfo,esProduct);
        //2、复制sku信息,对于es要保存商品信息,还要查出这个商品的sku,给es保存
        List<SkuStock> stocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
        List<EsSkuProductInfo> esSkuProductInfos=new ArrayList<>(stocks.size());
        /**
         * 查出销售属性的名字,销售属性是动态的，他的值是不确定的，由es检索的时候动态聚合出这个商品所有销售属性的值
         * select pa.* from pms_product_attribute pa where pa.product_attribute_category_id=
         * (
         * select pa.product_attribute_category_id
         * from pms_product_attribute_value pav
         * LEFT JOIN pms_product_attribute pa on pa.id=pav.product_attribute_id
         * where pav.product_id=#{id} AND pa.type=0 LIMIT 1
         * )and type=0 ORDER BY pa.sort DESC;
         */
        List<ProductAttribute> skuAttributeNames=productAttributeValueMapper.selectProductSaleAttrName(id);
        stocks.forEach(skuStock -> {
            EsSkuProductInfo info = new EsSkuProductInfo();
            BeanUtils.copyProperties(skuStock,info);

            //生成sku的特色标题
            String subTitle=esProduct.getName();
            if(StringUtils.isEmpty(skuStock.getSp1())) subTitle+=" "+skuStock.getSp1();
            if(StringUtils.isEmpty(skuStock.getSp2())) subTitle+=" "+skuStock.getSp2();
            if(StringUtils.isEmpty(skuStock.getSp3())) subTitle+=" "+skuStock.getSp3();
            info.setSkuTitle(subTitle);
            //查出sku所有销售属性对应的值,要统计数据库中这个sku有多少个值

            List<EsProductAttributeValue> skuAttributeValues=new ArrayList<>();
            for (int i = 0; i <skuAttributeNames.size() ; i++) {
                EsProductAttributeValue value = new EsProductAttributeValue();
                value.setName(value.getName());
                value.setProductId(id);
                value.setProductAttributeId(skuAttributeNames.get(i).getId());
                value.setType(skuAttributeNames.get(i).getType());
                //某个销售属性具体的值，查询商品的属性分类里面所有的属性的时候，按照sort字段排序好
                if(i==0){
                    value.setValue(skuStock.getSp1());
                }else if(i==1){
                    value.setValue(skuStock.getSp2());
                }else{
                    value.setValue(skuStock.getSp3());
                }
                skuAttributeValues.add(value);
            }

            info.setAttributeValues(skuAttributeValues);
            esSkuProductInfos.add(info);
        });
        esProduct.setSkuProductInfos(esSkuProductInfos);
        /**
         * 查出商品的属性及对应的值
         * select pav.*,pa.`name`,type from pms_product_attribute_value pav LEFT JOIN pms_product_attribute pa ON pav.product_attribute_id=pa.id where pav.product_id=23 and pa.type=1
         */
        List<EsProductAttributeValue> attributeValues=productAttributeValueMapper.selectProductBaseAttributeAndValue(id);
        //3、复制公共属性，即spu属性
        esProduct.setAttrValueList(attributeValues);

        //把商品保存在es中
        try {
            Index build = new Index.Builder(esProduct).index(EsConstant.PRODUCT_ES_INDEX).id(id.toString()).type(EsConstant.PRODUCT_INFO_ES_TYPE).build();
            DocumentResult execute = jestClient.execute(build);
            boolean succeeded = execute.isSucceeded();
            if(succeeded) log.info("ES中,id为{}商品上架完成",id);
            else{
                log.info("ES中,{}号商品未保存成功，开始重试",id);
//                saveProductToEs(id);
            }
        } catch (IOException e) {
            log.error("ES中，id为{}商品数据保存异常,{}",id,e.getMessage());
//            saveProductToEs(id);
        }
    }

    public void setProductPublishStatus(Integer publishStatus, Long id) {
        //该product默认所有属性为空
        Product product = new Product();
        product.setId(id);
        product.setPublishStatus(publishStatus);
        //mybatis-plus自带更新方法是哪个字段有值更哪个，其它不变
        productMapper.updateById(product);
    }


    /**
     * 大保存
     * @param productParam
     * 考虑事务
     * 1、哪些东西是一定要回滚的、哪些即使出错了不必要回滚
     *      商品的核心信息（基本数据，sku）保存的时候，不要受到别的无关信息的影响，无关信息出问题，核心信息不用回滚。
     * 2、事务的传播行为；propagation:当前方法的事务[是否要和别人共用一个事务]如何传播下去（里面的方法如果用事务，是否和它共用一个事务）
     *      Propagation propagation() default Propagation.REQUIRED;
     *      REQUIRED（必须）:如果以前有事务，就和之前的事务共用一个事务，没有就创建一个事务
     *      REQUIRES_NEW（总是用新的事务）:创建一个新的事务，如果以前有事务，暂停前面的事务。
     *      SUPPORTS（支持）:之前有事务就以事务的方式运行，没有事务也可以；
     *      MANDATORY（强制）:一定要有事务，如果没事务就报错
     *      NOT_SUPPORTED（不支持）:不支持在事务内运行，如果有事务，则挂起事务。
     *      NEVER（从不使用事务）:不支持事务内运行，如果有事务，抛出异常。
     *      NESTED:开启一个子事务（MySQL不支持），需要支持还原点的数据库
     *
     * 外事务{
     *     A();//事务.Required
     *     B();//事务 Required_new
     *     C();//事务 Required
     *     D();//事务：Required_new
     *}
     *     //给数据库存 --外
     *  场景1：A方法出现异常，A回滚，BCD不执行
     *  场景2：C方法出现异常，A回滚，B成功，C回滚，D不执行
     *  场景3：外成了后出异常，BD成，A,C,外回滚。
     *  场景4：D炸，抛异常，外事务感知异常，A,C回滚，外执行不到，D自己回滚，B成功
     *  场景5：C用try-catch执行；C出了异常回滚，由于异常被捕获，外事务没有感知异常，A,B,D都成，C自己回滚。
     *  总结：传播行为过程中，只要Required_new被执行过就一定成功，不管后面出不出问题。异常机制还是一样的，出现异常代码以后不执行。
     *          Required只要感觉到异常就一定回滚。
     *
     * 事务在Spring中是怎么做的？
     * TransactionManager;
     * AOP做的
     * 动态代理：XXXProxy.saveBaseInfo();
     * 自己类调用自己类里面的方法，就是一个复制粘贴，归根到底值加了一个事务。
     * Controller调Service其实是调了Service的代理对象，即ServiceProxy.x();相当于只给x加了事务。
     * 对象.方法（）才能加上事务。
     * 事务的问题：Service自己调用自己的方法无法加上事务
     *      解决：如果是对象.方法()就可以
     *          拿到IOC容器，从容器中再把组件获取一下，用对象调方法。
     *
     * 事务传播行为
     * =============================
     * 隔离级别：解决读写加锁问题的 mysql默认：可重复读（快照）
     *
     * 读未提交
     * 读已提交
     * 可重复度
     * 串行化
     * =============================
     * 异常回滚策略
     * 异常：
     *      运行时异常（不受查异常）
     *          int i=10/0;
     *      编译时异常（受检异常）
     *          FileNotFound;1)要么throw，要么try catch
     * 运行异常默认是一定回滚，noRollbackFor指定某些异常不回滚
     * 编译时异常默认是不回滚的，但可以在注解上用rollbackFor指定某些异常回滚
     */
    @Override
    @Transactional
    public void saveProduct(PmsProductParam productParam) {

        ProductServiceImpl proxy = (ProductServiceImpl)AopContext.currentProxy();

        //pms_product：保存商品基本信息
        proxy.saveBaseInfo(productParam);

        //pms_product_attribute_value:保存这个商品对应的所有属性的值
        proxy.saveProductAttributeValue(productParam);


//        //pms_product_full_reduction:保存商品的满减信息
//        proxy.saveFullReduction(productParam);
//
//        //pms_product_ladder:阶梯价格表
//        proxy.saveProductLadder(productParam);

        //pms_sku_stock:sku库存表
        proxy.saveSkuStock(productParam);

        //以上的写法相当于一个saveProduct事务。
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSkuStock(PmsProductParam productParam) {
        List<SkuStock> skuStockList = productParam.getSkuStockList();
        for (int i = 0; i <skuStockList.size() ; i++) {
            SkuStock skuStock=skuStockList.get(i);
            if(StringUtils.isEmpty(skuStock.getSkuCode())){
                skuStock.setSkuCode(threadLocal.get()+"_"+i);
            }
            skuStock.setProductId(threadLocal.get());
            skuStockMapper.insert(skuStock);
        }
    }
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void saveProductLadder(PmsProductParam productParam) {
//        List<ProductLadder> ladderList = productParam.getProductLadderList();
//        ladderList.forEach(ladder->{
//            ladder.setProductId(threadLocal.get());
//            productLadderMapper.insert(ladder);
//        });
//    }
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void saveFullReduction(PmsProductParam productParam) {
//        List<ProductFullReduction> fullReductionList = productParam.getProductFullReductionList();
//        fullReductionList.forEach(reduction->{
//            reduction.setProductId(threadLocal.get());
//            productFullReductionMapper.insert(reduction);
//        });
//    }

    public void saveProductAttributeValue(PmsProductParam productParam) {
        List<ProductAttributeValue> valueList = productParam.getProductAttributeValueList();
        valueList.forEach((item)->{
            //mybatis-plus可以获取到刚刚保存到product数据库的id,首先将属性的产品id设置好，这个是自生成的，不是前端传过来的
            item.setProductId(threadLocal.get());
            productAttributeValueMapper.insert(item);
        });
    }

    public Product saveBaseInfo(PmsProductParam productParam){
        Product product=new Product();
        BeanUtils.copyProperties(productParam,product);
        productMapper.insert(product);
        threadLocal.set(product.getId());
        return product;
    }

    @Override
    public PageInfoVo productPageInfo(PmsProductQueryParam param) {

        QueryWrapper<Product> wrapper=new QueryWrapper<>();

        if(param.getBrandId()!=null) wrapper.eq("brand_id",param.getBrandId());
        if(!StringUtils.isEmpty(param.getKeyword())) wrapper.like("name",param.getKeyword());
        if(param.getProductCategoryId()!=null) wrapper.eq("product_category_id",param.getProductCategoryId());
        if(!StringUtils.isEmpty(param.getProductSn())) wrapper.like("product_sn",param.getProductSn());
        if(param.getPublishStatus()!=null) wrapper.eq("publish_status",param.getPublishStatus());
        if(param.getVerifyStatus()!=null) wrapper.eq("verify_status",param.getVerifyStatus());

        IPage<Product> page = productMapper.selectPage(new Page<Product>(param.getPageNum(), param.getPageSize()), wrapper);
        PageInfoVo vo = new PageInfoVo(page.getTotal(), page.getPages(), param.getPageSize(), page.getRecords(), page.getCurrent());
        return vo;
    }

    @Override
    public EsProduct productAllInfo(Long id) {

        EsProduct esProduct=null;

        SearchSourceBuilder builder=new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("id",id));

        Search build = new Search.Builder(builder.toString()).addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        SearchResult execute=null;
        try {
            execute = jestClient.execute(build);
            List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
            esProduct = hits.get(0).source;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return esProduct;
    }

    @Override
    public EsProduct productSkuInfo(Long id) {
        EsProduct esProduct=null;

        SearchSourceBuilder builder=new SearchSourceBuilder();
        builder.query(QueryBuilders.nestedQuery("skuProductInfos",QueryBuilders.termQuery("skuProductInfos.id",id), ScoreMode.None));

        Search build = new Search.Builder(builder.toString()).addIndex(EsConstant.PRODUCT_ES_INDEX)
                .addType(EsConstant.PRODUCT_INFO_ES_TYPE)
                .build();
        SearchResult execute=null;
        try {
            execute = jestClient.execute(build);
            List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
            esProduct = hits.get(0).source;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return esProduct;
    }

    @Override
    public Product productInfo(Long id) {
        return productMapper.selectById(id);
    }
}
