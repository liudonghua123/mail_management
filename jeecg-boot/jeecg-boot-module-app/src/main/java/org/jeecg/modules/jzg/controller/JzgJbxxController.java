package org.jeecg.modules.jzg.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.jzg.entity.JzgJbxx;
import org.jeecg.modules.jzg.service.IJzgJbxxService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 教职工源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Api(tags="教职工源表")
@RestController
@RequestMapping("/jzg/jzgJbxx")
@Slf4j
public class JzgJbxxController extends JeecgController<JzgJbxx, IJzgJbxxService> {
	@Autowired
	private IJzgJbxxService jzgJbxxService;
	
	/**
	 * 分页列表查询
	 *
	 * @param jzgJbxx
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "教职工源表-分页列表查询")
	@ApiOperation(value="教职工源表-分页列表查询", notes="教职工源表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(JzgJbxx jzgJbxx,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<JzgJbxx> queryWrapper = QueryGenerator.initQueryWrapper(jzgJbxx, req.getParameterMap());
		Page<JzgJbxx> page = new Page<JzgJbxx>(pageNo, pageSize);
		IPage<JzgJbxx> pageList = jzgJbxxService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param jzgJbxx
	 * @return
	 */
	@AutoLog(value = "教职工源表-添加")
	@ApiOperation(value="教职工源表-添加", notes="教职工源表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody JzgJbxx jzgJbxx) {
		jzgJbxxService.save(jzgJbxx);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param jzgJbxx
	 * @return
	 */
	@AutoLog(value = "教职工源表-编辑")
	@ApiOperation(value="教职工源表-编辑", notes="教职工源表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody JzgJbxx jzgJbxx) {
		jzgJbxxService.updateById(jzgJbxx);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "教职工源表-通过id删除")
	@ApiOperation(value="教职工源表-通过id删除", notes="教职工源表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		jzgJbxxService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "教职工源表-批量删除")
	@ApiOperation(value="教职工源表-批量删除", notes="教职工源表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.jzgJbxxService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "教职工源表-通过id查询")
	@ApiOperation(value="教职工源表-通过id查询", notes="教职工源表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		JzgJbxx jzgJbxx = jzgJbxxService.getById(id);
		if(jzgJbxx==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(jzgJbxx);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param jzgJbxx
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JzgJbxx jzgJbxx) {
        return super.exportXls(request, jzgJbxx, JzgJbxx.class, "教职工源表");
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
        return super.importExcel(request, response, JzgJbxx.class);
    }

}
