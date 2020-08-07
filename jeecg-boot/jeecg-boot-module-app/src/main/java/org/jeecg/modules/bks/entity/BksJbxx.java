package org.jeecg.modules.bks.entity;

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
 * @Description: 本科生源表
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Data
@TableName("t_bks_jbxx")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="t_bks_jbxx对象", description="本科生源表")
public class BksJbxx implements Serializable {
    private static final long serialVersionUID = 1L;

	/**学工号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "学工号")
    private java.lang.String id;
	/**姓名*/
	@Excel(name = "姓名", width = 128)
    @ApiModelProperty(value = "姓名")
    private java.lang.String xm;
	/**性别*/
	@Excel(name = "性别", width = 32, dicCode = "sex")
	@Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String xbdm;
	/**院系代码*/
	@Excel(name = "院系代码", width = 32)
    @ApiModelProperty(value = "院系代码")
    private java.lang.String yxdm;
	/**院系名称*/
	@Excel(name = "院系名称", width = 32)
    @ApiModelProperty(value = "院系名称")
    private java.lang.String yxmc;
	/**专业代码*/
	@Excel(name = "专业代码", width = 32)
    @ApiModelProperty(value = "专业代码")
    private java.lang.String zydm;
	/**身份证件号*/
	@Excel(name = "身份证件号", width = 32)
    @ApiModelProperty(value = "身份证件号")
    private java.lang.String sfzjh;
	/**手机号码*/
	@Excel(name = "手机号码", width = 32)
    @ApiModelProperty(value = "手机号码")
    private java.lang.String sjhm;
	/**是否在校*/
	@Excel(name = "是否在校", width = 32, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否在校")
    private java.lang.String sfzx;
}
