// State
import {ActionTree, GetterTree, MutationTree} from "vuex";
import {api} from "@/api";

interface Root {
    cells: Cell[][];
    bombs: number;
    apiLoading: boolean;
    debounce: boolean;
}

interface Cell {
    x: number;
    y: number;
    showNumbers: boolean;
    numberValue: number;
    isFlag: boolean;
    isBlocked: boolean;
    probability: number;
    errMsg: string[];
}

const initialState: Root = {
    cells: [],
    bombs: 10,
    apiLoading: false,
    debounce: false
}

//Mutations

function validateCells(cells: Cell[][], x: number, y: number, recurs: boolean) {
    cells[x][y].errMsg = [];
    if (cells[x][y].showNumbers) {
        let unknownCount = 0;
        let flagCount = 0;
        let emptyCount = 0;
        let totalCount = 0;
        for (let mx = -1; mx <= 1; mx++) {
            for (let my = -1; my <= 1; my++) {
                const nx = mx + x;
                const ny = my + y;
                if (nx >= 0 && nx < cells.length) {
                    if (ny >= 0 && ny < cells[x].length) {
                        const cell = cells[nx][ny];
                        if (nx != x || ny != y) {
                            if (!cell.isBlocked) {
                                totalCount++;
                                if (cell.showNumbers) {
                                    emptyCount++;
                                } else if (cell.isFlag) {
                                    flagCount++;
                                } else {
                                    unknownCount++;
                                }
                            }
                        }
                    }
                }
            }
        }
        // Case too little flags
        if (totalCount - emptyCount < cells[x][y].numberValue) {
            cells[x][y].errMsg.push("Not enough unknown/flagged neighbors for bombs");
        }
        // Case too many flags
        if (flagCount > cells[x][y].numberValue) {
            cells[x][y].errMsg.push(`Too many flagged neighbors (counted ${flagCount} flags)`)
        }
    }
    if (recurs) {
        for (let mx = -1; mx <= 1; mx++) {
            for (let my = -1; my <= 1; my++) {
                const nx = mx + x;
                const ny = my + y;
                if (nx >= 0 && nx < cells.length) {
                    if (ny >= 0 && ny < cells[x].length) {
                        if (nx != x || ny != y) {
                            validateCells(cells, nx, ny, false);
                        }
                    }
                }
            }
        }
    }
}

const mutations: MutationTree<Root> = {

    setNewBoard(state, cells) {
        state.cells = cells
    },

    setFreshProbability(state) {
        const defaultProbability = state.bombs / (state.cells.length * state.cells[0].length);
        for (let x = 0; x < state.cells.length; x++) {
            for (let y = 0; y < state.cells[x].length; y++) {
                state.cells[x][y].probability = defaultProbability;
            }
        }
    },

    swapShowNumbersCell(state, coords) {
        const cell = state.cells[coords.x][coords.y]
        if (cell.isBlocked) {
            cell.isBlocked = false;
            cell.showNumbers = false;
            cell.isFlag = false;
        } else if (cell.showNumbers) {
            cell.showNumbers = false;
            cell.isBlocked = true;
        } else  {
            cell.showNumbers = true;
            cell.isBlocked = false;
        }
        validateCells(state.cells, coords.x, coords.y, true);
    },

    swapValuesCell(state, coords) {
        const cell = state.cells[coords.x][coords.y]
        if (!cell.isBlocked) {
            if (cell.showNumbers) {
                cell.numberValue = (cell.numberValue + 1) % 9;
            } else {
                cell.isFlag = !cell.isFlag;
            }
            validateCells(state.cells, coords.x, coords.y, true);
        }
    },

    updateWithProbabilities(state, probs) {
        const probsMap = new Map();
        for (const prob of probs) {
            probsMap.set(prob.x + "," + prob.y, prob)
        }
        for (let x = 0; x < state.cells.length; x++) {
            for (let y = 0; y < state.cells[x].length; y++) {
                const key = x + "," + y;
                if (probsMap.has(key)) {
                    state.cells[x][y].probability = probsMap.get(key).chance;
                }
            }
        }
    },

    setLoading(state, loadingStatus) {
        state.apiLoading = loadingStatus;
    },

    setDebounce(state, debounceStatus) {
        state.debounce = debounceStatus;
    },

    resetBoardState(state) {
        const defaultProbability = state.bombs / (state.cells.length * state.cells[0].length);
        for (let x = 0; x < state.cells.length; x++) {
            for (let y = 0; y < state.cells[x].length; y++) {
                state.cells[x][y].numberValue = 0;
                state.cells[x][y].showNumbers = false;
                state.cells[x][y].isFlag = false;
                state.cells[x][y].isBlocked = false;
                state.cells[x][y].probability = defaultProbability;
                state.cells[x][y].errMsg = [];
            }
        }
    },

    pushErrMsgToAllShowCells(state) {
        const message = "Invalid combination of states";
        for (const row of state.cells) {
            for (const cell of row) {
                if (cell.showNumbers) {
                    const set = new Set(cell.errMsg)
                    set.add(message)
                    cell.errMsg = Array.from(set)
                }
            }
        }
    },

    removeErrMsgToAllShowCells(state) {
        const message = "Invalid combination of states";
        for (const row of state.cells) {
            for (const cell of row) {
                if (cell.showNumbers) {
                    const set = new Set(cell.errMsg)
                    set.delete(message)
                    cell.errMsg = Array.from(set)
                }
            }
        }
    },

    updateBomb(state, bombs) {
        state.bombs = bombs;
    }
};

