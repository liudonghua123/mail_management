<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="学工号">
              <a-input placeholder="请输入学工号" v-model="queryParam.id"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="姓名">
              <a-input placeholder="请输入姓名" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="性别">
                <j-dict-select-tag placeholder="请选择性别" v-model="queryParam.gender" dictCode="sex" />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="部门名称">
                <a-input placeholder="请输入部门名称" v-model="queryParam.depName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="证件号">
                <a-input placeholder="请输入证件号" v-model="queryParam.cardIdNum"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="手机号">
                <a-input placeholder="请输入手机号" v-model="queryParam.phone"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="身份类型">
                <j-dict-select-tag
                  placeholder="请选择身份类型"
                  v-model="queryParam.type"
                  dictCode="user_type"
                />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="账号是否创建">
                <j-dict-select-tag
                  placeholder="请选择账号是否创建"
                  v-model="queryParam.mailCreated"
                  dictCode="yn"
                />
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
      <a-button type="primary" icon="download" @click="handleExportXls('邮箱用户')">导出</a-button>
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
      <a-button type="danger" icon="sync" @click="handleSync">同步原始数据</a-button>
      <a-button type="primary" @click="handleDiff('0')">教职工数据差异</a-button>
      <a-button type="primary" @click="handleDiff('1')">本科生数据差异</a-button>
      <a-button type="primary" @click="handleDiff('2')">研究生数据差异</a-button>
      <a-button type="primary" :disabled="type==''" @click="handleMailUserCreate">执行差异邮箱账号创建</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />删除
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
        <a-popover title="自定义列" trigger="click" placement="leftBottom" style="float: right;margin: auto 10px;" >
          <template slot="content">
            <a-checkbox-group @change="onColSettingsChange" v-model="settingColumns" :defaultValue="settingColumns">
              <a-row>
                <template v-for="(item,index) in defColumns">
                  <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12" :key="index"><a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox></a-col>
                  </template>
                </template>
              </a-row>
            </a-checkbox-group>
          </template>
          <a><a-icon type="setting" />自定义列</a>
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
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>
      </a-table>
    </div>

    <mail-user-modal ref="modalForm" @ok="modalFormOk"></mail-user-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import MailUserModal from './modules/MailUserModal'
import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
import { filterMultiDictText } from '@/components/dict/JDictSelectUtil'
import { getAction, postAction, putAction, deleteAction, downFile, getFileAccessHttpUrl } from '@/api/manage'

export default {
  name: 'MailUserList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    JDictSelectTag,
    MailUserModal,
  },
  data() {
    return {
      description: '邮箱用户管理页面',
      // 表头
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
          title: '学工号',
          align: 'center',
          dataIndex: 'id',
        },
        {
          title: '姓名',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '部门名称',
          align: 'center',
          dataIndex: 'depName',
        },
        {
          title: '证件号',
          align: 'center',
          dataIndex: 'cardIdNum',
        },
        {
          title: '手机号',
          align: 'center',
          dataIndex: 'phone',
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
      settingColumns:[],
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
          title: '学工号',
          align: 'center',
          dataIndex: 'id',
        },
        {
          title: '姓名',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '性别',
          align: 'center',
          dataIndex: 'gender_dictText',
        },
        {
          title: '部门名称',
          align: 'center',
          dataIndex: 'depName',
        },
        {
          title: '证件号',
          align: 'center',
          dataIndex: 'cardIdNum',
        },
        {
          title: '手机号',
          align: 'center',
          dataIndex: 'phone',
        },
        {
          title: '身份类型',
          align: 'center',
          dataIndex: 'type_dictText',
        },
        {
          title: '账号是否创建',
          align: 'center',
          dataIndex: 'mailCreated_dictText',
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
        list: '/mail_user/mailUser/list',
        delete: '/mail_user/mailUser/delete',
        deleteBatch: '/mail_user/mailUser/deleteBatch',
        exportXlsUrl: '/mail_user/mailUser/exportXls',
        importExcelUrl: 'mail_user/mailUser/importExcel',
        syncUrl: 'mail_user/mailUser/sync',
        diffUrl: 'mail_user/mailUser/diff',
        createMailUserUrl: 'mail_user/mailUser/createMailUser',
        mailUserAttrsUrl: 'mail_user/mailUser/mailUserAttrs',
        deleteMailUserUrl: 'mail_user/mailUser/deleteMailUser',
        updateMailUserUrl: 'mail_user/mailUser/updateMailUser',
      },
      dictOptions: {},
      type: '',
      diffData: [],
    }
  },
  created() {
    this.initColumns();
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
    handleTableChange(pagination, filters, sorter) {
      if (Object.keys(sorter).length > 0) {
        this.isorter.column = sorter.field;
        this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
      }
      this.ipagination = pagination;
      if(this.type == '') {
        return this.loadData();
      }
      // 本地分页
      console.info({pagination, filters, sorter})
      this.dataSource = this.diffData.slice((pagination.current - 1) * pagination.pageSize, pagination.current * pagination.pageSize)
    },
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error('请设置url.list属性!')
        return
      }
      // 恢复pagination.pageSize
      this.ipagination.pageSize = 10
      // 恢复 type
      this.type = ''
      //加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1
      }
      var params = this.getQueryParams() //查询条件
      this.loading = true
      getAction(this.url.list, params).then((res) => {
        if (res.success) {
          this.dataSource = res.result.records
          this.ipagination.total = res.result.total
        }
        if (res.code === 510) {
          this.$message.warning(res.message)
        }
        this.loading = false
      })
    },
    initDictConfig() {},
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
    handleDiff(type) {
      this.loading = true
      this.type = type
      getAction(this.url.diffUrl, { type }).then((res) => {
        this.loading = false
        if (res.success) {
          console.info('handleDiff res.success')
          this.$message.success(res.message)
          this.diffData = res.result
          this.dataSource = res.result.slice(0, 10)
          this.ipagination.total = res.result.length
          this.ipagination.pageSize = 10
        } else {
          this.$message.error(res.message)
        }
      })
    },
    handleMailUserCreate() {
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！')
        return
      } else {
        let ids = ''
        for (let a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        this.loading = true
        postAction(this.url.createMailUserUrl, null, { ids, type: this.type }).then((res) => {
          this.loading = false
          if (res.success) {
            const message = `创建成功的账号：${res.result.successUsers.map(
              (item) => item.id
            )}\n创建失败的账号：${res.result.failedUsers.map((item) => item.id)}`
            this.$message.success(message)
          } else {
            this.$message.error(res.message)
          }
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