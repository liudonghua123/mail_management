package org.jeecg.modules.core_mail_user.service.impl;

import org.jeecg.modules.core_mail_user.entity.CoreMailUser;
import org.jeecg.modules.core_mail_user.mapper.CoreMailUserMapper;
import org.jeecg.modules.core_mail_user.service.ICoreMailUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: coremail用户
 * @Author: jeecg-boot
 * @Date:   2020-08-11
 * @Version: V1.0
 */
@Service
public class CoreMailUserServiceImpl extends ServiceImpl<CoreMailUserMapper, CoreMailUser> implements ICoreMailUserService {

}
