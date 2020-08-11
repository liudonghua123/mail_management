<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="用户状态">
              <j-dict-select-tag placeholder="请选择用户状态" v-model="queryParam.userStatus" dictCode="coremail_user_status"/>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="域名">
              <j-dict-select-tag placeholder="请选择域名" v-model="queryParam.domainName" dictCode="coremail_domain_name"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
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
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
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
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button type="danger" icon="sync" @click="handleSync">同步coremail数据</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        <a-popover title="自定义列" trigger="click" placement="leftBottom">
          <template slot="content">
            <a-checkbox-group @change="onColSettingsChange" v-model="settingColumns" :defaultValue="settingColumns">
              <a-row>
                <template v-for="(item,index) in defColumns">
                  <template v-if="item.key!='rowIndex'&& item.dataIndex!='action'">
                      <a-col :span="12"><a-checkbox :value="item.dataIndex">{{ item.title }}</a-checkbox></a-col>
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
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
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

    <core-mail-user-modal ref="modalForm" @ok="modalFormOk"></core-mail-user-modal>
  </a-card>
</template>

<script>

  import Vue from 'vue'
  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CoreMailUserModal from './modules/CoreMailUserModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import { getAction, postAction, putAction, deleteAction, downFile, getFileAccessHttpUrl } from '@/api/manage'

  export default {
    name: 'CoreMailUserList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      JDictSelectTag,
      CoreMailUserModal
    },
    data () {
      return {
        description: 'coremail用户管理页面', 
        //表头
        columns:[
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'服务等级',
            align:"center",
            dataIndex: 'cosId'
          },
          {
            title:'用户状态',
            align:"center",
            dataIndex: 'userStatus_dictText'
          },
          {
            title:'域名',
            align:"center",
            dataIndex: 'domainName_dictText'
          },
          {
            title:'真实姓名',
            align:"center",
            dataIndex: 'trueName'
          },
          {
            title:'昵称',
            align:"center",
            dataIndex: 'nickName'
          },
          {
            title:'手机号码',
            align:"center",
            dataIndex: 'mobileNumber'
          },
          {
            title:'身份证号',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'工号(学号)',
            align:"center",
            dataIndex: 'zipcode'
          },
          {
            title:'学院(及专业)',
            align:"center",
            dataIndex: 'homepage'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        //列设置
        settingColumns:[],
        // 表头
        defColumns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'服务等级',
            align:"center",
            dataIndex: 'cosId'
          },
          {
            title:'用户列表排序',
            align:"center",
            dataIndex: 'userListRank'
          },
          {
            title:'用户状态',
            align:"center",
            dataIndex: 'userStatus_dictText'
          },
          {
            title:'域名',
            align:"center",
            dataIndex: 'domainName_dictText'
          },
          {
            title:'用户附加容量',
            align:"center",
            dataIndex: 'quotaDelta'
          },
          {
            title:'网盘附加容量',
            align:"center",
            dataIndex: 'nfQuotaDelta'
          },
          {
            title:'用户过期日',
            align:"center",
            dataIndex: 'userExpiryDate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'用户部门id',
            align:"center",
            dataIndex: 'orgUnitId'
          },
          {
            title:'密码',
            align:"center",
            dataIndex: 'password'
          },
          {
            title:'自动转发地址',
            align:"center",
            dataIndex: 'forwarddes'
          },
          {
            title:'自动转发功能是否生效',
            align:"center",
            dataIndex: 'forwardactive_dictText'
          },
          {
            title:'自动转发是否在本站保存',
            align:"center",
            dataIndex: 'keeplocal_dictText'
          },
          {
            title:'时区',
            align:"center",
            dataIndex: 'timeZone'
          },
          {
            title:'删除邮件后的操作',
            align:"center",
            dataIndex: 'afterdel'
          },
          {
            title:'其它email地址',
            align:"center",
            dataIndex: 'altEmail'
          },
          {
            title:'忘记密码问题',
            align:"center",
            dataIndex: 'pwdHintQuestion'
          },
          {
            title:'忘记密码答案',
            align:"center",
            dataIndex: 'pwdHintAnswer'
          },
          {
            title:'真实姓名',
            align:"center",
            dataIndex: 'trueName'
          },
          {
            title:'昵称',
            align:"center",
            dataIndex: 'nickName'
          },
          {
            title:'手机号码',
            align:"center",
            dataIndex: 'mobileNumber'
          },
          {
            title:'家庭电话',
            align:"center",
            dataIndex: 'homePhone'
          },
          {
            title:'公司电话',
            align:"center",
            dataIndex: 'companyPhone'
          },
          {
            title:'传真号码',
            align:"center",
            dataIndex: 'faxNumber'
          },
          {
            title:'性别',
            align:"center",
            dataIndex: 'gender'
          },
          {
            title:'省份',
            align:"center",
            dataIndex: 'province'
          },
          {
            title:'城市',
            align:"center",
            dataIndex: 'city'
          },
          {
            title:'生日',
            align:"center",
            dataIndex: 'birthday',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'身份证号',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'工号(学号)',
            align:"center",
            dataIndex: 'zipcode'
          },
          {
            title:'学院(及专业)',
            align:"center",
            dataIndex: 'homepage'
          },
          {
            title:'国家/地区',
            align:"center",
            dataIndex: 'country'
          },
          {
            title:'周年纪念日',
            align:"center",
            dataIndex: 'anniversary'
          },
          {
            title:'注册IP',
            align:"center",
            dataIndex: 'regIp'
          },
          {
            title:'职位',
            align:"center",
            dataIndex: 'duty'
          },
          {
            title:'备注',
            align:"center",
            dataIndex: 'remarks'
          },
          {
            title:'完整部门路径',
            align:"center",
            dataIndex: 'orgUnitFullname'
          },
          {
            title:'IP绑定',
            align:"center",
            dataIndex: 'loginIpRange'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/core_mail_user/coreMailUser/list",
          delete: "/core_mail_user/coreMailUser/delete",
          deleteBatch: "/core_mail_user/coreMailUser/deleteBatch",
          exportXlsUrl: "/core_mail_user/coreMailUser/exportXls",
          importExcelUrl: "core_mail_user/coreMailUser/importExcel",
          syncUrl: 'core_mail_user/coreMailUser/sync',
          
        },
        dictOptions:{},
      }
    },
    created() {
      this.initColumns();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
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
      initDictConfig(){
      },
      //列设置更改事件
      onColSettingsChange (checkedValues) {
        var key = this.$route.name+":colsettings";
        Vue.ls.set(key, checkedValues, 7 * 24 * 60 * 60 * 1000)
        this.settingColumns = checkedValues;
        const cols = this.defColumns.filter(item => {
          if(item.key =='rowIndex'|| item.dataIndex=='action'){
            return true
          }
          if (this.settingColumns.includes(item.dataIndex)) {
            return true
          }
          return false
        })
        this.columns =  cols;
      },
      initColumns(){
        //权限过滤（列权限控制时打开，修改第二个参数为授权码前缀）
        //this.defColumns = colAuthFilter(this.defColumns,'testdemo:');

        var key = this.$route.name+":colsettings";
        let colSettings= Vue.ls.get(key);
        if(colSettings==null||colSettings==undefined){
          let allSettingColumns = [];
          this.defColumns.forEach(function (item,i,array ) {
            allSettingColumns.push(item.dataIndex);
          })
          this.settingColumns = allSettingColumns;
          this.columns = this.defColumns;
        }else{
          this.settingColumns = colSettings;
          const cols = this.defColumns.filter(item => {
            if(item.key =='rowIndex'|| item.dataIndex=='action'){
              return true;
            }
            if (colSettings.includes(item.dataIndex)) {
              return true;
            }
            return false;
          })
          this.columns =  cols;
        }
      }
    }
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