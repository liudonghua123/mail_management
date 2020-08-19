package org.jeecg.modules.core_mail_user.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.CoremailUtils;
import org.jeecg.Utils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.core_mail_user.entity.CoreMailUser;
import org.jeecg.modules.core_mail_user.service.ICoreMailUserService;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tebie.applib.api.APIContext;

/**
 * @Description: coremail用户
 * @Author: jeecg-boot
 * @Date: 2020-08-11
 * @Version: V1.0
 */
@Api(tags = "coremail用户")
@RestController
@RequestMapping("/core_mail_user/coreMailUser")
@Slf4j
public class CoreMailUserController extends JeecgController<CoreMailUser, ICoreMailUserService> {
  @Autowired
  private ICoreMailUserService coreMailUserService;
  @Autowired
  private ISysDictService sysDictService;
  @Autowired
  private CoremailUtils coremailUtils;

  /**
   * 分页列表查询
   *
   * @param coreMailUser
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  @AutoLog(value = "coremail用户-分页列表查询")
  @ApiOperation(value = "coremail用户-分页列表查询", notes = "coremail用户-分页列表查询")
  @GetMapping(value = "/list")
  public Result<?> queryPageList(CoreMailUser coreMailUser,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<CoreMailUser> queryWrapper =
        QueryGenerator.initQueryWrapper(coreMailUser, req.getParameterMap());
    Page<CoreMailUser> page = new Page<CoreMailUser>(pageNo, pageSize);
    IPage<CoreMailUser> pageList = coreMailUserService.page(page, queryWrapper);
    return Result.ok(pageList);
  }

  /**
   * 同步原始数据
   */
  @AutoLog(value = "coremail用户-同步coremail用户数据")
  @ApiOperation(value = "coremail用户-同步coremail用户数据", notes = "coremail用户-同步coremail用户数据")
  @GetMapping(value = "/sync")
  public Result<?> sync() {
    List<DictModel> coremailOrgNames = sysDictService.queryDictItemsByCode("coremail_org_names");
    List<DictModel> coremailOrgDomains =
        sysDictService.queryDictItemsByCode("coremail_org_domains");
    Map<String, String> orgs =
        coremailOrgNames.stream().map(item -> new String[] {item.getValue(), item.getText()})
            .collect(Collectors.toMap(item -> item[0], item -> item[1]));
    Map<String, String> orgDomains =
        coremailOrgDomains.stream().map(item -> new String[] {item.getValue(), item.getText()})
            .collect(Collectors.toMap(item -> item[0], item -> item[1]));
    log.info("正在获取组织用户列表数据......");
    Map<String, Map<String, String>> allUserAttrs = new HashMap<>();
    for (Entry<String, String> item : orgs.entrySet()) {
      log.info("正在获取 {}/{} 组织用户列表数据......", item.getKey(), item.getValue());
      String[] userList = coremailUtils.getUserListByOrgId(item.getKey());
      String domain = orgDomains.get(item.getKey());
      if (userList == null) {
        log.warn("获取 {}/{} 组织用户列表数据为空", item.getKey(), item.getValue());
        continue;
      }
      List<String> userAtDomains = Arrays.stream(userList)
          .map(u -> String.format("%s@%s", u, domain)).collect(Collectors.toList());
      log.info("正在获取 {}/{} 组织的用户列表属性数据......", item.getKey(), item.getValue());
      Map<String, Map<String, String>> attrsMap = coremailUtils.getAttrs(userAtDomains);
      log.info("正在获取 {}/{} 组织的用户别名信息数据......", item.getKey(), item.getValue());
      Map<String, String[]> aliasMap = coremailUtils.getSmtpAlias(userAtDomains);
      // 合并alias,username信息至属性信息
      for (Entry<String, Map<String, String>> attrItem : attrsMap.entrySet()) {
        String userAtDomain = attrItem.getKey();
        String[] alias = aliasMap.get(userAtDomain);
        Map<String, String> attrsValue = attrItem.getValue();
        if (alias != null) {
          attrsValue.put("alias",
              Arrays.stream(alias).map(fullAlias -> fullAlias.substring(0, fullAlias.indexOf("@")))
                  .collect(Collectors.joining(",")));
          attrsValue.put("username", userAtDomain.substring(0, userAtDomain.indexOf("@")));
        }
      }
      log.info("获取 {}/{} 组织的用户列表属性数据 {} 条", item.getKey(), item.getValue(), attrsMap.size());
      allUserAttrs.putAll(attrsMap);
    }
    log.info("正在转换组织用户列表数据......");
    Set<CoreMailUser> coreMailUserList = coremailUtils.convertToCoreMailUser(allUserAttrs);
    // 清空数据
    log.warn("正在清空数据......");
    QueryWrapper<CoreMailUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.isNotNull("id");
    coreMailUserService.remove(queryWrapper);
    // 添加新数据
    log.warn("正在添加新数据......");
    coreMailUserService.saveBatch(coreMailUserList);
    log.warn("成功添加新数据：{}条", coreMailUserList.size());
    return Result.ok("同步原始数据！");
  }

