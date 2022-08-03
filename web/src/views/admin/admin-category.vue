<template>
  <a-layout style="padding: 0 24px 24px">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline">
          <a-form-item>
            <!-- 添加新增按钮 start-->
            <a-button type="primary" @click="add()">
              新增分类
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <!-- 添加新增按钮 end -->
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
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
      title="分类表单"
      :loading="modelLoading"
      @ok="handleModelOk">
    <!-- 表单 -->
    <a-form :model="category" :label-col="{span:6}" :wrapper-col="wrapperCol">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-select v-model:value="category.parent" ref="select">
          <!-- 当值是 0 时，代表这个类是父分类 -->
          <a-select-option value="0">
            无
          </a-select-option>
          <!-- :disable="category.id===c.id" 父分类不能选择父分类。所以隐藏 -->
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disable="category.id===c.id">
            {{ c.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <!--对话框 end-->
</template>

<script>
import {defineComponent, onMounted, ref} from "vue";
import axios from "axios";
import message from 'ant-design-vue'
import {Tool} from '@/util/tool'

export default defineComponent({
  name: "Admin-Category",
  setup() {
    const category = ref();
    const categorys = ref()
    const loading = ref(false);

    // 模态框是否可见
    const modelVisible = ref(false);
    // 模态框是否处于加载状态
    const modelLoading = ref(false);
    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '排序',
        dataIndex: 'sort'
      }, {
        title: '操作',
        key: 'action',
        slots: {
          customRender: 'action'
        }
      }
    ];

    /*
    * // 一级分类树，children 属性就是二级分类
    * [{
    *   id: "",
    *   name: "",
    *   children: [{
    *       id: "",
    *       name: "",
    *    }]
    * }]
    * */
    const level1 = ref();

    /*
    * 数据查询
    * */
    const handleQuery = () => {
      // 当加载数据时，打开 loading 状态
      loading.value = true;
      axios.get("/category/all").then((res) => {
        // 当有返回值时，关闭 loading 状态
        loading.value = false;
        // 获取返回值数据
        const data = res.data;
        // 如果正常响应状态成功，获取响应数据内的数据，否则，提示错误
        if (data.success) {
          categorys.value = data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(categorys.value, 0);
        } else {
          // message.info(data.message);
        }
      });
    };


    /**
     * 表单
     * */
    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/category/save", category.value).then((res) => {
        modelLoading.value = false;
        const data = res.data; // data = commonResp 在这里用以判断是否有值
        if (data.success) {
          // 当 data 有值的时候再去修改 model 框的属性
          modelVisible.value = false;
          modelLoading.value = false;
          // 重新加载列表
          handleQuery()
        } else {
          // 提醒错误信息
          message.error(data.message)
        }
      })
    }
    /**
     * 编辑
     */
    const edit = (record) => {
      modelVisible.value = true;
      category.value = Tool.copy(record);
    }
    /**
     * 保存
     */
    const add = () => {
      modelVisible.value = true;
      category.value = {};
    }

    /**
     * 删除
     */
    const handleDelete = (id) => {
      axios.delete("/category/delete/" + id).then((res) => {
        const data = res.data;
        if (data.success) {
          // 重新加载列表
          handleQuery()
        }
      })
    }

    onMounted(() => {
      handleQuery()
    });

    return {
      columns,
      loading,
      modelLoading,
      modelVisible,
      edit,
      add,
      handleModelOk,
      handleDelete,
      // 查询方法
      handleQuery,
      // 分类信息
      category,
      categorys,
      // 文档树数据
      level1
    }

  }
})
</script>

<style scoped>
</style>