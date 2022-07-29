<template>
  <a-layout style="padding: 0 24px 24px">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <!-- 电子书封面 -->
        <template #cover="{text: cover}">
          <img v-if="cover" :src="cover" alt="avatar">
        </template>
        <!-- 编辑按钮       -->
        <template v-slot:action="{text, record}">
          <!-- 空格组件 -->
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-button type="danger">
              删除
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <!--对话框-->
  <a-modal
      v-model:visible="modelVisible"
      title="电子书表单"
      :loading="modelLoading"
      @ok="handleModelOk">
    <!-- 表单 -->
    <a-form :model="ebook" :label-col="{span:6}" :wrapper-col="wrapperCol">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name"/>
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id"/>
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id"/>
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="text"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <!--对话框 end-->
</template>

<script>
import {defineComponent, onMounted, ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "Admin-eBook",
  setup() {
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 5,
      total: 0
    });
    const loading = ref(false);
    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类一',
        key: 'category1Id',
        dataIndex: 'category1Id'
      },
      {
        title: '分类二',
        key: 'category2Id',
        dataIndex: 'category2Id'
      }, {
        title: '文档数',
        dataIndex: 'docCount'
      }, {
        title: '阅读数',
        dataIndex: 'viewCount'
      }, {
        title: '点赞数',
        dataIndex: 'voteCount'
      }, {
        title: 'Action',
        key: 'action',
        slots: {
          customRender: 'action'
        }
      }
    ];

    /*
    * 数据查询
    * */
    const handleQuery = (params) => {
      loading.value = true;
      axios.get("/ebook/list", {
        params: {
          page: params.page,
          size: params.size
        }
      }).then((res) => {
        loading.value = false;
        const data = res.data;
        ebooks.value = data.content.list;

        // 重置分页按钮
        pagination.value.current = params.page;
        pagination.value.total = data.content.total;
      });
    };

    /**
     * 点击表格页码时触发
     * @param pagination
     */
    const handleTableChange = (pagination) => {
      console.log("分页参数: " + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };
    /**
     * 表单
     * */
        // 模态框是否可见
    const modelVisible = ref(false);
    // 模态框是否处于加载状态
    const modelLoading = ref(false);
    // 电子书
    const ebook = ref({})

    const handleModelOk = () => {
      modelLoading.value = true;
      axios.post("/ebook/save", ebook.value).then((res) => {
        const data = res.data; // data = commonResp 在这里用以判断是否有值
        if (data.success) {
          // 当 data 有值的时候再去修改 model 框的属性
          modelVisible.value = false;
          modelLoading.value = false;

          // 重新加载列表
          handleQuery({
            page: 1,
            size: pagination.value.pageSize
          })
        }
      })
    }
    /**
     * 编辑
     */
    const edit = (record) => {
      modelVisible.value = true;
      ebook.value = record
    }

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize
      })
    });

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,
      modelLoading,
      modelVisible,
      handleModelOk,
      edit,
      ebook
    }

  }
})
</script>

<style scoped>

</style>