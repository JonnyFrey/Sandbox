<template>
  <v-container fill-height fluid>
    <v-row align="center" justify="center">
      <v-col class="text-center">
        <transition name="scale" appear>
          <span class="error_text blinking">
            &lt;<span class="not">Not </span>
            <span class="found">Found</span>/&gt;
          </span>
        </transition>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.error_text {
  color: white;
  font-size: 6vw;
  font-family: "Courier New", serif;
  text-blink: 1;
}

.error_text .not {
  color: hotpink;
}

.error_text .found {
  color: limegreen;
}

.blinking {
  animation: blinkingText 2s infinite;
}

@keyframes blinkingText {
  0% {
    opacity: 0;
  }
  75% {
    opacity: 0.7;
  }
  100% {
    opacity: 0;
  }
}
</style>
