package org.jeecg.modules.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.CoremailUtils;
import org.jeecg.Utils;
import org.jeecg.modules.jzg.entity.JzgJbxx;
import org.jeecg.modules.jzg.service.IJzgJbxxService;
import org.jeecg.modules.mail_user.entity.MailUser;
import org.jeecg.modules.mail_user.service.IMailUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import tebie.applib.api.APIContext;

@Slf4j
@Component
public class AutoCreateJZGCoremail implements Job {
  @Autowired
  private IMailUserService mailUserService;
  @Autowired
  private IJzgJbxxService jzgJbxxService;
  @Autowired
  private CoremailUtils coremailUtils;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("开始任务！");
    // 查找新增教职工
    QueryWrapper<MailUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", "0");
    List<MailUser> existTypedMailUsers = mailUserService.list(queryWrapper);
    List<String> existIds =
        existTypedMailUsers.stream().map(item -> item.getId()).collect(Collectors.toList());
    List<JzgJbxx> jzgList = jzgJbxxService.list();
    List<String> originalIds =
        jzgList.stream().map(item -> item.getId()).collect(Collectors.toList());
    List<String> diffIds = new ArrayList<>(CollectionUtil.subtract(originalIds, existIds));
    List<JzgJbxx> diffJzg = jzgList.stream().filter(item -> diffIds.contains(item.getId()))
        .collect(Collectors.toList());
    List<MailUser> diffMailUsers = diffJzg.stream()
        .map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
            item.getXbdm(), item.getSzdwmc(), item.getSfzjh(), item.getSjhm(), "0", "0"))
        .collect(Collectors.toList());
    log.info("增教职工 {} ", diffMailUsers.size());
    // 创建邮箱账号
    List<MailUser> successUsers = new ArrayList<>();
    List<MailUser> failedUsers = new ArrayList<>();
    for (MailUser mailUser : diffMailUsers) {
      String userAtDomain = String.format("%s@ynu.edu.cn", mailUser.getId());
      String domainName = "ynu.edu.cn";
      String[] attrNames = new String[] {"domain_name", "password", "cos_id", "user_status",
          "quota_delta", "true_name", "mobile_number", "gender", "address", "zipcode", "homepage"};
      String cardIdNum = mailUser.getCardIdNum();
      // 如果证件号为空或者证件号长度小于6（设置初始密码为证件号后六位,并且转换为小写）
      if (StringUtils.isEmpty(cardIdNum) || cardIdNum.length() < 6) {
        log.warn("创建 {} 邮箱账号失败，原始证件号是 {} 不满足预设条件", userAtDomain, cardIdNum);
        failedUsers.add(mailUser);
        continue;
      }
      String password = cardIdNum.substring(cardIdNum.length() - 6).toLowerCase();
      String[] attrValues = new String[] {domainName, password, "1", "0", "0", mailUser.getName(),
          mailUser.getPhone(), mailUser.getGender(), cardIdNum, mailUser.getId(),
          mailUser.getDepName()};
      String attrs = null;
      try {
        attrs = Utils.encode(attrNames, attrValues);
      } catch (Exception e) {
        e.printStackTrace();
      }
      APIContext ret = coremailUtils.createUser("1", "a", mailUser.getId(), attrs);
      if (ret.getRetCode() == 0) {
        successUsers.add(mailUser);
        log.info("创建 {} 邮箱账号成功！", userAtDomain);
      } else {
        failedUsers.add(mailUser);
        log.warn("创建 {} 邮箱账号失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
      }
    }
    // 通知记录
    log.info("成功创建增教职工 {} ", successUsers.size());
    log.info("失败创建增教职工 {} ", failedUsers.size());
    // 保存成功创建的MailUsers
    log.warn("正在保存MailUsers......");
    mailUserService.saveBatch(successUsers);
    log.warn("保存MailUsers成功......");
  }

}
