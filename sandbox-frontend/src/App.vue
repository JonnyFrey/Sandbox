<template>
  <v-app>
    <v-app-bar app color="green">
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-spacer />
      <div class="d-flex align-center">
        <transition name="fade" appear>
          <v-img
            alt="Waffel Logo"
            class="shrink mr-2"
            contain
            src="@/assets/waffel_icon.png"
            width="40"
          />
        </transition>
        <transition name="slide-fade" appear>
          <v-toolbar-title style="color: black">Sandbox</v-toolbar-title>
        </transition>
      </div>

      <v-spacer />
    </v-app-bar>
    <v-navigation-drawer v-model="drawer" bottom temporary app>
      <v-list nav dense>
        <v-list-item-group active-class="green--text text--accent-4">
          <v-list-item
            v-for="(item, index) in drawerItems"
            :key="index"
            @click="autoCollapse"
            :to="item.path"
          >
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ item.name }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <transition
        name="slide-fade"
        mode="out-in"
        @before-leave="callMoving(true)"
        @after-enter="callMoving(false)"
      >
        <router-view />
      </transition>
    </v-main>
  </v-app>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";

@Component({})
export default class App extends Vue {
  private drawer = false;
  private drawerItems = [
    { name: "Home", path: "/", icon: "mdi-home" },
    { name: "Minesweeper Heatmap", path: "/minesweeper", icon: "mdi-mine" },
    { name: "About", path: "/about", icon: "mdi-progress-wrench" }
  ];

  callMoving(payload: boolean) {
    this.$store.dispatch("moving", payload);
  }

  autoCollapse() {
    this.drawer = false;
  }
}
</script>

<style scoped>
.slide-fade-enter {
  transform: translateX(-100px);
  opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.5s ease;
}

.slide-fade-leave-to {
  transform: translateX(100px);
  opacity: 0;
}
</style>
