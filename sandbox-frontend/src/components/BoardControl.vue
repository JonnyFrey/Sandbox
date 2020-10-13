<template>
  <v-container>
    <v-row>
      <v-col md="4">
        <v-slider
          v-model="width"
          label="Width"
          thumb-label
          min="3"
          max="30"
          track-fill-color="green"
          thumb-color="green"
        />
      </v-col>
      <v-col md="4">
        <v-slider
          v-model="height"
          label="Height"
          thumb-label
          min="3"
          max="30"
          track-fill-color="green"
          thumb-color="green"
        />
      </v-col>
      <v-col md="2">
        <v-form ref="bombForm">
          <v-text-field
              v-model.number="bombs"
              label="Bombs"
              type="number"
              :rules="[checkBombMax, checkBombMin]"
              thumb-label
              min="1"
              max="900"
              track-fill-color="green"
          />
        </v-form>
      </v-col>
      <v-col md="1">
        <v-btn @click="resetBoard">Reset</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-progress-linear v-show="loading" indeterminate />
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import _, { DebouncedFunc } from "lodash";

@Component({})
export default class BoardControl extends Vue {
  private width = 10;
  private height = 10;
  private bombs = 10;
  private boardSizeFunction: DebouncedFunc<() => void>;
  private bombFunction: DebouncedFunc<() => void>;
  private loading = false;
  private errored = false;

  mounted() {
    this.boardSizeFunction = _.debounce(() => {
      //@ts-ignore
      if (this.$refs.bombForm.validate()) {
        this.$store.dispatch("setMinesweeperBoardSize", {
          width: this.width,
          height: this.height
        });
      }
      this.loading = false;
    }, 1500);

    this.bombFunction = _.debounce(() => {
      //@ts-ignore
      if (this.$refs.bombForm.validate()) {
        this.$store.dispatch("updateBomb", this.bombs);
      }
      this.loading = false;
    }, 1500);
  }

  calculateMaxBombs() {
    return this.width * this.height;
  }

  checkBombMax() {
    return (
      this.width * this.height >= this.bombs ||
      "Can't specify more bombs than can exist"
    );
  }

  checkBombMin() {
    return this.bombs > 0 || "Can't have no bombs exist";
  }

  @Watch("width")
  @Watch("height")
  updateBoard() {
    if (!this.errored) {
      this.$store.dispatch("setDebounce", false);
      this.loading = true;
      // Board Function Takes precedence
      this.bombFunction.cancel();
      this.boardSizeFunction();
    }
  }

  @Watch("bombs")
  updateBomb() {
    // Board Function Takes precedence
    if (!this.loading && !this.errored) {
      this.$store.dispatch("setDebounce", false);
      this.loading = true;
      this.bombFunction();
    }
  }

  resetBoard() {
    this.$store.dispatch("resetBoardState");
  }
}
</script>

<style scoped></style>
