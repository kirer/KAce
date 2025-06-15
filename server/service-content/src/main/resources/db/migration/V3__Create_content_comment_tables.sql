-- 内容评论表
CREATE TABLE IF NOT EXISTS content_comments (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id),
    user_id UUID NOT NULL,
    parent_id UUID,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_content_comments_parent FOREIGN KEY (parent_id) REFERENCES content_comments(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_content_comments_content_id ON content_comments(content_id);
CREATE INDEX idx_content_comments_user_id ON content_comments(user_id);
CREATE INDEX idx_content_comments_parent_id ON content_comments(parent_id);
CREATE INDEX idx_content_comments_status ON content_comments(status);
CREATE INDEX idx_content_comments_created_at ON content_comments(created_at);

-- 评论计数表（存储内容的评论数，用于提高性能）
CREATE TABLE IF NOT EXISTS content_comment_counts (
    content_id UUID PRIMARY KEY REFERENCES contents(id),
    total_count BIGINT NOT NULL DEFAULT 0,
    published_count BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL
);

-- 触发器函数：更新评论计数
CREATE OR REPLACE FUNCTION update_content_comment_counts()
RETURNS TRIGGER AS $$
BEGIN
    -- 新增评论
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO content_comment_counts (content_id, total_count, published_count, updated_at)
        VALUES (NEW.content_id, 1, CASE WHEN NEW.status = 'PUBLISHED' THEN 1 ELSE 0 END, NOW())
        ON CONFLICT (content_id) DO UPDATE
        SET 
            total_count = content_comment_counts.total_count + 1,
            published_count = content_comment_counts.published_count + CASE WHEN NEW.status = 'PUBLISHED' THEN 1 ELSE 0 END,
            updated_at = NOW();
        RETURN NEW;
    END IF;

    -- 更新评论状态
    IF (TG_OP = 'UPDATE') THEN
        -- 状态从非发布变为发布
        IF (OLD.status != 'PUBLISHED' AND NEW.status = 'PUBLISHED') THEN
            UPDATE content_comment_counts
            SET published_count = published_count + 1,
                updated_at = NOW()
            WHERE content_id = NEW.content_id;
        -- 状态从发布变为非发布
        ELSIF (OLD.status = 'PUBLISHED' AND NEW.status != 'PUBLISHED') THEN
            UPDATE content_comment_counts
            SET published_count = published_count - 1,
                updated_at = NOW()
            WHERE content_id = NEW.content_id;
        END IF;
        RETURN NEW;
    END IF;

    -- 删除评论
    IF (TG_OP = 'DELETE') THEN
        UPDATE content_comment_counts
        SET 
            total_count = total_count - 1,
            published_count = published_count - CASE WHEN OLD.status = 'PUBLISHED' THEN 1 ELSE 0 END,
            updated_at = NOW()
        WHERE content_id = OLD.content_id;
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 绑定触发器
CREATE TRIGGER trigger_update_content_comment_counts
AFTER INSERT OR UPDATE OF status OR DELETE ON content_comments
FOR EACH ROW EXECUTE FUNCTION update_content_comment_counts(); 