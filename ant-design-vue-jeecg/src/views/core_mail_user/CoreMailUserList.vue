<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="账号名称">
              <a-input placeholder="请输入账号名称" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="账号别名">
              <a-input placeholder="请输入账号别名" v-model="queryParam.alias"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="用户状态">
              <j-dict-select-tag
                placeholder="请选择用户状态"
                v-model="queryParam.userStatus"
                dictCode="coremail_user_status"
              />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="域名">
              <j-dict-select-tag
                placeholder="请选择域名"
                v-model="queryParam.domainName"
                dictCode="coremail_domain_name"
              />
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="组织">
                <j-dict-select-tag
                  placeholder="请选择组织"
                  v-model="queryParam.orgId"
                  dictCode="coremail_org_names"
                />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="部门">
                <j-dict-select-tag
                  placeholder="请选择部门"
                  v-model="queryParam.orgUnitId"
                  dictCode="coremail_org_names"
                />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="真实姓名">
                <a-input placeholder="请输入真实姓名" v-model="queryParam.trueName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="昵称">
                <a-input placeholder="请输入昵称" v-model="queryParam.nickName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="手机号码">
                <a-input placeholder="请输入手机号码" v-model="queryParam.mobileNumber"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="性别">
                <a-input placeholder="请输入性别" v-model="queryParam.gender"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="身份证号">
                <a-input placeholder="请输入身份证号" v-model="queryParam.address"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="工号(学号)">
                <a-input placeholder="请输入工号(学号)" v-model="queryParam.zipcode"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="学院(及专业)">
                <a-input placeholder="请输入学院(及专业)" v-model="queryParam.homepage"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button
                type="primary"
                @click="searchReset"
                icon="reload"
                style="margin-left: 8px"
              >重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('coremail用户')">导出</a-button>
      <a-upload
        name="file"
        :showUploadList="false"
        :multiple="false"
        :headers="tokenHeader"
        :action="importExcelUrl"
        @change="handleImportExcel"
      >
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button type="primary" icon="sync" @click="handleSync">同步coremail数据</a-button>
      <a-button type="primary" @click="handleUserExist">验证账号是否存在</a-button>
      <a-button type="primary" @click="handleAuthenticate">验证邮箱账号密码</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />删除
          </a-menu-item>
          <a-menu-item key="1" @click="batchChangeAttr('user_status=0')">
            <a-icon type="update" />启用
          </a-menu-item>
          <a-menu-item key="1" @click="batchChangeAttr('user_status=1')">
            <a-icon type="update" />停用
          </a-menu-item>
          <a-menu-item key="1" @click="batchChangeAttr('user_status=4')">
            <a-icon type="update" />锁定
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a-popover
          title="自定义列"
          trigger="click"
          placement="leftBottom"
          style="float: right;margin: auto 10px;"
        >
          <template slot="content">
            <a-checkbox-group
              @change="onColSettingsChange"
              v-model="settingColumns"
              :defaultValue="settingColumns"
            >
              <a-row>
                <template v-for="(item,index) in defColumns">
                  <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                    <a-col :span="12" :key="index">
                      <a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox>
                    </a-col>
                  </template>
                </template>
              </a-row>
            </a-checkbox-group>
          </template>
          <a>
            <a-icon type="setting" />自定义列
          </a>
        </a-popover>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img
            v-else
            :src="getImgView(text)"
            height="25px"
            alt
            style="max-width:80px;font-size: 12px;font-style: italic;"
          />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)"
          >下载</a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">
              更多
              <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleAuthenticate(record)">验证密码</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleGetAttrs(record)">属性信息</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>

    <core-mail-user-modal ref="modalForm" @ok="modalFormOk"></core-mail-user-modal>

    <!-- 验证邮箱账号密码对话框 -->
    <a-modal v-model="authenticateModalVisible" title="验证邮箱账号密码" @ok="handleAuthenticateModalOk">
      <a-form-model
        ref="authenticateModal"
        :model="authenticateModalForm"
        :rules="authenticateModalRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-model-item label="邮箱账号" prop="authenticateUsername">
          <a-input v-model="authenticateModalForm.authenticateUsername" />
        </a-form-model-item>
        <a-form-model-item label="邮箱密码" prop="authenticatePassword">
          <a-input v-model="authenticateModalForm.authenticatePassword" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <!-- 验证邮箱账号是否存在对话框 -->
    <a-modal v-model="userExistModalVisible" title="验证邮箱账号是否存在" @ok="handleUserExistModalOk">
      <a-form-model
        ref="userExistModal"
        :model="userExistModalForm"
        :rules="userExistModalRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-model-item label="邮箱账号" prop="userExistUsername">
          <a-input v-model="userExistModalForm.userExistUsername" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-card>
