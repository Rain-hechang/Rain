<template>
  <div class="home">
    <el-card>
      <template #title>
        <span>关于</span>
      </template>
      <div>
        市场上各种后台脚手架，但是功能方面使用起来都不顺手，所以抽时间写了这个权限系统，二次开发非常方便，后续会一直更新！！！
      </div>
    </el-card>
    <el-card>
      <p>前端技术栈</p>
      <TextLink :text-arrs="technologyStacks"/>
      <p>后端技术栈</p>
      <TextLink :text-arrs="technologyStacksApi"/>
      <p>Rain项目地址: <a href="https://github.com/Rain-hechang/Rain"
                          class="on">https://github.com/Rain-hechang/Rain</a></p>
    </el-card>
  </div>
</template>

<script setup name="Index">
import TextLink from '@/BaseComponent/TextLink'
import CodeInfo from '@/BaseComponent/Code'
import {technologyStacks, technologyStacksApi, dependencies, projectDir} from './config'
</script>

<style scoped lang="scss">
.on {
  color: #409eff;
}

.on:hover {
  color: lightcoral;
}

.home {
  :deep(.el-card) {
    margin-bottom: 15px;
  }

  margin: var(--ba-main-space);
}

.description {
  margin-bottom: 20px;
}
</style>
