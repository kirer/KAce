#!/bin/bash

# 重命名目录以符合service-前缀命名规则
echo "开始重命名目录..."

# 创建备份目录
mkdir -p backup

# 重命名common目录
if [ -d "common" ]; then
    echo "重命名 common -> service-common"
    cp -r common backup/
    mv common service-common
fi

# 重命名gateway目录
if [ -d "gateway" ]; then
    echo "重命名 gateway -> service-gateway"
    cp -r gateway backup/
    mv gateway service-gateway
fi

# 重命名plugins目录
if [ -d "plugins" ]; then
    echo "重命名 plugins -> service-plugin"
    cp -r plugins backup/
    mv plugins service-plugin
fi

# 确保所有服务目录都有service-前缀
for dir in *-service; do
    if [ -d "$dir" ]; then
        new_name="service-${dir%-service}"
        echo "重命名 $dir -> $new_name"
        cp -r "$dir" backup/
        mv "$dir" "$new_name"
    fi
done

echo "重命名完成！" 