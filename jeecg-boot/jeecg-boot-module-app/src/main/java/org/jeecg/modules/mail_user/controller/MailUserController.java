package org.jeecg.modules.mail_user.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.CoremailUtils;
import org.jeecg.Utils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.bks.entity.BksJbxx;
import org.jeecg.modules.bks.service.IBksJbxxService;
import org.jeecg.modules.jzg.entity.JzgJbxx;
import org.jeecg.modules.jzg.service.IJzgJbxxService;
import org.jeecg.modules.mail_user.entity.MailUser;
import org.jeecg.modules.mail_user.service.IMailUserService;
import org.jeecg.modules.yjs.entity.YjsJbxx;
import org.jeecg.modules.yjs.service.IYjsJbxxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tebie.applib.api.APIContext;

/**
 * @Description: 邮箱用户
 * @Author: jeecg-boot
 * @Date: 2020-08-05
 * @Version: V1.0
 */
@Api(tags = "邮箱用户")
@RestController
@RequestMapping("/mail_user/mailUser")
@Slf4j
public class MailUserController extends JeecgController<MailUser, IMailUserService> {
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


	@Value("${coremail.endpointHost}")
	String endpointHost;
	@Value("${coremail.endpointPort}")
	int endpointPort;

	/**
	 * 分页列表查询
	 *
	 * @param mailUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "邮箱用户-分页列表查询")
	@ApiOperation(value = "邮箱用户-分页列表查询", notes = "邮箱用户-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(MailUser mailUser,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<MailUser> queryWrapper =
				QueryGenerator.initQueryWrapper(mailUser, req.getParameterMap());
		Page<MailUser> page = new Page<MailUser>(pageNo, pageSize);
		IPage<MailUser> pageList = mailUserService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 同步原始数据
	 */
	@AutoLog(value = "邮箱用户-同步原始数据")
	@ApiOperation(value = "邮箱用户-同步原始数据", notes = "邮箱用户-同步原始数据")
	@GetMapping(value = "/sync")
	public Result<?> sync() {
		List<BksJbxx> bksList = bksJbxxService.list();
		List<YjsJbxx> yjsList = yjsJbxxService.list();
		List<JzgJbxx> jzgList = jzgJbxxService.list();
		log.warn("正在合并原始数据......");
		List<MailUser> mailUserList = mergeUser(bksList, yjsList, jzgList);
		QueryWrapper<MailUser> queryWrapper = new QueryWrapper<>();
		queryWrapper.isNotNull("id");
		// 清空数据
		log.warn("正在清空数据......");
		mailUserService.remove(queryWrapper);
		// 添加新数据
		log.warn("正在添加新数据......");
		mailUserService.saveBatch(mailUserList);
		log.warn("成功添加新数据：{}条", mailUserList.size());
		return Result.ok("同步原始数据！");
	}

	/**
	 * 差异原始比对
	 */
	@AutoLog(value = "邮箱用户-差异原始比对")
	@ApiOperation(value = "邮箱用户-差异原始比对", notes = "邮箱用户-差异原始比对")
	@GetMapping(value = "/diff")
	public Result<?> diff(@RequestParam(name = "type", defaultValue = "1") String type) {
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
		return Result.ok(diffMailUsers);
	}

