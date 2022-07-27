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
import {defineComponent} from 'vue';

export default defineComponent({
  components: {
    TheHeader,
    TheFooter
  }
});
</script>
