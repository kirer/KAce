package com.github.kirer.kace.automation

import com.github.kirer.kace.automation.model.ElementInfo
import com.github.kirer.kace.automation.model.Rect

/**
 * UI元素选择器，提供流畅的链式API来筛选UI元素
 */
class ElementSelector {
    private val filters = mutableListOf<(ElementInfo) -> Boolean>()
    
    /**
     * 添加自定义过滤条件
     */
    fun addFilter(filter: (ElementInfo) -> Boolean): ElementSelector {
        filters.add(filter)
        return this
    }
    
    // 第一类：文本相关的筛选条件
    
    /**
     * 通过ID精确匹配
     */
    fun id(id: String): ElementSelector {
        filters.add { it.id == id }
        return this
    }
    
    /**
     * 通过ID包含匹配
     */
    fun idContains(str: String): ElementSelector {
        filters.add { it.id.contains(str) }
        return this
    }
    
    /**
     * 通过ID前缀匹配
     */
    fun idStartsWith(prefix: String): ElementSelector {
        filters.add { it.id.startsWith(prefix) }
        return this
    }
    
    /**
     * 通过ID后缀匹配
     */
    fun idEndsWith(suffix: String): ElementSelector {
        filters.add { it.id.endsWith(suffix) }
        return this
    }
    
    /**
     * 通过ID正则匹配
     */
    fun idMatches(regex: String): ElementSelector {
        val pattern = Regex(regex)
        filters.add { it.id.matches(pattern) }
        return this
    }
    
    /**
     * 通过文本精确匹配
     */
    fun text(text: String): ElementSelector {
        filters.add { it.text == text }
        return this
    }
    
    /**
     * 通过文本包含匹配
     */
    fun textContains(str: String): ElementSelector {
        filters.add { it.text.contains(str) }
        return this
    }
    
    /**
     * 通过文本前缀匹配
     */
    fun textStartsWith(prefix: String): ElementSelector {
        filters.add { it.text.startsWith(prefix) }
        return this
    }
    
    /**
     * 通过文本后缀匹配
     */
    fun textEndsWith(suffix: String): ElementSelector {
        filters.add { it.text.endsWith(suffix) }
        return this
    }
    
    /**
     * 通过文本正则匹配
     */
    fun textMatches(regex: String): ElementSelector {
        val pattern = Regex(regex)
        filters.add { it.text.matches(pattern) }
        return this
    }
    
    /**
     * 通过描述精确匹配
     */
    fun desc(desc: String): ElementSelector {
        filters.add { it.desc == desc }
        return this
    }
    
    /**
     * 通过描述包含匹配
     */
    fun descContains(str: String): ElementSelector {
        filters.add { it.desc.contains(str) }
        return this
    }
    
    /**
     * 通过类名精确匹配
     */
    fun className(className: String): ElementSelector {
        filters.add { it.className == className }
        return this
    }
    
    /**
     * 通过类名包含匹配
     */
    fun classNameContains(str: String): ElementSelector {
        filters.add { it.className.contains(str) }
        return this
    }
    
    /**
     * 通过包名精确匹配
     */
    fun packageName(packageName: String): ElementSelector {
        filters.add { it.packageName == packageName }
        return this
    }
    
    /**
     * 通过包名包含匹配
     */
    fun packageNameContains(str: String): ElementSelector {
        filters.add { it.packageName.contains(str) }
        return this
    }
    
    /**
     * 通过边界精确匹配
     */
    fun bounds(rect: Rect): ElementSelector {
        filters.add { it.bounds == rect }
        return this
    }
    
    /**
     * 通过边界包含匹配
     */
    fun boundsContains(rect: Rect): ElementSelector {
        filters.add { it.bounds?.contains(rect) == true }
        return this
    }
    
    /**
     * 通过边界内部匹配
     */
    fun boundsInside(rect: Rect): ElementSelector {
        filters.add { rect.contains(it.bounds ?: return@add false) }
        return this
    }
    
    // 第二类：布尔属性筛选条件
    
    /**
     * 可点击
     */
    fun clickable(b: Boolean = true): ElementSelector {
        filters.add { it.clickable == b }
        return this
    }
    
    /**
     * 可长按
     */
    fun longClickable(b: Boolean = true): ElementSelector {
        filters.add { it.longClickable == b }
        return this
    }
    
    /**
     * 可滚动
     */
    fun scrollable(b: Boolean = true): ElementSelector {
        filters.add { it.scrollable == b }
        return this
    }
    
    /**
     * 可用状态
     */
    fun enabled(b: Boolean = true): ElementSelector {
        filters.add { it.enabled == b }
        return this
    }
    
    /**
     * 选中状态
     */
    fun selected(b: Boolean = true): ElementSelector {
        filters.add { it.selected == b }
        return this
    }
    
    /**
     * 可检查状态
     */
    fun checkable(b: Boolean = true): ElementSelector {
        filters.add { it.checkable == b }
        return this
    }
    
    /**
     * 已检查状态
     */
    fun checked(b: Boolean = true): ElementSelector {
        filters.add { it.checked == b }
        return this
    }
    
    /**
     * 可见状态
     */
    fun visible(b: Boolean = true): ElementSelector {
        filters.add { it.visible == b }
        return this
    }
    
    /**
     * 是否为密码输入框
     */
    fun password(b: Boolean = true): ElementSelector {
        filters.add { it.password == b }
        return this
    }
    
    /**
     * 可编辑状态
     */
    fun editable(b: Boolean = true): ElementSelector {
        filters.add { it.editable == b }
        return this
    }
    
    // 第三类：数值属性筛选条件
    
    /**
     * 深度匹配
     */
    fun depth(depth: Int): ElementSelector {
        filters.add { it.depth == depth }
        return this
    }
    
    /**
     * 索引匹配
     */
    fun index(index: Int): ElementSelector {
        filters.add { it.index == index }
        return this
    }
    
    /**
     * 应用所有过滤条件到指定元素
     */
    fun matches(element: ElementInfo): Boolean {
        return filters.all { it(element) }
    }
    
    /**
     * 过滤一个元素列表
     */
    fun filter(elements: List<ElementInfo>): List<ElementInfo> {
        return elements.filter { matches(it) }
    }
    
    /**
     * 清除所有过滤条件
     */
    fun reset(): ElementSelector {
        filters.clear()
        return this
    }
    
    /**
     * 获取过滤条件数量
     */
    val filterCount: Int
        get() = filters.size
} 