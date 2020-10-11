<template>
  <v-container class="board" fluid>
    <v-row
        v-for="(x, xIdx) in height"
        :key="xIdx"
        no-gutters
        justify="center"
        class="flex-nowrap"
    >
      <v-col v-for="(y, yIdx) in width" :key="yIdx">
        <Cell
            :x="x - 1"
            :y="y - 1"
            :loading="loading"
            @cellUpdate="cellUpdate"
        />
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="3">
        <v-progress-linear indeterminate v-show="debouceRunning" />
      </v-col>
    </v-row>
    <v-overlay absolute :value="loading" z-index="1">
      <v-progress-circular indeterminate />
    </v-overlay>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Cell from "@/components/Cell.vue";
import _, {DebouncedFunc} from "lodash";

@Component({
  components: {
    Cell
  }
})
export default class Board extends Vue {
  private cellUpdateCall: DebouncedFunc<() => void>;

  mounted() {
    this.cellUpdateCall = _.debounce(() => {
      this.$store.dispatch("updateProbability");
      this.$store.dispatch("setDebounce", false);
    }, 1500);

    this.$store.watch(
      state => state.minesweeper.debounce,
      debounce => {
        if (!debounce) {
          this.cellUpdateCall.cancel();
        }
      }
    );
  }

  get width() {
    return this.$store.state.minesweeper.cells[0].length;
  }

  get height() {
    return this.$store.state.minesweeper.cells.length;
  }

  cellUpdate() {
    console.log("Updating...")
    this.$store.dispatch("setDebounce", true);
    this.cellUpdateCall();
  }

  get loading() {
    return this.$store.state.minesweeper.apiLoading;
  }

  get debouceRunning() {
    return this.$store.state.minesweeper.debounce;
  }
}
</script>

<style scoped>
.board {
  position: relative;
  overflow-x: auto;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.col {
  flex-grow: 0;
}
</style>
