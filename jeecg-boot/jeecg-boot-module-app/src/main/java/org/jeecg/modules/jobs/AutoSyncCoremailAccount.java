package org.jeecg.modules.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.CoremailUtils;
import org.jeecg.Utils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.bks.entity.BksJbxx;
import org.jeecg.modules.bks.service.IBksJbxxService;
import org.jeecg.modules.jzg.entity.JzgJbxx;
import org.jeecg.modules.jzg.service.IJzgJbxxService;
import org.jeecg.modules.mail_user.entity.MailUser;
import org.jeecg.modules.mail_user.service.IMailUserService;
import org.jeecg.modules.message.util.PushMsgUtil;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.yjs.entity.YjsJbxx;
import org.jeecg.modules.yjs.service.IYjsJbxxService;
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
public class AutoSyncCoremailAccount implements Job {

  private String parameter;

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  @Autowired
  private IMailUserService mailUserService;
  @Autowired
  private IBksJbxxService bksJbxxService;
  @Autowired
  private IYjsJbxxService yjsJbxxService;
  @Autowired
  private IJzgJbxxService jzgJbxxService;
  @Autowired
  private CoremailUtils coremailUtils;
  // @Autowired
  // private EmailSendMsgHandle emailSendMsgHandle;
  @Autowired
  private PushMsgUtil pushMsgUtil;
  @Autowired
  private ISysDictService sysDictService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // type值通过定时任务parameter传入
    String type = this.parameter;
    log.info("开始任务！， 类型是 {}", type);
    if (!Arrays.asList("0", "1", "2").contains(type)) {
      log.info("类型错误，传入的参数只能是 0,1,2 分别表示 教职工，本科生，研究生！");
      return;
    }
    // 查找新增用户
    QueryWrapper<MailUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", type);
    List<MailUser> existTypedMailUsers = mailUserService.list(queryWrapper);
    List<String> existIds =
        existTypedMailUsers.stream().map(item -> item.getId()).collect(Collectors.toList());
    List<MailUser> diffMailUsers = null;
    // 教职工
    if (type.equals("0")) {
      List<JzgJbxx> jzgList = jzgJbxxService.list();
      List<String> originalIds =
          jzgList.stream().map(item -> item.getId()).collect(Collectors.toList());
      List<String> diffIds = new ArrayList<>(CollectionUtil.subtract(originalIds, existIds));
      List<JzgJbxx> diffJzg = jzgList.stream().filter(item -> diffIds.contains(item.getId()))
          .collect(Collectors.toList());
      diffMailUsers = diffJzg.stream()
          .map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
              item.getXbdm(), item.getSzdwmc(), item.getSfzjh(), item.getSjhm(), "0", "0"))
          .collect(Collectors.toList());
    }
    // 本科生
    else if (type.equals("1")) {
      List<BksJbxx> bksList = bksJbxxService.list();
      List<String> originalIds =
          bksList.stream().map(item -> item.getId()).collect(Collectors.toList());
      List<String> diffIds = new ArrayList<>(CollectionUtil.subtract(originalIds, existIds));
      List<BksJbxx> diffBks = bksList.stream().filter(item -> diffIds.contains(item.getId()))
          .collect(Collectors.toList());
      diffMailUsers = diffBks.stream()
          .map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
              item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "1", "0"))
          .collect(Collectors.toList());
    }
    // 研究生
    else if (type.equals("2")) {
      List<YjsJbxx> yjsList = yjsJbxxService.list();
      List<String> originalIds =
          yjsList.stream().map(item -> item.getId()).collect(Collectors.toList());
      List<String> diffIds = new ArrayList<>(CollectionUtil.subtract(originalIds, existIds));
      List<YjsJbxx> diffYjs = yjsList.stream().filter(item -> diffIds.contains(item.getId()))
          .collect(Collectors.toList());
      diffMailUsers = diffYjs.stream()
          .map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
              item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "2", "0"))
          .collect(Collectors.toList());
    }
    // 创建邮箱账号
    List<MailUser> successUsers = new ArrayList<>();
    List<MailUser> failedUsers = new ArrayList<>();
    for (MailUser mailUser : diffMailUsers) {
      String userAtDomain = String.format("%s@mail.ynu.edu.cn", mailUser.getId());
      if (type.equals("0")) {
        userAtDomain = String.format("%s@ynu.edu.cn", mailUser.getId());
      }
      String domainName = type.equals("0") ? "ynu.edu.cn" : "mail.ynu.edu.cn";
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
    log.info("成功创建增用户 {} ", successUsers.size());
    log.info("失败创建增用户 {} ", failedUsers.size());
    // 保存成功创建的MailUsers
    log.info("正在保存 MailUsers......");
    mailUserService.saveBatch(successUsers);
    log.info("保存 MailUsers 成功......");

    // 发送通知公告
    List<DictModel> userTypeDicts = sysDictService.queryDictItemsByCode("user_type");
    List<DictModel> notificationMailDicts =
        sysDictService.queryDictItemsByCode("notification_mails");
    Map<String, String> userTypes =
        userTypeDicts.stream().map(item -> new String[] {item.getValue(), item.getText()})
            .collect(Collectors.toMap(item -> item[0], item -> item[1]));
    Map<String, String> notificationMails =
        notificationMailDicts.stream().map(item -> new String[] {item.getValue(), item.getText()})
            .collect(Collectors.toMap(item -> item[0], item -> item[1]));
    Map<String, String> data = new HashMap<>();
    data.put("datetime", Utils.commonDateFormat.format(new Date()));
    data.put("type", userTypes.get(type));
    data.put("count", String.format("成功: %s/失败: %s", successUsers.size(), failedUsers.size()));
    data.put("details",
        String.format("成功: %s/失败: %s",
            successUsers.stream().map(item -> item.getId()).collect(Collectors.joining(",")),
            failedUsers.stream().map(item -> item.getId()).collect(Collectors.joining(","))));
    for (Entry<String, String> item : notificationMails.entrySet()) {
      log.info("正在发送邮件 {}({}) ......", item.getKey(), item.getValue());
      pushMsgUtil.sendMessage("2", "coremail_account_async", data, item.getKey());
    }
  }

}
