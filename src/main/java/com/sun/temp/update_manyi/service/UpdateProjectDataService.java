package com.sun.temp.update_manyi.service;

import com.sun.temp.update_manyi.domain.DataUpload;
import com.sun.temp.update_manyi.repository.DataUploadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 更新数据中的时间
 *
 * @author sunjian.
 */
@Service
public class UpdateProjectDataService {
    private final DataUploadMapper mapper;

    @Autowired
    public UpdateProjectDataService(DataUploadMapper mapper) {
        this.mapper = mapper;
    }

    //生成sql
    public String generateSql() {
        final List<DataUpload> all = mapper.getAll();

        /*
        UPDATE project_data
            SET created_date = CASE
                WHEN import_batch = 'be6ea95a-fdfb-494c-a1d6-0c8064bd2666' THEN '1999-05-23 12:19:00.000000'
                WHEN import_batch = 'fc3dc106-57ce-4846-a07c-119ab8ee173a' THEN '1990-11-01 11:58:07.000'
                ELSE created_date END,updated_date = created_date;
         */

        StringBuilder sqlBuilder = new StringBuilder("UPDATE project_data SET created_date = CASE ");

        for (DataUpload dataUpload : all) {
            sqlBuilder.append("WHEN import_batch = '").append(dataUpload.getImportBatch()).append("' THEN '")
            .append(dataUpload.getCreatedTime()).append("' ");
        }

        sqlBuilder.append("ELSE created_date END, updated_date = created_date;");
        return sqlBuilder.toString();
    }
}
