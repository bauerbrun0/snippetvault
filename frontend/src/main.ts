import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import Ripple from 'primevue/ripple'

const app = createApp(App)

app.use(router)
app.use(PrimeVue, {
  theme: {
    preset: Aura,
  },
  ripple: true,
})

app.directive('ripple', Ripple)

app.mount('#app')
