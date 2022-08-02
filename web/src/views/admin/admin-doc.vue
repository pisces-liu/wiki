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
    <a-form :model="doc" :label-col="{span:6}" :wrapper-col="wrapperCol">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <!-- 选择父分类组件 -->
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="请选择父文档"
            tree-default-expand-all
            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort"/>
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
import {useRoute} from "vue-router";

export default defineComponent({
  name: "Admin-Doc",
  setup() {
    const route = useRoute()
    const doc = ref();
    doc.value = {
      ebookId: route.query.ebookId
    };
    const docs = ref()
    const loading = ref(false);

    //######################################## 编辑分类相关代码
    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modelVisible.value = true;

      doc.value = Tool.copy(record);
      console.log(doc.value)
      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);
      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});
    };
    //######################################## 编辑分类相关代码 end


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
        title: '父分类',
        key: 'parent',
        dataIndex: 'parent'
      }, {
        title: 'sort',
        dataIndex: 'sort'
      }, {
        title: 'Action',
        key: 'action',
        slots: {
          customRender: 'action'
        }
      }
    ];

    const level1 = ref();

    /*
    * 数据查询
    * */
    const handleQuery = () => {
      // 当加载数据时，打开 loading 状态
      loading.value = true;
      axios.get("/doc/all").then((res) => {
        // 当有返回值时，关闭 loading 状态
        loading.value = false;
        // 获取返回值数据
        const data = res.data;
        // 如果正常响应状态成功，获取响应数据内的数据，否则，提示错误
        if (data.success) {
          docs.value = data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
        } else {
          // message.info(data.message);
        }
      });
    };


    /**
     * 点击 表单 ok 时，发送请求
     * */
    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/doc/save", doc.value).then((res) => {
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
     * 新增
     */
    const add = () => {
      modelVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);
      // 为选择树添加一个"无"
      treeSelectData.value.unshift({id: 0, name: '无'});
    };

    /**
     * 删除
     */
    const handleDelete = (id: any) => {
      axios.delete("/doc/delete/" + id).then((res) => {
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
      doc,
      docs,
      // 文档树数据
      level1,

      treeSelectData
    }

  }
})
</script>

<style scoped>
</style>