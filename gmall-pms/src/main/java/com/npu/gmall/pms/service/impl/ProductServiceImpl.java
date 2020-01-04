package com.npu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npu.gmall.pms.entity.Product;
import com.npu.gmall.pms.mapper.ProductMapper;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.vo.PageInfoVo;
import com.npu.gmall.vo.product.PmsProductQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-01-02
 */
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    ProductMapper productMapper;

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
}
