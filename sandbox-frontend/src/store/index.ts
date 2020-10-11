import Vue from "vue";
import Vuex from "vuex";
import {minesweeper} from "@/store/minesweeper";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    minesweeper: minesweeper
  }
});