	/**
	 * 创建账号
	 * 
	 * @throws Exception
	 */
	@AutoLog(value = "邮箱用户-创建账号")
	@ApiOperation(value = "邮箱用户-创建账号", notes = "邮箱用户-创建账号")
	@PostMapping(value = "/createMailUser")
	public Result<?> createMailUser(@RequestParam(name = "ids", required = true) String ids,
			@RequestParam(name = "type", defaultValue = "1") String type) throws Exception {
		List<String> idList = Arrays.asList(ids.split(","));
		List<MailUser> mappedMailUsers = null;
		// 教职工
		if (type.equals("0")) {
			QueryWrapper<JzgJbxx> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("id", idList);
			List<JzgJbxx> jzgList = jzgJbxxService.list(queryWrapper);
			mappedMailUsers = jzgList.stream()
					.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
							item.getXbdm(), item.getSzdwmc(), item.getSfzjh(), item.getSjhm(), "0", "1"))
					.collect(Collectors.toList());
		}
		// 本科生
		else if (type.equals("1")) {
			QueryWrapper<BksJbxx> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("id", idList);
			List<BksJbxx> bksList = bksJbxxService.list(queryWrapper);
			mappedMailUsers = bksList.stream()
					.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
							item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "1", "1"))
					.collect(Collectors.toList());
		}
		// 研究生
		else if (type.equals("2")) {
			QueryWrapper<YjsJbxx> queryWrapper = new QueryWrapper<>();
			queryWrapper.in("id", idList);
			List<YjsJbxx> yjsList = yjsJbxxService.list();
			mappedMailUsers = yjsList.stream()
					.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
							item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "2", "1"))
					.collect(Collectors.toList());
		}
		// 创建邮箱账号
		List<MailUser> successUsers = new ArrayList<>();
		List<MailUser> failedUsers = new ArrayList<>();
		createMailUser(mappedMailUsers, successUsers, failedUsers, type);
		// 保存成功创建的MailUsers
		log.warn("正在保存MailUsers......");
		mailUserService.saveBatch(successUsers);
		log.warn("保存MailUsers成功......");
		return Result.ok(new CreateMailResponse(successUsers, failedUsers));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class CreateMailResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		List<MailUser> successUsers;
		List<MailUser> failedUsers;
	}

	/**
	 * 获取用户属性
	 */
	@AutoLog(value = "邮箱用户-获取用户属性")
	@ApiOperation(value = "邮箱用户-获取用户属性", notes = "邮箱用户-获取用户属性")
	@GetMapping(value = "/mailUserAttrs")
	public Result<?> mailUserAttrs(
			@RequestParam(name = "userAtDomain", required = true) String userAtDomain) {
		Map<String, String> attrs = coremailUtils.getAttrs(userAtDomain);
		if (attrs == null) {
			return Result.error(200, String.format("获取 %s 用户属性信息错误", userAtDomain));
		}
		return Result.ok(attrs);
	}

	/**
	 * 删除邮箱账号s
	 */
	@AutoLog(value = "邮箱用户-删除邮箱账号")
	@ApiOperation(value = "邮箱用户-删除邮箱账号", notes = "邮箱用户-删除邮箱账号")
	@DeleteMapping(value = "/deleteMailUser")
	public Result<?> deleteMailUser(
			@RequestParam(name = "userAtDomain", required = true) String userAtDomain) {
		APIContext ret = coremailUtils.deleteUser(userAtDomain);
		if (ret.getRetCode() != APIContext.RC_NORMAL) {
			log.warn("删除邮箱账号 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
					ret.getErrorInfo());
			return Result.error(200, ret.getErrorInfo());
		}
		log.info("删除邮箱账号 {} 成功！", userAtDomain);
		return Result.ok(String.format("删除邮箱账号 %s 成功！", userAtDomain));
	}

	/**
	 * 更新邮箱账号
	 */
	@AutoLog(value = "邮箱用户-更新邮箱账号")
	@ApiOperation(value = "邮箱用户-更新邮箱账号", notes = "邮箱用户-更新邮箱账号")
	@PutMapping(value = "/updateMailUser")
	public Result<?> updateMailUser(
			@RequestParam(name = "userAtDomain", required = true) String userAtDomain,
			@RequestParam(name = "attrs", required = true) String attrs) {
		APIContext ret = coremailUtils.changeAttrs(userAtDomain, attrs);
		if (ret.getRetCode() != APIContext.RC_NORMAL) {
			log.warn("更新邮箱账号 {} 失败，code: {}, msg: {}", userAtDomain, ret.getRetCode(),
					ret.getErrorInfo());
			return Result.error(200, ret.getErrorInfo());
		}
		log.info("更新邮箱账号 {} 成功！", userAtDomain);
		return Result.ok(String.format("更新邮箱账号 %s 成功！", userAtDomain));
	}

	/**
	 * <pre>
	 * /home/coremail/conf/res_zh_CN.cf
			[UD/AttrNames]
			password          = "密码"
			pwd_hint_question = "密码提示问题"
			pwd_hint_answer   = "密码提示答案"
			time_zone         = "时区"
			alt_email         = "备用邮件地址"
			true_name         = "姓名"
			nick_name         = "昵称"
			gender            = "性别"
			birthday          = "生日"
			mobile_number     = "手机号码"
			home_phone        = "家庭电话"
			company_phone     = "公司电话"
			fax_number        = "传真号码"
			address           = "身份证号"
			#address           = "联系地址"
			#zipcode           = "邮政编码"
			zipcode           = "工号(学号)"
			city              = "城市"
			province          = "省份/州"
			country           = "国家/地区"
			homepage          = "学院(及专业)"
			#homepage          = "公司主页"
			anniversary       = "周年纪念日"
			reg_ip            = "注册IP"
			duty              = "职位"
			remarks           = "备注"
			org_unit_fullName = "完整部门路径"
			login_ip_range    = "IP绑定"
			alias             = "别名"
			quota_delta       = "邮箱附加容量"
			user_expiry_date  = "到期日期"
	 * </pre>
	 * 
	 * @throws Exception
	 */
	private void createMailUser(List<MailUser> mappedMailUsers, List<MailUser> successUsers,
			List<MailUser> failedUsers, String type) throws Exception {
		for (MailUser mailUser : mappedMailUsers) {
			String userAtDomain = String.format("%s@mail.ynu.edu.cn", mailUser.getId());
			if (type.equals("0")) {
				userAtDomain = String.format("%s@ynu.edu.cn", mailUser.getId());
			}
			// createUser(providerId, orgId, userId, attrs)
			// providerId 固定为"1"
			// orgId 组织标识
			// userId 用户名
			// domainName 域名
			// cosId 服务等级标识，缺省服务为"1"
			// userStatus 用户状态，正常为"0"，停用为"1"，锁定为"4"
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
			String attrs = Utils.encode(attrNames, attrValues);
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
	}

	private List<MailUser> mergeUser(List<BksJbxx> bksList, List<YjsJbxx> yjsList,
			List<JzgJbxx> jzgList) {
		List<MailUser> mailUserList = new ArrayList<>();
		List<MailUser> bks = bksList.stream()
				.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
						item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "1", "0"))
				.collect(Collectors.toList());
		List<MailUser> yjs = yjsList.stream()
				.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
						item.getXbdm(), item.getYxmc(), item.getSfzjh(), item.getSjhm(), "2", "0"))
				.collect(Collectors.toList());
		List<MailUser> jzg = jzgList.stream()
				.map(item -> new MailUser(item.getId(), null, null, null, null, null, item.getXm(),
						item.getXbdm(), item.getSzdwmc(), item.getSfzjh(), item.getSjhm(), "0", "0"))
				.collect(Collectors.toList());
		mailUserList.addAll(bks);
		mailUserList.addAll(yjs);
		mailUserList.addAll(jzg);
		return mailUserList;
	}

	/**
	 * 添加
	 *
	 * @param mailUser
	 * @return
	 */
	@AutoLog(value = "邮箱用户-添加")
	@ApiOperation(value = "邮箱用户-添加", notes = "邮箱用户-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MailUser mailUser) {
		mailUserService.save(mailUser);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param mailUser
	 * @return
	 */
	@AutoLog(value = "邮箱用户-编辑")
	@ApiOperation(value = "邮箱用户-编辑", notes = "邮箱用户-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MailUser mailUser) {
		mailUserService.updateById(mailUser);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "邮箱用户-通过id删除")
	@ApiOperation(value = "邮箱用户-通过id删除", notes = "邮箱用户-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		mailUserService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "邮箱用户-批量删除")
	@ApiOperation(value = "邮箱用户-批量删除", notes = "邮箱用户-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.mailUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "邮箱用户-通过id查询")
	@ApiOperation(value = "邮箱用户-通过id查询", notes = "邮箱用户-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		MailUser mailUser = mailUserService.getById(id);
		if (mailUser == null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(mailUser);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param mailUser
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, MailUser mailUser) {
		return super.exportXls(request, mailUser, MailUser.class, "邮箱用户");
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
		return super.importExcel(request, response, MailUser.class);
	}

}
