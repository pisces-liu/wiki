<template>
  <a-layout style="padding: 0 24px 24px">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <!-- 添加根据名称查询功能 start-->
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登录名"></a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page:1,size:pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <!-- 添加新增按钮 start-->
            <a-button type="primary" @click="add()">
              新增用户
            </a-button>
          </a-form-item>
        </a-form>
        <!-- 添加根据名称查询用户功能 end-->


      </p>
      <!-- 添加新增按钮 end -->
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <!-- 编辑按钮       -->
        <template v-slot:action="{text, record}">
          <!-- 空格组件 -->
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确定删除？"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <!--对话框-->
  <a-modal
      v-model:visible="modelVisible"
      title="用户表单"
      :loading="modelLoading"
      @ok="handleModelOk">
    <!-- 表单 -->
    <a-form :model="user" :label-col="{span:6}" :wrapper-col="wrapperCol">
      <a-form-item label="用户名">
        <a-input v-model:value="user.loginName"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <!--对话框 end-->
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from "vue";
import axios from "axios";
import message from 'ant-design-vue'
import {Tool} from '@/util/tool'

export default defineComponent({
  name: "Admin-User",
  setup() {
    // 查询用户参数：param
    const param = ref();
    param.value = {};

    const categoryIds = ref();

    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);
    const columns = [
      {
        title: '用户名',
        dataIndex: 'loginName'
      },
      {
        title: '昵称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
      },
      {
        title: '管理',
        key: 'action',
        slots: {
          customRender: 'action'
        }
      }
    ];

    const level1 = ref();
    let categorys: any;

    // 数据查询
    const handleQuery = (params: any) => {
      console.log("params:")
      console.log(param.value.loginName)
      loading.value = true;
      axios.get("/user/all", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((res) => {
        loading.value = false;
        const data = res.data;
        if (data.success) {

          users.value = data.content.list;
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;

          param.value = {};
        } else {
          // 通过 ant 组件，提示错误信息
          // message.info(data.message);
        }
      });
    };


    const handleTableChange = (pagination: any) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };
    const modelVisible = ref(false);
    const modelLoading = ref(false);
    const user: any = ref({})

    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/user/save", user.value).then((res) => {

        modelLoading.value = false;
        const data = res.data; // data = commonResp 在这里用以判断是否有值
        if (data.success) {
          // 当 data 有值的时候再去修改 model 框的属性
          modelVisible.value = false;
          modelLoading.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          })
        } else {
          // 提醒错误信息
          // message.error(data.message)
        }
      })
    }
    /**
     * 编辑
     */
    const edit = (record: any) => {
      modelVisible.value = true;
      user.value = Tool.copy(record);
      // 准备 级联 数据
      categoryIds.value = [user.value.category1Id, user.value.category2Id];
    }
    /**
     * 保存
     */
    const add = () => {
      modelVisible.value = true;
      user.value = {}
    }

    /**
     * 删除
     */
    const handleDelete = (id: any) => {
      axios.delete("/user/delete/" + id).then((res) => {
        const data = res.data;
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          })
        }
      })
    }


    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      })
    });

    return {
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      modelLoading,
      modelVisible,
      edit,
      add,
      user,
      handleModelOk,
      handleDelete,
      // 用户查询数据
      param,
      // 查询方法
      handleQuery,
      categoryIds,
      level1
    }

  }
})
</script>

<style scoped>
img {
  width: 70px;
}
</style>