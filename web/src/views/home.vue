<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          v-model:selectedKeys="selectedKeys2"
          v-model:openKeys="openKeys"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <!--显示欢迎导航-->
        <a-menu-item key="WelCome">
          <router-link :to="'/'">
            <MailOutlined/>
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
        <!--分类导航-->
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
              <span>
                <user-outlined/>
                {{ item.name }}
              </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
              <span>
                <user-outlined/>
                {{ child.name }}
              </span>
          </a-menu-item>
        </a-sub-menu>
        <!--分类导航 end-->
      </a-menu>
    </a-layout-sider>
    <a-layout style="padding: 0 24px 24px">
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <a-list item-layout="vertical" size="large" :grid="{ gutter: 16, column: 3 }"
                :data-source="eEbookList">
          <template #renderItem="{ item }">
            <a-list-item key="item.name">
              <template #actions>
                <span v-for="{ type, text } in actions" :key="type">
                  <component :is="type" style="margin-right: 8px"/>
                  {{ text }}
                </span>
              </template>
              <a-list-item-meta :description="item.description">
                <template #title>
                  <a :href="item.href">{{ item.name }}</a>
                </template>
                <template #avatar>
                  <a-avatar :src="item.cover"/>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-layout-content>
    </a-layout>
  </a-layout>

</template>

<script lang="ts">
import axios from 'axios';
import {defineComponent, onMounted, ref} from 'vue';
import {
  UserOutlined,
  LaptopOutlined,
  NotificationOutlined,
  StarOutlined,
  LikeOutlined,
  MessageOutlined,
  MailOutlined
} from '@ant-design/icons-vue';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'Home',
  components: {
    UserOutlined,
    LaptopOutlined,
    NotificationOutlined,
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
    MailOutlined
  },
  setup() {
    // 书籍列表
    const eEbookList = ref();
    // 分类列表
    const level1 = ref();

    // StarOutlined：收藏数，LikeOutlined：点赞数，MessageOutlined：留言数
    const actions: Record<string, string>[] = [
      {type: 'StarOutlined', text: '156'},
      {type: 'LikeOutlined', text: '156'},
      {type: 'MessageOutlined', text: '2'},
    ];


    // 获取分类数据
    const getCategoryList = () => {
      // 当加载数据时，打开 loading 状态
      axios.get("/category/all").then((res) => {
        // 获取返回值数据
        const data = res.data;
        // 如果正常响应状态成功，获取响应数据内的数据，否则，提示错误
        if (data.success) {
          const categorys = data.content;
          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：");
          console.log(level1.value)
        } else {
          // message.info(data.message);
        }
      });
    };

    // 获取电子书数据
    const getEbookList = () => {
      axios.get("/ebook/list").then((res) => {
        const data = res.data;
        eEbookList.value = data.content.list;
      })
    }


    onMounted(() => {
      getCategoryList();
      getEbookList();
    });


    return {
      eEbookList,
      actions,
      level1
    }
  }
});
</script>
