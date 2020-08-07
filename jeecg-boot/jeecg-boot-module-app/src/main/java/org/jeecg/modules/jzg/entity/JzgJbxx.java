package org.jeecg.modules.jzg.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 教职工源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Data
@TableName("t_jzg_jbxx")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="t_jzg_jbxx对象", description="教职工源表")
public class JzgJbxx implements Serializable {
    private static final long serialVersionUID = 1L;

	/**学工号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "学工号")
    private java.lang.String id;
	/**姓名*/
	@Excel(name = "姓名", width = 128)
    @ApiModelProperty(value = "姓名")
    private java.lang.String xm;
	/**性别代码*/
	@Excel(name = "性别代码", width = 32)
    @ApiModelProperty(value = "性别代码")
    private java.lang.String xbdm;
	/**所在单位代码*/
	@Excel(name = "所在单位代码", width = 32)
    @ApiModelProperty(value = "所在单位代码")
    private java.lang.String szdwdm;
	/**所在单位名称*/
	@Excel(name = "所在单位名称", width = 32)
    @ApiModelProperty(value = "所在单位名称")
    private java.lang.String szdwmc;
	/**身份证件号*/
	@Excel(name = "身份证件号", width = 32)
    @ApiModelProperty(value = "身份证件号")
    private java.lang.String sfzjh;
	/**手机号码*/
	@Excel(name = "手机号码", width = 32)
    @ApiModelProperty(value = "手机号码")
    private java.lang.String sjhm;
	/**当前状态代码*/
	@Excel(name = "当前状态代码", width = 32)
    @ApiModelProperty(value = "当前状态代码")
    private java.lang.String dqztdm;
	/**用人方式代码*/
	@Excel(name = "用人方式代码", width = 32)
    @ApiModelProperty(value = "用人方式代码")
    private java.lang.String yrfsdm;
}