  /**
   * 更新邮箱账号
   */
  @AutoLog(value = "coremail用户-更新邮箱账号")
  @ApiOperation(value = "coremail用户-更新邮箱账号", notes = "coremail用户-更新邮箱账号")
  @PutMapping(value = "/updateCoreMailUser")
  public Result<?> updateMailUser(
      @RequestParam(name = "userAtDomain", required = true) String userAtDomain,
      @RequestParam(name = "attrs", required = true) String attrs) {
    APIContext ret = coremailUtils.changeAttrs(userAtDomain, attrs);
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      return Result.error(200, ret.getErrorInfo());
    }
    log.info("更新邮箱账号 {} 成功！", userAtDomain);
    // 更新本地信息
    Map<String, String> newAttrs = coremailUtils.getAttrs(userAtDomain);
    if (newAttrs == null) {
      log.warn("获取用户 {} 属性信息错误......", userAtDomain);
      return Result.error(String.format("获取用户 %s 属性信息错误......", userAtDomain));
    }
    CoreMailUser coreMailUser = JSON.parseObject(JSON.toJSONString(newAttrs), CoreMailUser.class);
    coreMailUser.setId(userAtDomain);
    coreMailUserService.updateById(coreMailUser);
    return Result.ok(String.format("更新邮箱账号 %s 成功！", userAtDomain));
  }

  /**
   * 批量更新邮箱账号
   */
  @AutoLog(value = "coremail用户-批量更新邮箱账号")
  @ApiOperation(value = "coremail用户-批量更新邮箱账号", notes = "coremail用户-批量更新邮箱账号")
  @PutMapping(value = "/updateCoreMailUserBatch")
  public Result<?> updateCoreMailUserBatch(
      @RequestParam(name = "userAtDomains", required = true) String userAtDomains,
      @RequestParam(name = "attrs", required = true) String attrs) {
    List<String> userAtDomainList = Arrays.asList(userAtDomains.split(","));
    List<String> successList = new ArrayList<>();
    List<String> failedList = new ArrayList<>();
    List<CoreMailUser> coreMailUsers = new ArrayList<>();
    for (int i = 0; i < userAtDomainList.size(); i++) {
      String userAtDomain = userAtDomainList.get(i);
      APIContext ret = coremailUtils.changeAttrs(userAtDomain, attrs);
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("批量更新邮箱账号 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
            ret.getErrorInfo());
        failedList.add(userAtDomain);
        continue;
      }
      log.info("批量更新邮箱账号 {} 成功！", userAtDomain);
      Map<String, String> newAttrs = coremailUtils.getAttrs(userAtDomain);
      CoreMailUser coreMailUser = JSON.parseObject(JSON.toJSONString(newAttrs), CoreMailUser.class);
      coreMailUser.setId(userAtDomain);
      coreMailUsers.add(coreMailUser);
    }
    // 批量更新本地信息
    coreMailUserService.updateBatchById(coreMailUsers);
    return Result.ok(String.format("批量更新邮箱账号 %s 成功！", userAtDomains),
        new BatchResponse<String>(successList, failedList));
  }

  /**
   * 添加
   *
   * @param coreMailUser
   * @return
   * @throws Exception
   */
  @AutoLog(value = "coremail用户-添加")
  @ApiOperation(value = "coremail用户-添加", notes = "coremail用户-添加")
  @PostMapping(value = "/add")
  public Result<?> add(@RequestBody CoreMailUser coreMailUser) throws Exception {
    String userAtDomain = coreMailUser.getId();
    String id = userAtDomain.substring(0, userAtDomain.indexOf("@"));
    String domain = userAtDomain.substring(userAtDomain.indexOf("@") + 1);
    // 这里的日期JSON序列化的时候不会参考Entity里的注解，无法正确处理
    Map<String, Object> attrMap =
        JSON.parseObject(JSON.toJSONStringWithDateFormat(coreMailUser, "yyyy-MM-dd"));
    // 删除非coremail属性
    String[] nonCoremailAttrs = new String[] {"id", "create_by", "create_time", "update_by",
        "update_time", "sys_org_code", "alias", "username"};
    for (String nonCoremailAttr : nonCoremailAttrs) {
      attrMap.remove(nonCoremailAttr);
    }
    // 创建账号
    String attrs = Utils.encode(attrMap);
    log.info("添加 {} 邮箱账号 attrs {}", id, attrs);
    APIContext ret = coremailUtils.createUser("1", "a", id, attrs);
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      log.warn("添加邮箱账号 {} 失败，code: {}, msg: {}", id, ret.getRetCode(), ret.getErrorInfo());
      return Result.error(200, ret.getErrorInfo());
    }
    // 添加别名
    String alias = coreMailUser.getAlias();
    if (!StringUtils.isEmpty(alias)) {
      String[] aliasArray = alias.split(",");
      for (String singleAlias : aliasArray) {
        coremailUtils.addSmtpAlias(userAtDomain, String.format("%s@%s", singleAlias, domain));
      }
    }
    // 重新获取coremail中的属性信息、别名信息，转换为CoreMailUser，然后保存
    Map<String, String> userAttrs = coremailUtils.getAttrs(userAtDomain);
    String[] smtpAlias = coremailUtils.getSmtpAlias(userAtDomain);
    CoreMailUser newCoreMailUser =
        JSON.parseObject(JSON.toJSONString(userAttrs), CoreMailUser.class);
    newCoreMailUser.setId(userAtDomain);
    newCoreMailUser.setUsername(id);
    if (smtpAlias != null) {
      newCoreMailUser.setAlias(Arrays.stream(smtpAlias)
          .map(item -> item.substring(0, item.indexOf("@"))).collect(Collectors.joining(",")));
    }
    log.info("添加邮箱账号 {} 成功！", id);
    coreMailUserService.save(newCoreMailUser);
    return Result.ok(String.format("添加邮箱账号 %s 成功！", id));
  }

  /**
   * 编辑
   *
   * @param coreMailUser
   * @return
   * @throws Exception
   */
  @AutoLog(value = "coremail用户-编辑")
  @ApiOperation(value = "coremail用户-编辑", notes = "coremail用户-编辑")
  @PutMapping(value = "/edit")
  public Result<?> edit(@RequestBody CoreMailUser coreMailUser) throws Exception {
    String userAtDomain = coreMailUser.getId();
    String id = userAtDomain.substring(0, userAtDomain.indexOf("@"));
    String domain = userAtDomain.substring(userAtDomain.indexOf("@") + 1);
    Map<String, Object> attrMap =
        JSON.parseObject(JSON.toJSONStringWithDateFormat(coreMailUser, "yyyy-MM-dd"));
    // 删除非coremail属性
    String[] nonCoremailAttrs = new String[] {"id", "create_by", "create_time", "update_by",
        "update_time", "sys_org_code", "alias", "username"};
    for (String nonCoremailAttr : nonCoremailAttrs) {
      attrMap.remove(nonCoremailAttr);
    }
    // 创建账号
    // domain_name 属性只可读，不可写，不能在更新属性中
    attrMap.remove("domain_name");
    // 如果密码字段为空，则不更新密码
    if (attrMap.get("password").equals("")) {
      attrMap.remove("password");
    }
    String attrs = Utils.encode(attrMap);
    log.info("更新 {} 邮箱账号 attrs {}", userAtDomain, attrs);
    APIContext ret = coremailUtils.changeAttrs(userAtDomain, attrs);
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      log.warn("更新邮箱账号 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
          ret.getErrorInfo());
      return Result.error(200, ret.getErrorInfo());
    }
    // 删除原别名，添加别名
    String alias = coreMailUser.getAlias();
    CoreMailUser originCoreMailUser = coreMailUserService.getById(userAtDomain);
    String originAlias = originCoreMailUser.getAlias();
    boolean changed = !alias.equals(originAlias);
    if (changed && !StringUtils.isEmpty(originAlias)) {
      log.info("删除 {} 的别名 {}", userAtDomain, originAlias);
      String[] aliasArray = originAlias.split(",");
      for (String singleAlias : aliasArray) {
        coremailUtils.delSmtpAlias(userAtDomain, String.format("%s@%s", singleAlias, domain));
      }
    }
    if (changed && !StringUtils.isEmpty(alias)) {
      log.info("添加 {} 的别名 {}", userAtDomain, originAlias);
      String[] aliasArray = alias.split(",");
      for (String singleAlias : aliasArray) {
        coremailUtils.addSmtpAlias(userAtDomain, String.format("%s@%s", singleAlias, domain));
      }
    }
    // 重新获取coremail中的属性信息，转换为CoreMailUser，然后更新
    Map<String, String> userAttrs = coremailUtils.getAttrs(userAtDomain);
    String[] smtpAlias = coremailUtils.getSmtpAlias(userAtDomain);
    CoreMailUser newCoreMailUser =
        JSON.parseObject(JSON.toJSONString(userAttrs), CoreMailUser.class);
    newCoreMailUser.setId(userAtDomain);
    newCoreMailUser.setUsername(id);
    if (smtpAlias != null) {
      newCoreMailUser.setAlias(Arrays.stream(smtpAlias)
          .map(item -> item.substring(0, item.indexOf("@"))).collect(Collectors.joining(",")));
    }
    log.info("更新邮箱账号 {} 成功！", userAtDomain);
    coreMailUserService.updateById(newCoreMailUser);
    return Result.ok(String.format("更新邮箱账号 %s 成功！", userAtDomain));
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "coremail用户-通过id删除")
  @ApiOperation(value = "coremail用户-通过id删除", notes = "coremail用户-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
    APIContext ret = coremailUtils.deleteUser(id);
    // 删除别名
    String[] smtpAlias = coremailUtils.getSmtpAlias(id);
    if (smtpAlias != null) {
      for (String alias : smtpAlias) {
        coremailUtils.delSmtpAlias(id, alias);
      }
    }
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      log.warn("删除邮箱账号 {} 失败，code: {}, msg: {}", id, ret.getRetCode(), ret.getErrorInfo());
      return Result.error(200, ret.getErrorInfo());
    }
    log.info("删除邮箱账号 {} 成功！", id);
    coreMailUserService.removeById(id);
    return Result.ok(String.format("删除邮箱账号 %s 成功！", id));
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "coremail用户-批量删除")
  @ApiOperation(value = "coremail用户-批量删除", notes = "coremail用户-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    List<String> idList = Arrays.asList(ids.split(","));
    List<String> successList = new ArrayList<>();
    List<String> failedList = new ArrayList<>();
    for (int i = 0; i < idList.size(); i++) {
      String id = idList.get(i);
      APIContext ret = coremailUtils.deleteUser(id);
      // 删除别名
      String[] smtpAlias = coremailUtils.getSmtpAlias(id);
      if (smtpAlias != null) {
        for (String alias : smtpAlias) {
          coremailUtils.delSmtpAlias(id, alias);
        }
      }
      if (ret.getRetCode() != APIContext.RC_NORMAL) {
        log.warn("删除邮箱账号 {} 失败，code: {}, msg: {}", id, ret.getRetCode(), ret.getErrorInfo());
        failedList.add(id);
        continue;
      }
      successList.add(id);
      log.info("删除邮箱账号 {} 成功！", id);
    }
    this.coreMailUserService.removeByIds(idList);
    return Result.ok("批量删除成功!", new BatchResponse<String>(successList, failedList));
  }

  /**
   * 通过 userAtDomain 验证账号是否存在
   *
   * @param id
   * @return
   */
  @AutoLog(value = "coremail用户-验证账号是否存在")
  @ApiOperation(value = "coremail用户-验证账号是否存在", notes = "coremail用户-验证账号是否存在")
  @GetMapping(value = "/userExist")
  public Result<?> userExist(
      @RequestParam(name = "userAtDomain", required = true) String userAtDomain) {
    APIContext ret = coremailUtils.userExist(userAtDomain);
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      log.warn("验证账号是否存在 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
          ret.getErrorInfo());
      return Result.error(200, ret.getErrorInfo());
    }
    return Result.ok(String.format("验证账号是否存在 %s 成功！", userAtDomain), ret.getRetCode());
  }

  /**
   * 通过 userAtDomain,password 验证账号密码是否正确
   *
   * @param userAtDomain
   * @param password
   * @return
   */
  @AutoLog(value = "coremail用户-验证账号密码是否正确")
  @ApiOperation(value = "coremail用户-验证账号密码是否正确", notes = "coremail用户-验证账号密码是否正确")
  @GetMapping(value = "/authenticate")
  public Result<?> authenticate(
      @RequestParam(name = "userAtDomain", required = true) String userAtDomain,
      @RequestParam(name = "password", required = true) String password) {
    APIContext ret = coremailUtils.authenticate(userAtDomain, password);
    if (ret.getRetCode() != APIContext.RC_NORMAL) {
      log.warn("验证账号密码是否正确 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
          ret.getErrorInfo());
      return Result.error(200, ret.getErrorInfo());
    }
    return Result.ok(String.format("验证账号密码是否正确 %s 成功！", userAtDomain), ret.getRetCode());
  }

  /**
   * 通过 userAtDomain 获取用户属性信息
   *
   * @param id
   * @return
   */
  @AutoLog(value = "coremail用户-获取用户属性信息")
  @ApiOperation(value = "coremail用户-获取用户属性信息", notes = "coremail用户-获取用户属性信息")
  @GetMapping(value = "/getAttrs")
  public Result<?> getAttrs(
      @RequestParam(name = "userAtDomain", required = true) String userAtDomain) {
    Map<String, String> attrs = coremailUtils.getAttrs(userAtDomain);
    if (attrs == null) {
      log.warn("获取用户属性信息 {} 失败", userAtDomain);
      return Result.error(200, String.format("获取用户 %s 属性信息失败", userAtDomain));
    }
    return Result.ok(String.format("获取用户 %s 属性信息成功！", userAtDomain), attrs);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class BatchResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    List<T> successList;
    List<T> failedList;
  }


  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  @AutoLog(value = "coremail用户-通过id查询")
  @ApiOperation(value = "coremail用户-通过id查询", notes = "coremail用户-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
    CoreMailUser coreMailUser = coreMailUserService.getById(id);
    if (coreMailUser == null) {
      return Result.error("未找到对应数据");
    }
    return Result.ok(coreMailUser);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param coreMailUser
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, CoreMailUser coreMailUser) {
    return super.exportXls(request, coreMailUser, CoreMailUser.class, "coremail用户");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
    return super.importExcel(request, response, CoreMailUser.class);
  }

}
