package com.dong.easyeval.service.impl;

import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.mapper.TUserMapper;
import com.dong.easyeval.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author dong
 * @since 2026-03-21
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

}
