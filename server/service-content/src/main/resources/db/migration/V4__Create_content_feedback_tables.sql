-- 内容反馈表
CREATE TABLE IF NOT EXISTS content_feedbacks (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id),
    user_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    value INT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT unique_user_content_feedback_type UNIQUE (content_id, user_id, type)
);

-- 创建索引
CREATE INDEX idx_content_feedbacks_content_id ON content_feedbacks(content_id);
CREATE INDEX idx_content_feedbacks_user_id ON content_feedbacks(user_id);
CREATE INDEX idx_content_feedbacks_type ON content_feedbacks(type);
CREATE INDEX idx_content_feedbacks_created_at ON content_feedbacks(created_at);

-- 内容反馈聚合表 (用于缓存统计结果，提高性能)
CREATE TABLE IF NOT EXISTS content_feedback_stats (
    content_id UUID PRIMARY KEY REFERENCES contents(id),
    like_count INT NOT NULL DEFAULT 0,
    rating_count INT NOT NULL DEFAULT 0,
    rating_sum INT NOT NULL DEFAULT 0,
    rating_avg DECIMAL(3,2),
    helpful_count INT NOT NULL DEFAULT 0,
    not_helpful_count INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL
);

-- 创建反馈类型的枚举类型
CREATE TYPE feedback_type AS ENUM ('LIKE', 'RATING', 'HELPFUL', 'REACTION');

-- 触发器函数：更新反馈统计
CREATE OR REPLACE FUNCTION update_content_feedback_stats()
RETURNS TRIGGER AS $$
DECLARE
    _content_id UUID;
    _like_count INT;
    _rating_count INT;
    _rating_sum INT;
    _rating_avg DECIMAL(3,2);
    _helpful_count INT;
    _not_helpful_count INT;
BEGIN
    -- 确定内容ID
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        _content_id := NEW.content_id;
    ELSE
        _content_id := OLD.content_id;
    END IF;
    
    -- 统计点赞数
    SELECT COUNT(*) INTO _like_count
    FROM content_feedbacks
    WHERE content_id = _content_id AND type = 'LIKE' AND value > 0;
    
    -- 统计评分数据
    SELECT COUNT(*), COALESCE(SUM(value), 0) INTO _rating_count, _rating_sum
    FROM content_feedbacks
    WHERE content_id = _content_id AND type = 'RATING';
    
    -- 计算平均评分
    IF _rating_count > 0 THEN
        _rating_avg := (_rating_sum::DECIMAL / _rating_count);
    ELSE
        _rating_avg := NULL;
    END IF;
    
    -- 统计有用/无用数
    SELECT 
        COUNT(*) FILTER (WHERE value > 0),
        COUNT(*) FILTER (WHERE value < 0)
    INTO _helpful_count, _not_helpful_count
    FROM content_feedbacks
    WHERE content_id = _content_id AND type = 'HELPFUL';
    
    -- 更新或插入统计结果
    INSERT INTO content_feedback_stats 
    (content_id, like_count, rating_count, rating_sum, rating_avg, helpful_count, not_helpful_count, updated_at)
    VALUES 
    (_content_id, _like_count, _rating_count, _rating_sum, _rating_avg, _helpful_count, _not_helpful_count, NOW())
    ON CONFLICT (content_id) DO UPDATE
    SET 
        like_count = EXCLUDED.like_count,
        rating_count = EXCLUDED.rating_count,
        rating_sum = EXCLUDED.rating_sum,
        rating_avg = EXCLUDED.rating_avg,
        helpful_count = EXCLUDED.helpful_count,
        not_helpful_count = EXCLUDED.not_helpful_count,
        updated_at = NOW();
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 绑定触发器
CREATE TRIGGER trigger_update_content_feedback_stats
AFTER INSERT OR UPDATE OR DELETE ON content_feedbacks
FOR EACH ROW EXECUTE FUNCTION update_content_feedback_stats(); 