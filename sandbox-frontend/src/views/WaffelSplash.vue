<template>
  <v-container fill-height fluid>
    <v-row align="center" justify="center">
      <v-col class="text-center">
        <transition name="scale" appear>
          <span class="hello_text">
            &lt;<span class="hello">Hello </span>
            <span class="message">Waffel</span>/&gt;
          </span>
        </transition>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.hello_text {
  color: white;
  font-size: 6vw;
  font-family: "Courier New", serif;
}

.hello_text .hello {
  color: hotpink;
}

.hello_text .message {
  color: limegreen;
}

.scale-enter,
.scale-leave-to {
  opacity: 0;
}

.scale-enter-to,
.scale-leave {
}

.scale-enter-active,
.scale-leave-active {
  transition: all 3s ease;
}
</style>
