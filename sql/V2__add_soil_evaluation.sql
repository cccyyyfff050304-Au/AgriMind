ALTER TABLE soil_test_record
    ADD COLUMN risk_level VARCHAR(30) NOT NULL DEFAULT 'UNKNOWN' COMMENT '综合风险等级：UNKNOWN/LOW/MEDIUM/HIGH' AFTER data_source,
    ADD KEY idx_soil_test_record_risk_level (risk_level);

ALTER TABLE soil_indicator_result
    ADD COLUMN min_value DECIMAL(12,4) DEFAULT NULL COMMENT '判断时标准最小值快照' AFTER measured_value,
    ADD COLUMN max_value DECIMAL(12,4) DEFAULT NULL COMMENT '判断时标准最大值快照' AFTER min_value,
    ADD COLUMN risk_level VARCHAR(30) NOT NULL DEFAULT 'LOW' COMMENT '单项风险等级：LOW/MEDIUM/HIGH' AFTER result_level,
    ADD KEY idx_indicator_result_risk_level (risk_level);
