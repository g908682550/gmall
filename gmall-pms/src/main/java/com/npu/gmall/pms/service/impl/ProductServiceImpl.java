package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.pms.entity.*;
import com.npu.gmall.pms.mapper.*;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.vo.PageInfoVo;
import com.npu.gmall.vo.product.PmsProductParam;
import com.npu.gmall.vo.product.PmsProductQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Slf4j
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    ProductFullReductionMapper productFullReductionMapper;

    @Autowired
    ProductLadderMapper productLadderMapper;

    @Autowired
    SkuStockMapper skuStockMapper;

    //当前线程共享同样的数据,同一次调用，上面的方法下面要用，就可以用ThreadLocal共享数据
    ThreadLocal<Long> threadLocal=new ThreadLocal<>();

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


        //pms_product_full_reduction:保存商品的满减信息
        proxy.saveFullReduction(productParam);

        //pms_product_ladder:阶梯价格表
        proxy.saveProductLadder(productParam);

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(PmsProductParam productParam) {
        List<ProductLadder> ladderList = productParam.getProductLadderList();
        ladderList.forEach(ladder->{
            ladder.setProductId(threadLocal.get());
            productLadderMapper.insert(ladder);
        });
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFullReduction(PmsProductParam productParam) {
        List<ProductFullReduction> fullReductionList = productParam.getProductFullReductionList();
        fullReductionList.forEach(reduction->{
            reduction.setProductId(threadLocal.get());
            productFullReductionMapper.insert(reduction);
        });
    }

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
}
