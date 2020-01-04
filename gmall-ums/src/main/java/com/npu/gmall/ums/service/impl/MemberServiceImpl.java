package com.npu.gmall.ums.service.impl;

import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.ums.mapper.MemberMapper;
import com.npu.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2020-01-03
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
