import { useDark, useToggle } from '@vueuse/core'
import { useConfig } from '@/store/modules/layout'
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'

const isDark = useDark({
  onChanged(dark) {
    nextTick(() => {
      const config = useConfig()
      updateHtmlDarkClass(dark)
      config.setLayout('isDark', dark)
      config.onSetLayoutColor()
    })
  },
})

/**
 * 切换暗黑模式
 */
const toggleDark = useToggle(isDark)

/**
 * 切换当前页面的暗黑模式
 */
export function togglePageDark(val) {
  const config = useConfig()
  const isDark = ref(config.layout.isDark)
  onMounted(() => {
    if (isDark.value !== val) updateHtmlDarkClass(val)
  })
  onUnmounted(() => {
    updateHtmlDarkClass(isDark.value)
  })
  watch(
    () => config.layout.isDark,
    (newVal) => {
      isDark.value = newVal
      if (isDark.value !== val) updateHtmlDarkClass(val)
    }
  )
}

export function updateHtmlDarkClass(val) {
  const htmlEl = document.getElementsByTagName('html')[0]
  if (val) {
    htmlEl.setAttribute('class', 'dark')
  } else {
    htmlEl.setAttribute('class', '')
  }
}

export default toggleDark
