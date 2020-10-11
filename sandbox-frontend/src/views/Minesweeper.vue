<template>
  <v-container class="text-center" fluid>
    <v-row>
      <v-col>
        <h1>Minesweeper Heatmap</h1>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <Board />
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <BoardControl/>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Board from "@/components/Board.vue";
import BoardControl from "@/components/BoardControl.vue";

@Component({
  components: {
    Board,
    BoardControl
  }
})
export default class Minesweeper extends Vue {
  beforeCreate() {
    this.$store.dispatch('setMinesweeperBoardSize', { width: 10, height: 10})
  }
}
</script>

<style scoped></style>
