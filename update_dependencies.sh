#!/bin/bash

# 更新所有服务模块的build.gradle.kts文件
for file in server/service-*/build.gradle.kts; do
  echo "更新 $file"
  # 替换 :service-common 为 :server:service-common
  sed -i '' 's/implementation(project(":service-common"))/implementation(project(":server:service-common"))/' "$file"
  
  # 如果有其他服务依赖，也需要更新
  sed -i '' 's/implementation(project(":service-\([^"]*\)"))/implementation(project(":server:service-\1"))/' "$file"
done

# 更新插件模块的build.gradle.kts文件
for file in server/service-plugin/plugin-*/build.gradle.kts; do
  if [ -f "$file" ]; then
    echo "更新 $file"
    # 替换 :service-plugin:plugin-api 为 :server:service-plugin:plugin-api
    sed -i '' 's/implementation(project(":service-plugin:plugin-\([^"]*\)"))/implementation(project(":server:service-plugin:plugin-\1"))/' "$file"
    # 替换 :service-common 为 :server:service-common
    sed -i '' 's/implementation(project(":service-common"))/implementation(project(":server:service-common"))/' "$file"
  fi
done

echo "所有依赖路径已更新" 