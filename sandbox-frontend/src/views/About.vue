<template>
  <div class="about">
    <v-container>
      <v-row>
        <v-col>
          <h1>Project Status</h1>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-list disabled>
            <v-list-item-group v-model="inProgress">
              <v-list-item v-for="(item, index) in projectItems" :key="index">
                <v-list-item-content>
                  <v-list-item-title>{{ item.name }}</v-list-item-title>
                </v-list-item-content>
                <v-list-item-icon>
                  <v-icon :color="statusColor(item)">
                    {{
                      item.complete
                        ? "mdi-checkbox-marked-circle-outline"
                        : "mdi-alert-rhombus-outline"
                    }}
                  </v-icon>
                </v-list-item-icon>
              </v-list-item>
            </v-list-item-group>
          </v-list>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({})
export default class App extends Vue {
  private inProgress = 2;
  private projectItems = [
    { name: "Home Page", complete: true },
    { name: "Minesweeper Heatmap", complete: true },
    { name: "Sunshine Tracker", complete: false },
    { name: "Nonogram Solver", complete: false }
  ];

  statusColor(item) {
    return item.complete ? "green" : "red";
  }

  progressColor(idx) {
    return idx == this.inProgress ? "green" : undefined;
  }
}
</script>