// Actions
const actions: ActionTree<Root, any> = {

    setMinesweeperBoardSize({ commit, dispatch }, { width, height }) {
        const board = [];
        for (let x = 0; x < height; x++) {
            const row = [];
            for (let y = 0; y < width; y++) {
                row.push({
                    x: x,
                    y: y,
                    showNumbers: false,
                    numberValue: 0,
                    isFlag: false,
                    isBlocked: false,
                    probability: 0.5,
                    errMsg: new Set()
                });
            }
            board.push(row);
        }
        commit('setNewBoard', board);
        commit('setFreshProbability');
    },

    swapShowNumbersCell({ commit }, { x, y }) {
        commit('removeErrMsgToAllShowCells')
        commit('swapShowNumbersCell', { x: x, y: y });
    },

    swapValuesCell({ commit }, { x, y }) {
        commit('removeErrMsgToAllShowCells')
        commit('swapValuesCell', { x: x, y: y });
    },

    async updateProbability({ commit, state, getters }) {
        console.log("Updating Probs...");
        if (!getters.containsErrors) {
            const board = [];
            let flagCount = 0;
            let unknownCount = 0;
            for (let x = 0; x < state.cells.length; x++) {
                let row = ""
                for (let y = 0; y < state.cells[x].length; y++) {
                    const cell: Cell = state.cells[x][y];
                    if (cell.showNumbers) {
                        row = row + cell.numberValue;
                    } else if (cell.isFlag) {
                        row = row  + "!";
                        flagCount++;
                    } else if (cell.isBlocked) {
                        row = row + "#";
                    } else {
                        row = row + "?";
                        unknownCount++;
                    }
                }
                board.push(row);
            }
            commit('setLoading', true);
            const response = await api.getProbability(state.bombs - flagCount, board);
            if (response.data.length > 0) {
                commit('updateWithProbabilities', response.data);
            } else if (unknownCount > 0) {
                commit('pushErrMsgToAllShowCells')
            }
            commit('setLoading', false);
        } else {
            console.log("Didn't request, have errors")
        }
    },

    resetBoardState({ commit }) {
        commit('setDebounce', false)
        commit('resetBoardState');
    },

    setDebounce({ commit }, debounceStatus) {
        commit('setDebounce', debounceStatus);
    },

    updateBomb({ commit, dispatch }, bombs) {
        commit('updateBomb', bombs)
        dispatch('updateProbability')
    }

};

const getters: GetterTree<Root, any> = {
    containsErrors(state) {
        for (const row of state.cells) {
            for (const cell of row) {
                if (cell.errMsg.length > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

export const minesweeper = {
    state: initialState,
    mutations: mutations,
    actions: actions,
    getters: getters
}





