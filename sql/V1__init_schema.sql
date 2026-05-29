-- AgriMind V1 initial MySQL schema.
-- Recommended database:
-- CREATE DATABASE IF NOT EXISTS agri_mind DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE agri_mind;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '登录用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值，不保存明文密码',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    role_code VARCHAR(30) NOT NULL DEFAULT 'USER' COMMENT '角色编码：ADMIN/USER',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1-启用，0-禁用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_phone (phone),
    KEY idx_sys_user_status (status),
    KEY idx_sys_user_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS field_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    owner_user_id BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
    field_name VARCHAR(100) NOT NULL COMMENT '地块名称',
    field_code VARCHAR(50) DEFAULT NULL COMMENT '地块编码',
    location VARCHAR(255) DEFAULT NULL COMMENT '地块位置描述',
    area_mu DECIMAL(10,2) DEFAULT NULL COMMENT '地块面积，单位：亩',
    soil_type VARCHAR(50) DEFAULT NULL COMMENT '土壤类型',
    longitude DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    latitude DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_field_info_code (field_code),
    KEY idx_field_info_owner (owner_user_id),
    KEY idx_field_info_soil_type (soil_type),
    KEY idx_field_info_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地块信息表';

CREATE TABLE IF NOT EXISTS crop_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    crop_name VARCHAR(100) NOT NULL COMMENT '作物名称',
    crop_code VARCHAR(50) DEFAULT NULL COMMENT '作物编码',
    crop_category VARCHAR(50) DEFAULT NULL COMMENT '作物分类',
    growth_cycle_days INT DEFAULT NULL COMMENT '生长周期天数',
    suitable_ph_min DECIMAL(4,2) DEFAULT NULL COMMENT '适宜pH最小值',
    suitable_ph_max DECIMAL(4,2) DEFAULT NULL COMMENT '适宜pH最大值',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_crop_info_code (crop_code),
    KEY idx_crop_info_name (crop_name),
    KEY idx_crop_info_category (crop_category),
    KEY idx_crop_info_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作物信息表';

CREATE TABLE IF NOT EXISTS soil_test_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    test_no VARCHAR(64) NOT NULL COMMENT '检测编号',
    field_id BIGINT UNSIGNED NOT NULL COMMENT '地块ID',
    crop_id BIGINT UNSIGNED DEFAULT NULL COMMENT '作物ID',
    sample_time DATETIME NOT NULL COMMENT '采样时间',
    sample_depth_cm DECIMAL(6,2) DEFAULT NULL COMMENT '采样深度，单位：厘米',
    ph_value DECIMAL(4,2) DEFAULT NULL COMMENT '土壤pH值',
    organic_matter_g_kg DECIMAL(8,2) DEFAULT NULL COMMENT '有机质，单位：g/kg',
    nitrogen_mg_kg DECIMAL(8,2) DEFAULT NULL COMMENT '有效氮，单位：mg/kg',
    phosphorus_mg_kg DECIMAL(8,2) DEFAULT NULL COMMENT '有效磷，单位：mg/kg',
    potassium_mg_kg DECIMAL(8,2) DEFAULT NULL COMMENT '速效钾，单位：mg/kg',
    moisture_percent DECIMAL(5,2) DEFAULT NULL COMMENT '含水率，单位：%',
    temperature_celsius DECIMAL(5,2) DEFAULT NULL COMMENT '土壤温度，单位：摄氏度',
    conductivity_us_cm DECIMAL(10,2) DEFAULT NULL COMMENT '电导率，单位：us/cm',
    tester_name VARCHAR(50) DEFAULT NULL COMMENT '检测人员',
    data_source VARCHAR(30) NOT NULL DEFAULT 'MANUAL' COMMENT '数据来源：MANUAL/DEVICE/IMPORT',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_soil_test_record_no (test_no),
    KEY idx_soil_test_record_field (field_id),
    KEY idx_soil_test_record_crop (crop_id),
    KEY idx_soil_test_record_sample_time (sample_time),
    KEY idx_soil_test_record_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='土壤检测记录表';

