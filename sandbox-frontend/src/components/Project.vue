<template>
  <v-card>
    <v-card-title>{{ name }}</v-card-title>
    <v-card-subtitle>{{ description }}
    </v-card-subtitle>
    <v-card-text>
      <v-list>
        <v-list-item>
          <v-list-item-icon>
            <v-icon :style="{ color: complete ? 'green' : 'red' }">
              {{
                complete
                    ? "mdi-checkbox-marked-circle-outline"
                    : "mdi-alert-rhombus-outline"
              }}
            </v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            Initial Development
          </v-list-item-content>
        </v-list-item>
        <v-list-item v-for="(feature, idx) in features" :key="idx">
          <v-list-item-icon>
            <ProjectIcon :feature="feature"/>
          </v-list-item-icon>
          <v-list-item-content>
            {{ feature.name }}
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import {ProjectMeta} from "@/views/About.vue";
import ProjectIcon from "@/components/ProjectIcon.vue";

@Component({
  components: {
    ProjectIcon
  }
})
export default class Project extends Vue {
  @Prop() private previousProject: ProjectMeta;

  get name() {
    return this.previousProject != null ? this.previousProject.name : "";
  }

  get description() {
    return this.previousProject != null ? this.previousProject.description : "";
  }

  get complete() {
    return this.previousProject != null ? this.previousProject.complete : false;
  }

  get features() {
    return this.previousProject != null ? this.previousProject.features : [];
  }
}
</script>

<style scoped>

</style>