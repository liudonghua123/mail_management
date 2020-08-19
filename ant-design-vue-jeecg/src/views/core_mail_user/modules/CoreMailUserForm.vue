<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container>
      <a-form :form="form" slot="detail" :disabled="formDisabled">
        <a-collapse default-active-key="1" :disabled="false">
          <a-collapse-panel key="1" header="基本信息">
            <a-row>
              <a-col :span="12">
                <a-form-item label="账号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    :disabled="editMode"
                    v-decorator="['id']"
                    placeholder="请输入邮箱账号，例如 lily@ynu.edu.cn"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="密码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-col :span="16">
                    <a-input v-decorator="['password']" placeholder="请输入密码"></a-input>
                  </a-col>
                  <a-col :offset="2" :span="6">
                    <a-button type="primary" @click="extractPassword">设置初始密码</a-button>
                  </a-col>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="用户状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="radio"
                    v-decorator="['userStatus']"
                    :trigger-change="true"
                    dictCode="coremail_user_status"
                    placeholder="请选择用户状态"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="域名" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    disabled
                    type="list"
                    v-decorator="['domainName']"
                    :trigger-change="true"
                    dictCode="coremail_domain_name"
                    placeholder="请选择域名"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="用户过期日" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-date
                    placeholder="请选择用户过期日"
                    v-decorator="['userExpiryDate']"
                    :trigger-change="true"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱组织" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="list"
                    v-decorator="['orgId']"
                    :trigger-change="true"
                    dictCode="coremail_org_names"
                    placeholder="请选择邮箱组织"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱部门" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="list"
                    v-decorator="['orgUnitId']"
                    :trigger-change="true"
                    dictCode="coremail_org_names"
                    placeholder="请选择邮箱部门"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="真实姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['trueName']" placeholder="请输入真实姓名"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="昵称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['nickName']" placeholder="请输入昵称"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="手机号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['mobileNumber']" placeholder="请输入手机号码"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['alias']" placeholder="请输入别名"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="身份证号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['address']" placeholder="请输入身份证号"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="更多信息">
            <a-row>
              <a-col :span="12">
                <a-form-item label="工号(学号)" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['zipcode']" placeholder="请输入工号(学号)"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="学院(及专业)" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['homepage']" placeholder="请输入学院(及专业)"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="其它email地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['altEmail']" placeholder="请输入其它email地址"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="忘记密码问题" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['pwdHintQuestion']" placeholder="请输入忘记密码问题"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="忘记密码答案" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['pwdHintAnswer']" placeholder="请输入忘记密码答案"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="家庭电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['homePhone']" placeholder="请输入家庭电话"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="公司电话" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['companyPhone']" placeholder="请输入公司电话"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="传真号码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['faxNumber']" placeholder="请输入传真号码"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="radioButton"
                    v-decorator="['gender']"
                    :trigger-change="true"
                    dictCode="sex"
                    placeholder="请选择性别"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="省份" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['province']" placeholder="请输入省份"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="城市" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['city']" placeholder="请输入城市"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="生日" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-date
                    placeholder="请选择生日"
                    v-decorator="['birthday']"
                    :trigger-change="true"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <a-collapse-panel key="3" header="其他信息">
            <a-row>
              <a-col :span="12">
                <a-form-item label="服务等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['cosId']"
                    placeholder="请输入服务等级"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="用户列表排序" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['userListRank']"
                    placeholder="请输入用户列表排序"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="用户附加容量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['quotaDelta']"
                    placeholder="请输入用户附加容量"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="网盘附加容量" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['nfQuotaDelta']"
                    placeholder="请输入网盘附加容量"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="自动转发地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['forwarddes']" placeholder="请输入自动转发地址"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="自动转发功能是否生效" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="radio"
                    v-decorator="['forwardactive']"
                    :trigger-change="true"
                    dictCode="yn"
                    placeholder="请选择自动转发功能是否生效"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="自动转发是否在本站保存" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <j-dict-select-tag
                    type="radio"
                    v-decorator="['keeplocal']"
                    :trigger-change="true"
                    dictCode="yn"
                    placeholder="请选择自动转发是否在本站保存"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="时区" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['timeZone']"
                    placeholder="请输入时区"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="删除邮件后的操作" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input-number
                    v-decorator="['afterdel']"
                    placeholder="请输入删除邮件后的操作"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="国家/地区" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['country']" placeholder="请输入国家/地区"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="周年纪念日" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['anniversary']" placeholder="请输入周年纪念日"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="注册IP" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['regIp']" placeholder="请输入注册IP"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="职位" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['duty']" placeholder="请输入职位"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['remarks']" placeholder="请输入备注"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="完整部门路径" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['orgUnitFullname']" placeholder="请输入完整部门路径"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="IP绑定" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input v-decorator="['loginIpRange']" placeholder="请输入IP绑定"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
          </a-collapse-panel>
        </a-collapse>
        <a-row>
          <a-col v-if="showFlowSubmitButton" :span="24" style="text-align: center">
            <a-button @click="submitForm">提 交</a-button>
          </a-col>
        </a-row>
      </a-form>
    </j-form-container>
  </a-spin>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import pick from 'lodash.pick'
