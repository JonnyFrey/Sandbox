<template>
  <v-icon :style="{ color: feature.complete ? 'green' : 'red' }">
    {{ icon }}
  </v-icon>
</template>

<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import {Feature} from "@/views/About.vue";

@Component({})
export default class ProjectIcon extends Vue {
  @Prop({ required: true }) private feature: Feature;

  get icon() {
    if (this.feature.bug) {
      return this.feature.complete ? "mdi-bug-check-outline" : "mdi-bug-outline";
    }
    return this.feature.complete ? "mdi-checkbox-marked-circle-outline" : "mdi-alert-rhombus-outline"
  }
}
</script>

<style scoped>

</style>