CREATE TABLE IF NOT EXISTS soil_indicator_standard (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    indicator_code VARCHAR(50) NOT NULL COMMENT '指标编码，如PH/NITROGEN/PHOSPHORUS/POTASSIUM',
    indicator_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    crop_id BIGINT UNSIGNED DEFAULT NULL COMMENT '适用作物ID，为空表示通用标准',
    soil_type VARCHAR(50) DEFAULT NULL COMMENT '适用土壤类型，为空表示通用标准',
    level_code VARCHAR(30) NOT NULL COMMENT '等级编码：LOW/NORMAL/HIGH',
    min_value DECIMAL(12,4) DEFAULT NULL COMMENT '标准最小值，空表示无下限',
    max_value DECIMAL(12,4) DEFAULT NULL COMMENT '标准最大值，空表示无上限',
    unit VARCHAR(30) DEFAULT NULL COMMENT '指标单位',
    suggestion VARCHAR(500) DEFAULT NULL COMMENT '建议说明',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_indicator_standard_code (indicator_code),
    KEY idx_indicator_standard_scope (crop_id, soil_type),
    KEY idx_indicator_standard_level (level_code),
    KEY idx_indicator_standard_status (status),
    KEY idx_indicator_standard_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='土壤指标标准表';

CREATE TABLE IF NOT EXISTS soil_indicator_result (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    record_id BIGINT UNSIGNED NOT NULL COMMENT '土壤检测记录ID',
    standard_id BIGINT UNSIGNED DEFAULT NULL COMMENT '匹配的指标标准ID',
    indicator_code VARCHAR(50) NOT NULL COMMENT '指标编码',
    indicator_name VARCHAR(100) NOT NULL COMMENT '指标名称',
    measured_value DECIMAL(12,4) DEFAULT NULL COMMENT '实测值',
    unit VARCHAR(30) DEFAULT NULL COMMENT '指标单位',
    result_level VARCHAR(30) NOT NULL COMMENT '判断等级：LOW/NORMAL/HIGH/UNKNOWN',
    result_text VARCHAR(100) DEFAULT NULL COMMENT '判断结果描述',
    suggestion VARCHAR(500) DEFAULT NULL COMMENT '改良建议',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_indicator_result_record (record_id),
    KEY idx_indicator_result_standard (standard_id),
    KEY idx_indicator_result_code (indicator_code),
    KEY idx_indicator_result_level (result_level),
    KEY idx_indicator_result_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='土壤指标判断结果表';

CREATE TABLE IF NOT EXISTS ai_analysis_report (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    record_id BIGINT UNSIGNED NOT NULL COMMENT '土壤检测记录ID',
    user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '生成报告的用户ID',
    report_title VARCHAR(200) NOT NULL COMMENT '报告标题',
    summary VARCHAR(1000) DEFAULT NULL COMMENT '报告摘要',
    report_content MEDIUMTEXT DEFAULT NULL COMMENT 'AI分析报告正文',
    model_name VARCHAR(100) DEFAULT NULL COMMENT '模型名称',
    report_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '报告状态：DRAFT/GENERATED/FAILED',
    generated_time DATETIME DEFAULT NULL COMMENT '生成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_ai_report_record (record_id),
    KEY idx_ai_report_user (user_id),
    KEY idx_ai_report_status (report_status),
    KEY idx_ai_report_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI土壤分析报告表';

CREATE TABLE IF NOT EXISTS ai_call_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '调用用户ID',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型：SOIL_REPORT/RAG_QA/AGENT',
    request_id VARCHAR(100) DEFAULT NULL COMMENT '请求ID',
    provider VARCHAR(50) DEFAULT NULL COMMENT '模型服务商',
    model_name VARCHAR(100) DEFAULT NULL COMMENT '模型名称',
    prompt_tokens INT DEFAULT NULL COMMENT '输入token数',
    completion_tokens INT DEFAULT NULL COMMENT '输出token数',
    total_tokens INT DEFAULT NULL COMMENT '总token数',
    cost_amount DECIMAL(12,6) DEFAULT NULL COMMENT '预估调用成本',
    success TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功：1-成功，0-失败',
    error_message VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    request_time DATETIME DEFAULT NULL COMMENT '请求时间',
    response_time DATETIME DEFAULT NULL COMMENT '响应时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_ai_call_log_user (user_id),
    KEY idx_ai_call_log_business (business_type),
    KEY idx_ai_call_log_request (request_id),
    KEY idx_ai_call_log_time (request_time),
    KEY idx_ai_call_log_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI调用日志表';

CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    doc_title VARCHAR(200) NOT NULL COMMENT '文档标题',
    doc_type VARCHAR(50) DEFAULT NULL COMMENT '文档类型：PDF/WORD/TEXT/WEB',
    source_type VARCHAR(50) NOT NULL DEFAULT 'UPLOAD' COMMENT '来源类型：UPLOAD/URL/MANUAL',
    source_uri VARCHAR(500) DEFAULT NULL COMMENT '来源地址或文件路径',
    file_hash VARCHAR(128) DEFAULT NULL COMMENT '文件哈希，用于去重',
    chunk_count INT NOT NULL DEFAULT 0 COMMENT '文本片段数量',
    parse_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '解析状态：PENDING/PARSED/FAILED',
    uploaded_by BIGINT UNSIGNED DEFAULT NULL COMMENT '上传用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_knowledge_doc_type (doc_type),
    KEY idx_knowledge_doc_hash (file_hash),
    KEY idx_knowledge_doc_status (parse_status),
    KEY idx_knowledge_doc_uploaded_by (uploaded_by),
    KEY idx_knowledge_doc_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    document_id BIGINT UNSIGNED NOT NULL COMMENT '知识库文档ID',
    chunk_index INT NOT NULL COMMENT '片段序号',
    content TEXT NOT NULL COMMENT '文本片段内容',
    content_hash VARCHAR(128) DEFAULT NULL COMMENT '文本片段哈希',
    token_count INT DEFAULT NULL COMMENT 'token数量估算',
    embedding_id VARCHAR(100) DEFAULT NULL COMMENT '向量存储ID',
    metadata_json JSON DEFAULT NULL COMMENT '片段元数据JSON',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_knowledge_chunk_order (document_id, chunk_index),
    KEY idx_knowledge_chunk_document (document_id),
    KEY idx_knowledge_chunk_hash (content_hash),
    KEY idx_knowledge_chunk_embedding (embedding_id),
    KEY idx_knowledge_chunk_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文本片段表';

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '操作用户ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    module_name VARCHAR(100) DEFAULT NULL COMMENT '业务模块',
    operation_name VARCHAR(100) DEFAULT NULL COMMENT '操作名称',
    request_method VARCHAR(20) DEFAULT NULL COMMENT '请求方法',
    request_uri VARCHAR(255) DEFAULT NULL COMMENT '请求URI',
    client_ip VARCHAR(64) DEFAULT NULL COMMENT '客户端IP',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '浏览器或客户端标识',
    request_params JSON DEFAULT NULL COMMENT '请求参数JSON，避免记录敏感信息',
    response_status INT DEFAULT NULL COMMENT '响应状态码',
    success TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功：1-成功，0-失败',
    error_message VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    duration_ms INT DEFAULT NULL COMMENT '耗时，单位：毫秒',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_operation_log_user (user_id),
    KEY idx_operation_log_module (module_name),
    KEY idx_operation_log_time (operation_time),
    KEY idx_operation_log_success (success),
    KEY idx_operation_log_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
