<template>
  <v-tooltip bottom :disabled="!hasErrors">
    <template v-slot:activator="{ on, attrs }">
      <v-card
        :class="['flex-center', 'square-card', { blinking: hasErrors }]"
        elevation="4"
        hover
        @contextmenu.prevent="swapShowNumbers"
        @click="swapValues"
        :style="color"
        v-bind="attrs"
        v-on="on"
      >
        <v-card-text v-show="!loading" :style="color" class="square-text">{{ cellValue }}</v-card-text>
      </v-card>
    </template>
    {{ toolTip }}
  </v-tooltip>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";

@Component({})
export default class Cell extends Vue {
  @Prop() private x: number;
  @Prop() private y: number;
  @Prop({ default: false }) private loading;

  get cellValue() {
    const cell = this.cell;
    let result = "";
    if (cell.showNumbers) {
      result = cell.numberValue;
    } else if (!cell.isBlocked) {
      if (cell.isFlag) {
        result = "F";
      } else {
        result = cell.probability.toFixed(2) * 100;
        if (cell.isFlag && !cell.showNumbers) {
          result = 100;
        }
        if (cell.showNumbers) {
          result = 0;
        }
        //Format
        if (result < 10 && result != 0) {
          result = result.toFixed(1);
        } else {
          result = result.toFixed(0);
        }
        result += "%";
      }
    }
    return result;
  }

  get color() {
    const cell = this.cell;

    let bgColor;

    if (cell.showNumbers) {
      const startColor = 360;
      const endColor = 0;
      const newColor =
        (endColor - startColor) * (cell.numberValue / 8) + startColor;
      bgColor = {
        "background-color": "#c5c5c5 !important",
        color: "hsl(" + newColor + ", 100%, 25%)" + "!important"
      };
    } else if (cell.isBlocked) {
      bgColor = {
        "background-color": "#000000" + "!important",
        color: "#000000" + "!important"
      };
    } else if (cell.isFlag) {
      bgColor = {
        "background-color": "#FF0000" + "!important",
        color: "#FFFFFF" + "!important"
      };
    } else {
      const startColor = 100;
      const endColor = 30;
      const newColor = (endColor - startColor) * cell.probability + startColor;
      if (cell.probability != 0) {
        bgColor = {
          "background-color": "hsl(122, 39%, " + newColor + "%) !important",
          color: "#000000" + "!important"
        };
      } else {
        bgColor = {
          "background-color": "hsl(122, 39%, 100%) !important",
          color: "#515050" + "!important",
        };
      }
    }

    return bgColor;
  }

  swapShowNumbers(event: MouseEvent) {
    this.$store.dispatch("swapShowNumbersCell", { x: this.x, y: this.y });
    this.$emit("cellUpdate");
  }

  swapValues(event: MouseEvent) {
    const shouldUpdate = this.cell.showNumbers;
    console.log("Should Update " + shouldUpdate);
    console.log(this.cell);
    this.$store.dispatch("swapValuesCell", { x: this.x, y: this.y });
    if (shouldUpdate) {
      this.$emit("cellUpdate");
    }
  }

  get cell() {
    return this.$store.state.minesweeper.cells[this.x][this.y];
  }

  get hasErrors() {
    return this.cell.errMsg.length > 0;
  }

  get toolTip() {
    if (this.hasErrors) {
      return this.cell.errMsg.join("\n");
    }
    return "";
  }
}
</script>

<style scoped>
.blinking {
  animation: blinkingText 2s infinite;
}

@keyframes blinkingText {
  0% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.5;
  }
}
.square-text {
  padding: 10px;
  padding-top: 16px;
  font-weight: 800;
}

.square-card {
  height: 3.5rem;
  width: 3.5rem;
}
</style>
