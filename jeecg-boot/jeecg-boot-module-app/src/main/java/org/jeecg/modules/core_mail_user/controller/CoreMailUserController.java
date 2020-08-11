package org.jeecg.modules.core_mail_user.controller;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.Utils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.core_mail_user.entity.CoreMailUser;
import org.jeecg.modules.core_mail_user.service.ICoreMailUserService;
import org.jeecg.modules.mail_user.entity.MailUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import lombok.extern.slf4j.Slf4j;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

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


	@Value("${coremail.endpointHost}")
	String endpointHost;
	@Value("${coremail.endpointPort}")
	int endpointPort;

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
		Map<String, String> orgs = new HashMap<>();
		orgs.put("a", "云南大学教师");
		orgs.put("b", "云南大学学生");
		orgs.put("c", "云南大学校友");
		// orgs.put("d","云南大学主节点");
		orgs.put("e", "云南大学软件学院");
		orgs.put("f", "无组织通讯录");
		orgs.put("xxjszx", "信息技术中心");

		Map<String, String> orgDomains = new HashMap<>();
		orgDomains.put("a", "ynu.edu.cn");
		orgDomains.put("b", "mail.ynu.edu.cn");
		orgDomains.put("c", "alumnu.ynu.edu.cn");
		// orgDomains.put("d","yn.edu.cn");
		orgDomains.put("e", "sei.ynu.edu.cn");
		orgDomains.put("f", "ynu.edu.cn");
		orgDomains.put("xxjszx", "ynu.edu.cn");

		log.info("正在获取组织用户列表数据......");
		Map<String, Map<String, String>> allUserAttrs = new HashMap<>();
		for (Entry<String, String> item : orgs.entrySet()) {
			log.info("正在获取 {}/{} 组织用户列表数据......", item.getKey(), item.getValue());
			String[] userList = getUserListByOrgId(item.getKey());
			String domain = orgDomains.get(item.getKey());
			if (userList == null) {
				log.warn("获取 {}/{} 组织用户列表数据为空", item.getKey(), item.getValue());
				continue;
			}
			List<String> userAtDomains = Arrays.stream(userList)
					.map(u -> String.format("%s@%s", u, domain)).collect(Collectors.toList());
			log.info("正在获取 {}/{} 组织的用户列表属性数据......", item.getKey(), item.getValue());
			Map<String, Map<String, String>> attrs = getAttrs(userAtDomains);
			log.info("获取 {}/{} 组织的用户列表属性数据 {} 条", item.getKey(), item.getValue(), attrs.size());
			allUserAttrs.putAll(attrs);
		}
		log.info("正在转换组织用户列表数据......");
		Set<CoreMailUser> coreMailUserList = convertToCoreMailUser(allUserAttrs);
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

	private Set<CoreMailUser> convertToCoreMailUser(Map<String, Map<String, String>> allUserAttrs) {
		Set<CoreMailUser> coreMailUserList = new HashSet<>();
		for (Entry<String, Map<String, String>> userAttrsMap : allUserAttrs.entrySet()) {
			String userAtDomain = userAttrsMap.getKey();
			Map<String, String> userAttrs = userAttrsMap.getValue();
			// String id = userAtDomain.substring(0, userAtDomain.indexOf("@"));
			CoreMailUser coreMailUser =
					JSON.parseObject(JSON.toJSONString(userAttrs), CoreMailUser.class);
			coreMailUser.setId(userAtDomain);
			coreMailUserList.add(coreMailUser);
		}
		return coreMailUserList;
	}

	private String[] getUserListByOrgId(String orgId) {
		IClient client = null;
		try {
			Socket socket = new Socket(endpointHost, endpointPort);
			client = APIContext.getClient(socket);
			APIContext ret = client.getOrgCosUser(orgId, 1);
			if (ret.getRetCode() != APIContext.RC_NORMAL) {
				System.out
						.println("User list failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
				return null;
			}
			String result = ret.getResult();
			return result.equals("") ? new String[] {} : result.split(",");
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	private Map<String, Map<String, String>> getAttrs(List<String> userAtDomains) {
		Map<String, Map<String, String>> results = new HashMap<>();
		IClient client = null;
		try {
			Socket socket = new Socket(endpointHost, endpointPort);
			client = APIContext.getClient(socket);
			String[] attrNames = new String[] {"cos_id", "user_list_rank", "user_status", "domain_name",
					"quota_delta", "nf_quota_delta", "user_expiry_date", "org_unit_id", "password",
					"forwarddes", "forwardactive", "keeplocal", "time_zone", "afterdel", "alt_email",
					"pwd_hint_question", "pwd_hint_answer", "true_name", "nick_name", "mobile_number",
					"home_phone", "company_phone", "fax_number", "gender", "province", "city", "birthday",
					"address", "zipcode", "homepage", "country", "anniversary", "reg_ip", "duty", "remarks",
					"org_unit_fullName", "login_ip_range"};
			String[] attrValues =
					IntStream.range(0, attrNames.length).mapToObj(i -> "").toArray(x -> new String[x]);
			String attrs = Utils.encode(attrNames, attrValues);
			for (String userAtDomain : userAtDomains) {
				APIContext ret = client.getAttrs(userAtDomain, attrs);
				if (ret.getRetCode() != APIContext.RC_NORMAL) {
					System.out.println(
							"User attrs failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
					continue;
				}
				// System.out.println("User attrs: " + ret.getResult());
				results.put(userAtDomain, Utils.decode(ret.getResult()));
			}
			return results;
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	private Map<String, String> getAttrs(String userAtDomain) {
		IClient client = null;
		try {
			Socket socket = new Socket(endpointHost, endpointPort);
			client = APIContext.getClient(socket);
			String[] attrNames = new String[] {"cos_id", "user_list_rank", "user_status", "domain_name",
					"quota_delta", "nf_quota_delta", "user_expiry_date", "org_unit_id", "password",
					"forwarddes", "forwardactive", "keeplocal", "time_zone", "afterdel", "alt_email",
					"pwd_hint_question", "pwd_hint_answer", "true_name", "nick_name", "mobile_number",
					"home_phone", "company_phone", "fax_number", "gender", "province", "city", "birthday",
					"address", "zipcode", "homepage", "country", "anniversary", "reg_ip", "duty", "remarks",
					"org_unit_fullName", "login_ip_range"};
			String[] attrValues =
					IntStream.range(0, attrNames.length).mapToObj(i -> "").toArray(x -> new String[x]);
			String attrs = Utils.encode(attrNames, attrValues);
			APIContext ret = client.getAttrs(userAtDomain, attrs);
			if (ret.getRetCode() != APIContext.RC_NORMAL) {
				System.out
						.println("User attrs failed, code=" + ret.getRetCode() + ", msg=" + ret.getErrorInfo());
				return null;
			}
			System.out.println("User attrs: " + ret.getResult());
			return Utils.decode(ret.getResult());
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	/**
	 * 添加
	 *
	 * @param coreMailUser
	 * @return
	 */
	@AutoLog(value = "coremail用户-添加")
	@ApiOperation(value = "coremail用户-添加", notes = "coremail用户-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CoreMailUser coreMailUser) {
		coreMailUserService.save(coreMailUser);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param coreMailUser
	 * @return
	 */
	@AutoLog(value = "coremail用户-编辑")
	@ApiOperation(value = "coremail用户-编辑", notes = "coremail用户-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CoreMailUser coreMailUser) {
		coreMailUserService.updateById(coreMailUser);
		return Result.ok("编辑成功!");
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
		coreMailUserService.removeById(id);
		return Result.ok("删除成功!");
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
		this.coreMailUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
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
