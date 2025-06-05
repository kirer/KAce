package com.github.kirer.kace.automation.model

/**
 * 表示UI元素的详细信息
 */
data class ElementInfo(
    val id: String = "",
    val text: String = "",
    val desc: String = "",
    val className: String = "",
    val packageName: String = "",
    val bounds: Rect? = null,
    val depth: Int = 0,
    val index: Int = 0,
    
    // 布尔属性
    val checkable: Boolean = false,
    val checked: Boolean = false,
    val clickable: Boolean = false,
    val enabled: Boolean = false,
    val focusable: Boolean = false,
    val focused: Boolean = false,
    val scrollable: Boolean = false,
    val longClickable: Boolean = false,
    val selected: Boolean = false,
    val visible: Boolean = true,
    val password: Boolean = false,
    val editable: Boolean = false,
    
    // 平台特定属性，用于内部使用
    val platformNodeInfo: Any? = null
) {
    /**
     * 获取元素的中心点
     */
    val center: Point?
        get() = bounds?.center
    
    /**
     * 判断元素是否在屏幕上可见
     */
    val isVisibleOnScreen: Boolean
        get() = visible && bounds != null && bounds.width > 0 && bounds.height > 0
    
    /**
     * 获取元素的显示文本（优先使用text，如果为空则使用desc）
     */
    val displayText: String
        get() = text.ifEmpty { desc }
    
    override fun toString(): String {
        return "Element[id=$id, text=$text, class=$className]"
    }
} 