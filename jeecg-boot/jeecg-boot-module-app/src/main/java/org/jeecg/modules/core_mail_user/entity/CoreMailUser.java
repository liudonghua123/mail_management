package org.jeecg.modules.core_mail_user.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: coremail用户
 * @Author: jeecg-boot
 * @Date:   2020-08-11
 * @Version: V1.0
 */
@Data
@TableName("core_mail_user")
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiModel(value="core_mail_user对象", description="coremail用户")
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class CoreMailUser implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @EqualsAndHashCode.Include
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    // @JsonProperty(access = Access.WRITE_ONLY)
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    // @JsonProperty(access = Access.WRITE_ONLY)
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    // @JsonProperty(access = Access.WRITE_ONLY)
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    // @JsonProperty(access = Access.WRITE_ONLY)
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @JsonProperty(access = Access.WRITE_ONLY)
    private java.lang.String sysOrgCode;
	/**服务等级*/
	@Excel(name = "服务等级", width = 15)
    @ApiModelProperty(value = "服务等级")
    private java.lang.Integer cosId;
	/**用户列表排序*/
	@Excel(name = "用户列表排序", width = 15)
    @ApiModelProperty(value = "用户列表排序")
    private java.lang.Integer userListRank;
	/**用户状态*/
	@Excel(name = "用户状态", width = 15, dicCode = "coremail_user_status")
	@Dict(dicCode = "coremail_user_status")
    @ApiModelProperty(value = "用户状态")
    private java.lang.Integer userStatus;
	/**域名*/
	@Excel(name = "域名", width = 15, dicCode = "coremail_domain_name")
	@Dict(dicCode = "coremail_domain_name")
    @ApiModelProperty(value = "域名")
    private java.lang.String domainName;
	/**用户附加容量*/
	@Excel(name = "用户附加容量", width = 15)
    @ApiModelProperty(value = "用户附加容量")
    private java.lang.Integer quotaDelta;
	/**网盘附加容量*/
	@Excel(name = "网盘附加容量", width = 15)
    @ApiModelProperty(value = "网盘附加容量")
    private java.lang.Integer nfQuotaDelta;
	/**用户过期日*/
	@Excel(name = "用户过期日", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd",shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "用户过期日")
    private java.util.Date userExpiryDate;
	/**用户部门id*/
	@Excel(name = "用户部门id", width = 15)
    @ApiModelProperty(value = "用户部门id")
    private java.lang.String orgUnitId;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private java.lang.String password;
	/**自动转发地址*/
	@Excel(name = "自动转发地址", width = 15)
    @ApiModelProperty(value = "自动转发地址")
    private java.lang.String forwarddes;
	/**自动转发功能是否生效*/
	@Excel(name = "自动转发功能是否生效", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "自动转发功能是否生效")
    private java.lang.Integer forwardactive;
	/**自动转发是否在本站保存*/
	@Excel(name = "自动转发是否在本站保存", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "自动转发是否在本站保存")
    private java.lang.Integer keeplocal;
	/**时区*/
	@Excel(name = "时区", width = 15)
    @ApiModelProperty(value = "时区")
    private java.lang.String timeZone;
	/**删除邮件后的操作*/
	@Excel(name = "删除邮件后的操作", width = 15)
    @ApiModelProperty(value = "删除邮件后的操作")
    private java.lang.Integer afterdel;
	/**其它email地址*/
	@Excel(name = "其它email地址", width = 15)
    @ApiModelProperty(value = "其它email地址")
    private java.lang.String altEmail;
	/**忘记密码问题*/
	@Excel(name = "忘记密码问题", width = 15)
    @ApiModelProperty(value = "忘记密码问题")
    private java.lang.String pwdHintQuestion;
	/**忘记密码答案*/
	@Excel(name = "忘记密码答案", width = 15)
    @ApiModelProperty(value = "忘记密码答案")
    private java.lang.String pwdHintAnswer;
	/**真实姓名*/
	@Excel(name = "真实姓名", width = 15)
    @ApiModelProperty(value = "真实姓名")
    private java.lang.String trueName;
	/**昵称*/
	@Excel(name = "昵称", width = 15)
    @ApiModelProperty(value = "昵称")
    private java.lang.String nickName;
	/**手机号码*/
	@Excel(name = "手机号码", width = 15)
    @ApiModelProperty(value = "手机号码")
    private java.lang.String mobileNumber;
	/**家庭电话*/
	@Excel(name = "家庭电话", width = 15)
    @ApiModelProperty(value = "家庭电话")
    private java.lang.String homePhone;
	/**公司电话*/
	@Excel(name = "公司电话", width = 15)
    @ApiModelProperty(value = "公司电话")
    private java.lang.String companyPhone;
	/**传真号码*/
	@Excel(name = "传真号码", width = 15)
    @ApiModelProperty(value = "传真号码")
    private java.lang.String faxNumber;
	/**性别*/
	@Excel(name = "性别", width = 15)
    @ApiModelProperty(value = "性别")
    private java.lang.Integer gender;
	/**省份*/
	@Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
    private java.lang.String province;
	/**城市*/
	@Excel(name = "城市", width = 15)
    @ApiModelProperty(value = "城市")
    private java.lang.String city;
	/**生日*/
	@Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd",shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
    private java.util.Date birthday;
	/**身份证号*/
	@Excel(name = "身份证号", width = 15)
    @ApiModelProperty(value = "身份证号")
    private java.lang.String address;
	/**工号(学号)*/
	@Excel(name = "工号(学号)", width = 15)
    @ApiModelProperty(value = "工号(学号)")
    private java.lang.String zipcode;
	/**学院(及专业)*/
	@Excel(name = "学院(及专业)", width = 15)
    @ApiModelProperty(value = "学院(及专业)")
    private java.lang.String homepage;
	/**国家/地区*/
	@Excel(name = "国家/地区", width = 15)
    @ApiModelProperty(value = "国家/地区")
    private java.lang.String country;
	/**周年纪念日*/
	@Excel(name = "周年纪念日", width = 15)
    @ApiModelProperty(value = "周年纪念日")
    private java.lang.String anniversary;
	/**注册IP*/
	@Excel(name = "注册IP", width = 15)
    @ApiModelProperty(value = "注册IP")
    private java.lang.String regIp;
	/**职位*/
	@Excel(name = "职位", width = 15)
    @ApiModelProperty(value = "职位")
    private java.lang.String duty;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
	/**完整部门路径*/
	@Excel(name = "完整部门路径", width = 15)
    @ApiModelProperty(value = "完整部门路径")
    private java.lang.String orgUnitFullname;
	/**IP绑定*/
	@Excel(name = "IP绑定", width = 15)
    @ApiModelProperty(value = "IP绑定")
    private java.lang.String loginIpRange;

    
}
