package com.npu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.npu.gmall.ums.entity.MemberLevel;
import com.npu.gmall.ums.mapper.MemberLevelMapper;
import com.npu.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 会员等级表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-03
 */
@Service
@Component
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

}