import { validateDuplicateValue } from '@/utils/util'
import JFormContainer from '@/components/jeecg/JFormContainer'
import JDate from '@/components/jeecg/JDate'
import JDictSelectTag from '@/components/dict/JDictSelectTag'

export default {
  name: 'CoreMailUserForm',
  components: {
    JFormContainer,
    JDate,
    JDictSelectTag,
  },
  props: {
    //流程表单data
    formData: {
      type: Object,
      default: () => {},
      required: false,
    },
    //表单模式：true流程表单 false普通表单
    formBpm: {
      type: Boolean,
      default: false,
      required: false,
    },
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false,
    },
  },
  data() {
    return {
      form: this.$form.createForm(this),
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      validatorRules: {},
      url: {
        add: '/core_mail_user/coreMailUser/add',
        edit: '/core_mail_user/coreMailUser/edit',
        queryById: '/core_mail_user/coreMailUser/queryById',
      },
      editMode: true,
    }
  },
  computed: {
    formDisabled() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return false
        }
        return true
      }
      return this.disabled
    },
    showFlowSubmitButton() {
      if (this.formBpm === true) {
        if (this.formData.disabled === false) {
          return true
        }
      }
      return false
    },
  },
  created() {
    //如果是流程中表单，则需要加载流程表单data
    this.showFlowData()
  },
  methods: {
    extractPassword() {
      const cardIdNum = this.form.getFieldValue('address') || ''
      if(!cardIdNum) {
        return this.$message.error(`请输入证件号!`)
      }
      if(cardIdNum.length < 6) {
        return this.$message.warn(`证件号不正确!`)
      }
      this.form.setFieldsValue({
        password: cardIdNum.substr(-6).toLowerCase()
      })
      this.$message.success(`密码已设置为证件号小写后六位!`)
    },
    add() {
      this.edit({})
      this.editMode = false
    },
    edit(record) {
      this.editMode = true
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          ...pick(
            this.model,
            'id',
            'cosId',
            'userListRank',
            'userStatus',
            'domainName',
            'quotaDelta',
            'nfQuotaDelta',
            'userExpiryDate',
            'orgId',
            'orgUnitId',
            'password',
            'forwarddes',
            'forwardactive',
            'keeplocal',
            'timeZone',
            'afterdel',
            'altEmail',
            'pwdHintQuestion',
            'pwdHintAnswer',
            'trueName',
            'nickName',
            'mobileNumber',
            'homePhone',
            'companyPhone',
            'faxNumber',
            'gender',
            'province',
            'city',
            'birthday',
            'address',
            'zipcode',
            'homepage',
            'country',
            'anniversary',
            'regIp',
            'duty',
            'remarks',
            'orgUnitFullname',
            'loginIpRange',
            'alias',
            'username'
          ),
          password: '',
        })
      })
    },
    //渲染流程表单数据
    showFlowData() {
      if (this.formBpm === true) {
        let params = { id: this.formData.dataId }
        getAction(this.url.queryById, params).then((res) => {
          if (res.success) {
            this.edit(res.result)
          }
        })
      }
    },
    submitForm() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          let formData = Object.assign(this.model, values)
          console.log('表单提交数据', formData)
          httpAction(httpurl, formData, method)
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    },
    popupCallback(row) {
      this.form.setFieldsValue(
        pick(
          row,
          'id',
          'cosId',
          'userListRank',
          'userStatus',
          'domainName',
          'quotaDelta',
          'nfQuotaDelta',
          'userExpiryDate',
          'orgId',
          'orgUnitId',
          'password',
          'forwarddes',
          'forwardactive',
          'keeplocal',
          'timeZone',
          'afterdel',
          'altEmail',
          'pwdHintQuestion',
          'pwdHintAnswer',
          'trueName',
          'nickName',
          'mobileNumber',
          'homePhone',
          'companyPhone',
          'faxNumber',
          'gender',
          'province',
          'city',
          'birthday',
          'address',
          'zipcode',
          'homepage',
          'country',
          'anniversary',
          'regIp',
          'duty',
          'remarks',
          'orgUnitFullname',
          'loginIpRange',
          'alias',
          'username'
        )
      )
    },
  },
}
</script>