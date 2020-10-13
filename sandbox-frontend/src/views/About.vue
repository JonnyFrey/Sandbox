<template>
  <div class="about">
    <v-container>
      <v-row>
        <v-col>
          <h1>About me?</h1>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <p>
            Hello to whoever is reading this, my name is Jonny, and welcome to
            my site. I made this site on a whim to put all the little pet
            projects that come to my mind. I usually don't do any web
            development, so this is my first time working with Vue.js, AWS
            CloudFront, and AWS API Gateway. I hope you enjoy the site!
          </p>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <h1>About the projects status?</h1>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-list class="list-col" ref="list">
            <v-list-item-group v-model="selected">
              <v-list-item v-for="(item, index) in projectItems" :key="index">
                <v-list-item-content>
                  <v-list-item-title>{{ item.name }}</v-list-item-title>
                </v-list-item-content>
                <v-list-item-icon>
                  <v-icon ref="icon">
                    mdi-chevron-left
                  </v-icon>
                </v-list-item-icon>
              </v-list-item>
            </v-list-item-group>
          </v-list>
        </v-col>
        <v-col v-if="!this.$vuetify.breakpoint.xs">
          <transition name="slide-fade">
            <Project v-show="shouldShowCard" ref="card" :previousProject="previousProject"/>
          </transition>
        </v-col>
      </v-row>
      <v-row v-if="this.$vuetify.breakpoint.xs">
        <v-col>
          <transition name="slide-up-fade">
            <Project v-show="shouldShowCard" ref="card" :previousProject="previousProject"/>
          </transition>
        </v-col>
      </v-row>
      <transition name="slide-fade">
        <div class="foot" v-show="!this.$store.state.moving">
          Version {{ versionNumber }}
        </div>
      </transition>
    </v-container>
  </div>
</template>

<script lang="ts">
import {Component, Vue, Watch} from "vue-property-decorator";
import anime from "animejs/lib/anime.es.js";
import Project from "@/components/Project.vue";

export interface ProjectMeta {
  name: string;
  complete: boolean;
  description: string;
  features: Feature[];
}

export interface Feature {
  name: string;
  complete: boolean;
  bug: boolean;
}

@Component({
  components: {
    Project
  }
})
export default class About extends Vue {
  private selected: number = null;
  private previousSelected: number = null;
  private previousProject: ProjectMeta | null = null;
  private showCard = false;
  private animation;
  private versionNumber = process.env.VUE_APP_VERSION;
  private projectItems: ProjectMeta[] = [
    {
      name: "Home Page",
      complete: true,
      description:
          "Get the front screen set with the old website look for nostalgic reasons",
      features: [
        { name: "Add more details/animations to the projects in the About pages", complete: true, bug: false },
        { name: "Animate Splash Page", complete: false, bug: false }
      ]
    },
    {
      name: "Minesweeper Heatmap",
      complete: true,
      description:
        "A tool that, when you input the current state of a minesweeper board, it will calculate the likely hood of each unclicked squares being bombs.",
      features: [
        { name: "Change cell selection to a universal swapping strategy", complete: false, bug: false },
        { name: "Add instructions and explaination screen", complete: false, bug: false },
        { name: "Fix x overflow issue", complete: false, bug: true },
        { name: "Design mobile perspective", complete: false, bug: false }
      ]
    },
    {
      name: "Sunshine Tracker",
      complete: false,
      description:
          "This is a simple manual collectibles tracker for the game Super Mario Sunshine to assist in fully complete the game.",
      features: []
    },
    {
      name: "Nonogram Solver",
      complete: false,
      description:
          "A tool that, when you input the current state of a nonogram board, it will solve the board.",
      features: []
    },
    {
      name: "Hearthstone Statistics",
      complete: false,
      description:
          "A tool that gives you statistics based on the current state of hearthstone",
      features: []
    },
    {
      name: "Hearthstone Deck Randomizer",
      complete: false,
      description:
          'A tool that will generate a random "okayish" deck that you can import into hearthstone.',
      features: []
    }
  ];

  mounted() {
    if (!this.$vuetify.breakpoint.xs) {
      anime.set((this.$refs.list as Vue).$el, {width: "200%"});
    }
  }

  get shouldShowCard() {
    if (this.$vuetify.breakpoint.xs) {
      return this.showCard;
    }
    return this.showCard && this.animation != null && this.animation.completed;
  }

  @Watch("selected")
  listenToDetailClick() {
    const target = (this.$refs.list as Vue);
    if (this.selected != null) {
      if (
          this.previousProject == null ||
          this.previousProject.name == this.projectItems[this.selected].name
      ) {
        if (this.$vuetify.breakpoint.xs) {
          this.showCard = true;
          this.previousProject = this.projectItems[this.selected];
        } else {
          const ani = anime({
            targets: target.$el,
            width: "100%",
            easing: "easeInOutBack",
            duration: 1000
          });
          ani.finished.then(() => {
            this.showCard = true;
            this.previousProject = this.projectItems[this.selected];
          });
          this.animation = ani;

        }
      } else if (this.showCard) {
        const card = ((this.$refs.card as Vue).$el as HTMLElement)
        anime
            .timeline({
              targets: card
            })
            .add({
              translateY: 100,
              opacity: 0,
              easing: "easeInOutBack",
              duration: 500,
              complete: () => {
                this.previousProject = this.projectItems[this.selected];
              }
            })
            .add({
              translateY: 0,
              opacity: 1,
              easing: "easeInOutBack",
              duration: 500
            })
            .finished.then(() => {
          card.style.transform = "";
          card.style.opacity = "";
        });
      }
      anime({
        targets: (this.$refs.icon as Vue[])[this.selected].$el,
        rotate: 540,
        easing: "easeInOutBack",
        duration: 1000
      });
    } else {
      this.showCard = false;
      if (!this.$vuetify.breakpoint.xs) {
        const ani  = anime({
          targets: target.$el,
          width: "200%",
          easing: "easeInOutBack",
          duration: 1000
        });
        ani.finished.then(() => {
          this.showCard = false;
          this.previousProject = null;
        });
        this.animation = ani;
      }
    }
    if (this.previousSelected != null) {
      anime({
        targets: (this.$refs.icon as Vue[])[this.previousSelected].$el,
        rotate: 0,
        easing: "easeInOutBack",
        duration: 1000
      });
    }
    this.previousSelected = this.selected;
  }
}
</script>

<style scoped>
.slide-fade-enter {
  transform: translateX(200px);
  opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.5s ease;
}

.slide-fade-leave-to {
  transform: translateX(200px);
  opacity: 0;
}

.slide-up-fade-enter {
  transform: translateY(200px);
  opacity: 0;
}

.slide-up-fade-enter-active,
.slide-up-fade-leave-active {
  transition: all 0.5s ease;
}

.slide-up-fade-leave-to {
  transform: translateY(200px);
  opacity: 0;
}

.foot {
  left: 0 !important;
  bottom: 0 !important;
  width: 100%;
  text-align: right;
  padding-bottom: 10px;
  padding-right: 20px;
  position: fixed;
}
</style>
