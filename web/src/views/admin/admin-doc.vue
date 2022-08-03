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
              新增文档
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
      title="文档表单"
      :loading="modelLoading"
      @ok="handleModelOk">
    <!-- 表单 -->
    <a-form :model="doc" :label-col="{span:6}" :wrapper-col="wrapperCol">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <!-- 选择父文档组件 -->
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
      <a-form-item label="内容">
        <div style="border: 1px solid #ccc">
          <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
          />
          <Editor
              style="height: 500px; overflow-y: hidden;"
              v-model="valueHtml"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
          />
        </div>
      </a-form-item>
    </a-form>
  </a-modal>
  <!--对话框 end-->
</template>

<script lang="ts">
import '@wangeditor/editor/dist/css/style.css' // 引入 css

import {onBeforeUnmount, shallowRef} from 'vue'
import {defineComponent, onMounted, ref} from "vue";
import axios from "axios";
import message from 'ant-design-vue'
import {Tool} from '@/util/tool'
import {useRoute} from "vue-router";
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'

export default defineComponent({
  name: "Admin-Doc",
  components: {Editor, Toolbar},
  setup() {
    const route = useRoute()
    const doc = ref();
    const docs = ref()
    const loading = ref(false);

    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];

    // 将某节点及其子孙节点全部置为disabled
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

    // 编辑
    const edit = (record: any) => {
      modelVisible.value = true;
      doc.value = Tool.copy(record);
      console.log(doc.value)
      treeSelectData.value = Tool.copy(level1.value);// 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      setDisable(treeSelectData.value, record.id);
      treeSelectData.value.unshift({id: 0, name: '无'});// 为选择树添加一个"无"
    };

    const modelVisible = ref(false); // 模态框是否可见
    const modelLoading = ref(false); // 模态框是否处于加载状态
    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父文档',
        key: 'parent',
        dataIndex: 'parent'
      }, {
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

    const level1 = ref();

    // 查询数据
    const handleQuery = () => {
      loading.value = true; // 当加载数据时，打开 loading 状态
      axios.get("/doc/all").then((res) => {
        loading.value = false;// 当有返回值时，关闭 loading 状态
        const data = res.data;// 获取返回值数据
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


    // 发送表单请求
    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/doc/save", doc.value).then((res) => {
        modelLoading.value = false;
        const data = res.data; // data = commonResp 在这里用以判断是否有值
        if (data.success) {
          modelVisible.value = false;// 当 data 有值的时候再去修改 model 框的属性
          modelLoading.value = false;
          handleQuery();// 重新加载列表
        } else {
          // 提醒错误信息
          // message.error(data.message)
        }
      })
    };

    // 新增
    const add = () => {
      modelVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };
      treeSelectData.value = Tool.copy(level1.value);
      treeSelectData.value.unshift({id: 0, name: '无'}); // 为选择树添加一个"无"
    };

    // 删除
    const handleDelete = (id: any) => {
      axios.delete("/doc/delete/" + id).then((res) => {
        const data = res.data;
        if (data.success) {
          handleQuery()
        }
      })
    };

    // 编辑器实例--------------------
    // 编辑器实例，必须用 shallowRef
    const editorRef = shallowRef()

    // 内容 HTML
    const valueHtml = ref('<p>hello</p>')


    onMounted(() => {
      handleQuery();
      setTimeout(() => {
        valueHtml.value = '<p>模拟 Ajax 异步设置内容</p>'
      }, 1500);
    });

    const toolbarConfig = {}
    const editorConfig = { placeholder: '请输入内容...' }

    // 组件销毁时，也及时销毁编辑器
    onBeforeUnmount(() => {
      const editor = editorRef.value
      if (editor == null) return
      editor.destroy()
    })

    const handleCreated = (editor:any) => {
      editorRef.value = editor // 记录 editor 实例，重要！
    }
    // 编辑器实例--------------------

    return {
      columns,
      loading,
      modelLoading,
      modelVisible,
      edit,
      add,
      handleModelOk,
      handleDelete,
      handleQuery,// 查询方法
      doc,// 文档信息
      docs,
      level1, // 文档树数据
      treeSelectData,

      // editor
      editorRef,
      valueHtml,
      mode: 'default', // 或 'simple'
      toolbarConfig,
      editorConfig,
      handleCreated
    }
  }
})
</script>