</template>

<script>
import Vue from 'vue'
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import CoreMailUserModal from './modules/CoreMailUserModal'
import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
import { filterMultiDictText } from '@/components/dict/JDictSelectUtil'
import { getAction, postAction, putAction, deleteAction, downFile, getFileAccessHttpUrl } from '@/api/manage'

export default {
  name: 'CoreMailUserList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    JDictSelectTag,
    CoreMailUserModal,
  },
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!/\S+@\S+\.\S+/.test(value)) {
        callback(new Error('电子邮件账号格式不正确！'))
      }
      callback()
    }
    return {
      description: 'coremail用户管理页面',
      //表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function (t, r, index) {
            return parseInt(index) + 1
          },
        },
        {
          title: '账号',
          align: 'center',
          dataIndex: 'id',
        },
        {
          title: '服务等级',
          align: 'center',
          dataIndex: 'cosId',
        },
        {
          title: '用户状态',
          align: 'center',
          dataIndex: 'userStatus_dictText',
        },
        {
          title: '域名',
          align: 'center',
          dataIndex: 'domainName_dictText',
        },
        {
          title: '真实姓名',
          align: 'center',
          dataIndex: 'trueName',
        },
        {
          title: '昵称',
          align: 'center',
          dataIndex: 'nickName',
        },
        {
          title: '手机号码',
          align: 'center',
          dataIndex: 'mobileNumber',
        },
        {
          title: '身份证号',
          align: 'center',
          dataIndex: 'address',
        },
        {
          title: '工号(学号)',
          align: 'center',
          dataIndex: 'zipcode',
        },
        {
          title: '学院(及专业)',
          align: 'center',
          dataIndex: 'homepage',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        },
      ],
      //列设置
      settingColumns: [],
      // 表头
      defColumns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function (t, r, index) {
            return parseInt(index) + 1
          },
        },
        {
          title: '账号',
          align: 'center',
          dataIndex: 'id',
        },
        {
          title: '用户状态',
          align: 'center',
          dataIndex: 'userStatus_dictText',
        },
        {
          title: '域名',
          align: 'center',
          dataIndex: 'domainName_dictText',
        },
        {
          title: '用户附加容量',
          align: 'center',
          dataIndex: 'quotaDelta',
        },
        {
          title: '网盘附加容量',
          align: 'center',
          dataIndex: 'nfQuotaDelta',
        },
        {
          title: '用户过期日',
          align: 'center',
          dataIndex: 'userExpiryDate',
          customRender: function (text) {
            return !text ? '' : text.length > 10 ? text.substr(0, 10) : text
          },
        },
        {
          title: '邮箱组织',
          align: 'center',
          dataIndex: 'orgId_dictText',
        },
        {
          title: '邮箱部门',
          align: 'center',
          dataIndex: 'orgUnitId_dictText',
        },
        {
          title: '服务等级',
          align: 'center',
          dataIndex: 'cosId',
        },
        {
          title: '用户列表排序',
          align: 'center',
          dataIndex: 'userListRank',
        },
        {
          title: '密码',
          align: 'center',
          dataIndex: 'password',
        },
        {
          title: '自动转发地址',
          align: 'center',
          dataIndex: 'forwarddes',
        },
        {
          title: '自动转发功能是否生效',
          align: 'center',
          dataIndex: 'forwardactive_dictText',
        },
        {
          title: '自动转发是否在本站保存',
          align: 'center',
          dataIndex: 'keeplocal_dictText',
        },
        {
          title: '时区',
          align: 'center',
          dataIndex: 'timeZone',
        },
        {
          title: '删除邮件后的操作',
          align: 'center',
          dataIndex: 'afterdel',
        },
        {
          title: '其它email地址',
          align: 'center',
          dataIndex: 'altEmail',
        },
        {
          title: '忘记密码问题',
          align: 'center',
          dataIndex: 'pwdHintQuestion',
        },
        {
          title: '忘记密码答案',
          align: 'center',
          dataIndex: 'pwdHintAnswer',
        },
        {
          title: '真实姓名',
          align: 'center',
          dataIndex: 'trueName',
        },
        {
          title: '昵称',
          align: 'center',
          dataIndex: 'nickName',
        },
        {
          title: '手机号码',
          align: 'center',
          dataIndex: 'mobileNumber',
        },
        {
          title: '家庭电话',
          align: 'center',
          dataIndex: 'homePhone',
        },
        {
          title: '公司电话',
          align: 'center',
          dataIndex: 'companyPhone',
        },
        {
          title: '传真号码',
          align: 'center',
          dataIndex: 'faxNumber',
        },
        {
          title: '性别',
          align: 'center',
          dataIndex: 'gender',
        },
        {
          title: '省份',
          align: 'center',
          dataIndex: 'province',
        },
        {
          title: '城市',
          align: 'center',
          dataIndex: 'city',
        },
        {
          title: '生日',
          align: 'center',
          dataIndex: 'birthday',
          customRender: function (text) {
            return !text ? '' : text.length > 10 ? text.substr(0, 10) : text
          },
        },
        {
          title: '身份证号',
          align: 'center',
          dataIndex: 'address',
        },
        {
          title: '工号(学号)',
          align: 'center',
          dataIndex: 'zipcode',
        },
        {
          title: '学院(及专业)',
          align: 'center',
          dataIndex: 'homepage',
        },
        {
          title: '国家/地区',
          align: 'center',
          dataIndex: 'country',
        },
        {
          title: '周年纪念日',
          align: 'center',
          dataIndex: 'anniversary',
        },
        {
          title: '注册IP',
          align: 'center',
          dataIndex: 'regIp',
        },
        {
          title: '职位',
          align: 'center',
          dataIndex: 'duty',
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remarks',
        },
        {
          title: '完整部门路径',
          align: 'center',
          dataIndex: 'orgUnitFullname',
        },
        {
          title: 'IP绑定',
          align: 'center',
          dataIndex: 'loginIpRange',
        },
        {
          title: '别名',
          align: 'center',
          dataIndex: 'alias',
        },
        {
          title: '邮箱账号',
          align: 'center',
          dataIndex: 'username',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        },
      ],
      url: {
        list: '/core_mail_user/coreMailUser/list',
        delete: '/core_mail_user/coreMailUser/delete',
        deleteBatch: '/core_mail_user/coreMailUser/deleteBatch',
        exportXlsUrl: '/core_mail_user/coreMailUser/exportXls',
        importExcelUrl: 'core_mail_user/coreMailUser/importExcel',
        syncUrl: 'core_mail_user/coreMailUser/sync',
        updateCoreMailUserBatch: 'core_mail_user/coreMailUser/updateCoreMailUserBatch',
        userExistUrl: 'core_mail_user/coreMailUser/userExist',
        authenticateUrl: 'core_mail_user/coreMailUser/authenticate',
        getAttrsUrl: 'core_mail_user/coreMailUser/getAttrs',
      },
      dictOptions: {},
      authenticateModalVisible: false,
      authenticateModalForm: {
        authenticateUsername: '',
        authenticatePassword: '',
      },
      authenticateModalRules: {
        authenticateUsername: [
          { required: true, message: '请输入完整的邮箱账号', trigger: 'blur' },
          { min: '@ynu.edu.cn'.length, message: '邮箱账号必须完整', trigger: 'blur' },
          { validator: validateEmail, trigger: 'blur' },
        ],
        authenticatePassword: [{ required: true, message: '请输入邮箱账号的待验证密码', trigger: 'blur' }],
      },
      userExistModalVisible: false,
      userExistModalForm: {
        userExistUsername: '',
      },
      userExistModalRules: {
        userExistUsername: [
          { required: true, message: '请输入完整的邮箱账号', trigger: 'blur' },
          { min: '@ynu.edu.cn'.length, message: '邮箱账号必须完整', trigger: 'blur' },
          { validator: validateEmail, trigger: 'blur' },
        ],
      },
    }
  },
  created() {
    this.initColumns()
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
    handleSync() {
      this.loading = true
      getAction(this.url.syncUrl).then((res) => {
        this.loading = false
        if (res.success) {
          this.$message.success(res.message)
          this.loadData()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    handleAuthenticate({ id = 'xxx@ynu.edu.cn', address = '' }) {
      this.authenticateModalForm.authenticateUsername = id
      this.authenticateModalForm.authenticatePassword = address.substr(-6)
      this.authenticateModalVisible = true
    },
    handleUserExist({ id = 'xxx@ynu.edu.cn' }) {
      this.userExistModalForm.userExistUsername = id
      this.userExistModalVisible = true
    },
    handleGetAttrs({ id: userAtDomain = 'xxx@ynu.edu.cn' }) {
      getAction(this.url.getAttrsUrl, {
        userAtDomain,
      }).then((res) => {
        this.loading = false
        if (!res.success) {
          return this.$message.error(`获取账号 ${userAtDomain} 属性信息失败, ${res.message}`)
        }
        // return this.$message.success(`获取账号 ${userAtDomain} 属性信息:\n${JSON.stringify(res.result, null, 2)}`)
        this.$notification.info({
          message: `账号 ${userAtDomain} 属性信息`,
          description: <pre>{JSON.stringify(res.result, null, 2)}</pre>,
          icon: <a-icon type="smile" style="color: #108ee9" />,
        })
      })
    },
    handleAuthenticateModalOk() {
      this.$refs.authenticateModal.validate((valid) => {
        if (!valid) {
          return this.$message.error(`请输入正确的邮箱账号及密码`)
          return false
        }
        this.loading = true
        const { authenticateUsername: userAtDomain, authenticatePassword: password } = this.authenticateModalForm
        getAction(this.url.authenticateUrl, {
          userAtDomain,
          password,
        }).then((res) => {
          this.loading = false
          if (!res.success) {
            return this.$message.error(`验证账号 ${userAtDomain} 密码错误, 返回值是 ${res.message}`)
          }
          if (res.result != 0) {
            return this.$message.success(`验证账号 ${userAtDomain} 密码不正确, 返回值是 ${res.result}`)
          }
          return this.$message.success(`验证账号 ${userAtDomain} 密码正确`)
        })
      })
    },
    handleUserExistModalOk() {
      this.$refs.userExistModal.validate((valid) => {
        if (!valid) {
          return this.$message.error(`请输入正确的邮箱账号`)
          return false
        }
        this.loading = true
        const { userExistUsername: userAtDomain } = this.userExistModalForm
        getAction(this.url.userExistUrl, {
          userAtDomain,
        }).then((res) => {
          this.loading = false
          if (!res.success) {
            return this.$message.error(`验证账号 ${userAtDomain} 是否存在错误, 返回值是 ${res.message}`)
          }
          if (res.result != 0) {
            return this.$message.success(`验证账号 ${userAtDomain} 不存在, 返回值是 ${res.result}`)
          }
          return this.$message.success(`验证账号 ${userAtDomain} 存在`)
        })
      })
    },
    initDictConfig() {},
    batchChangeAttr: function (attrs) {
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！')
        return
      } else {
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var that = this
        this.$confirm({
          title: '确认修改用户状态',
          content: '是否修改选中数据的用户状态?',
          onOk: function () {
            that.loading = true
            putAction(that.url.updateCoreMailUserBatch, null, { userAtDomains: ids, attrs })
              .then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.loadData()
                  that.onClearSelected()
                } else {
                  that.$message.warning(res.message)
                }
              })
              .finally(() => {
                that.loading = false
              })
          },
        })
      }
    },
  },
}
</script>
<style scoped>
@import '~@assets/less/common.less';

.table-operator > .ant-btn-danger {
  color: #fff;
  background-color: #ff4d4f;
  border-color: #ff4d4f;
  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.12);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.045);
}

.table-operator .ant-btn-background-ghost.ant-btn-danger {
  color: #ff4d4f;
  background-color: transparent;
  border-color: #ff4d4f;
  text-shadow: none;
}
</style>