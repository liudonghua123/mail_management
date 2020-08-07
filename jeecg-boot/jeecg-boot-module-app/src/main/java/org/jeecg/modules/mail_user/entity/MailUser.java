package org.jeecg.modules.mail_user.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 邮箱用户
 * @Author: jeecg-boot
 * @Date:   2020-08-05
 * @Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mail_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="mail_user对象", description="邮箱用户")
public class MailUser implements Serializable {
    private static final long serialVersionUID = 1L;

	/**学工号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "学工号")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**姓名*/
	@Excel(name = "姓名", width = 128)
    @ApiModelProperty(value = "姓名")
    private java.lang.String name;
	/**性别*/
	@Excel(name = "性别", width = 32, dicCode = "sex")
	@Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private java.lang.String gender;
	/**部门名称*/
	@Excel(name = "部门名称", width = 32)
    @ApiModelProperty(value = "部门名称")
    private java.lang.String depName;
	/**证件号*/
	@Excel(name = "证件号", width = 32)
    @ApiModelProperty(value = "证件号")
    private java.lang.String cardIdNum;
	/**手机号*/
	@Excel(name = "手机号", width = 32)
    @ApiModelProperty(value = "手机号")
    private java.lang.String phone;
	/**身份类型*/
	@Excel(name = "身份类型", width = 32, dicCode = "user_type")
	@Dict(dicCode = "user_type")
    @ApiModelProperty(value = "身份类型")
    private java.lang.String type;
	/**账号是否创建*/
	@Excel(name = "账号是否创建", width = 32, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "账号是否创建")
    private java.lang.String mailCreated;
}
