package org.jeecg.modules.mail_user.service.impl;

import org.jeecg.modules.mail_user.entity.MailUser;
import org.jeecg.modules.mail_user.mapper.MailUserMapper;
import org.jeecg.modules.mail_user.service.IMailUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 邮箱用户
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Service
public class MailUserServiceImpl extends ServiceImpl<MailUserMapper, MailUser> implements IMailUserService {

}
