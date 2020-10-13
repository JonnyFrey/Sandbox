if (process.env.NODE_ENV === "production") {
  process.env.VUE_APP_BACKEND_URL =
    "https://0v3ak91mi9.execute-api.us-west-2.amazonaws.com/Prod";
}

process.env.VUE_APP_VERSION = require("./package.json").version;

module.exports = {
  transpileDependencies: ["vuetify"]
};
