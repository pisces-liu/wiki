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

export default defineComponent({
  components: {
    TheHeader,
    TheFooter
  },
  setup() {
    return {
    };
  },
});
</script>

<style>

</style>
