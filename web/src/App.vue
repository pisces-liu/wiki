<template>
  <a-layout>
    <the-header/>
    <router-view/>
    <the-footer/>
  </a-layout>
</template>

<script lang="ts">
import TheHeader from './components/the-header.vue';
import TheFooter from './components/the-footer.vue';
import {defineComponent, ref} from 'vue';
import axios from 'axios'

export default defineComponent({
  components: {
    TheHeader,
    TheFooter
  },
  setup() {
    axios.get("http://localhost:8848/ebook/list?name=b").then(res=>{
      console.log(res)
    })
    return {

    };
  },
});
</script>

<style>

</style>
