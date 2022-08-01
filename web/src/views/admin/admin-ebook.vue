<template>
  <a-layout style="padding: 0 24px 24px">
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <!-- 添加根据名称查询电子书功能 start-->
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称"></a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page:1,size:pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <!-- 添加新增按钮 start-->
            <a-button type="primary" @click="add()">
              新增图书
            </a-button>
          </a-form-item>

        </a-form>
        <!-- 添加根据名称查询电子书功能 end-->


      </p>
      <!-- 添加新增按钮 end -->
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
        <!-- 级联分类 -->
        <template v-slot:category="{text,record}">
          <span>{{ getCategoryName(record.category1Id) }}/{{ getCategoryName(record.category2Id) }}</span>
        </template>

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
      <a-form-item label="分类">
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{ label:'name',value:'id',children:'children'}"
            :options="level1"/>
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="text"/>
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
  name: "Admin-eBook",
  setup() {
    // 查询电子书参数：param
    const param = ref();
    param.value = {};
    /*
* 数组：[100, 101] 对应：前端开发/Vue
* */
    const categoryIds = ref();

    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
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
        title: '分类',
        slots: {customRender: 'category'}
      },
      {
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

    const level1 = ref();
    let categorys;

    /*
    * 分类数据查询
    * */
    const handleQueryCategory = () => {
      // 当加载数据时，打开 loading 状态
      loading.value = true;
      axios.get("/category/all").then((res) => {
        // 当有返回值时，关闭 loading 状态
        loading.value = false;
        // 获取返回值数据
        const data = res.data;
        // 如果正常响应状态成功，获取响应数据内的数据，否则，提示错误
        if (data.success) {
          categorys = data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：");
          console.log(level1.value)
        } else {
          // message.info(data.message);
        }
      });
    };

    const getCategoryName = (cid: number) => {
      let result = "";
      categorys.forEach((item: any) => {
        if (item.id === cid) {
          result = item.name;
        }
      });
      return result;
    }

    /*
    * 数据查询
    * */
    const handleQuery = (params) => {
      // 当加载数据时，打开 loading 状态
      loading.value = true;
      axios.get("/ebook/list", {
        // axios 拼接请求参数，参数为分页信息
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name
        }
      }).then((res) => {
        // 当有返回值时，关闭 loading 状态
        loading.value = false;
        // 获取返回值数据
        const data = res.data;
        // 如果正常响应状态成功，获取响应数据内的数据，否则，提示错误
        if (data.success) {

          ebooks.value = data.content.list;
          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;

          // 重置 查询参数
          param.value = {};
        } else {
          // 通过 ant 组件，提示错误信息
          // message.info(data.message);
        }
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
      // 准备 级联 数据

      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      axios.post("/ebook/save", ebook.value).then((res) => {

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
          message.error(data.message)
        }
      })
    }
    /**
     * 编辑
     */
    const edit = (record: any) => {
      modelVisible.value = true;
      ebook.value = Tool.copy(record);
      // 准备 级联 数据
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
    }
    /**
     * 保存
     */
    const add = () => {
      modelVisible.value = true;
      ebook.value = {}
    }

    /**
     * 删除
     */
    const handleDelete = (id) => {
      axios.delete("/ebook/delete/" + id).then((res) => {
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
      // 调用获取分类数据方法
      handleQueryCategory();
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
      edit,
      add,
      ebook,
      handleModelOk,
      handleDelete,
      // 电子书查询数据
      param,
      // 查询方法
      handleQuery,
      categoryIds,
      level1,
      getCategoryName
    }

  }
})
</script>

<style scoped>
img {
  width: 70px;
}
</style>