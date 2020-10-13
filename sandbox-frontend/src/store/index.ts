import Vue from "vue";
import Vuex from "vuex";
import {minesweeper} from "@/store/minesweeper";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    moving: false
  },
  mutations: {
    setMoving(state, result) {
      state.moving = result
    }
  },
  actions: {
    moving({ commit }, state) {
      commit('setMoving', state)
    }
  },
  modules: {
    minesweeper: minesweeper
  }
});
