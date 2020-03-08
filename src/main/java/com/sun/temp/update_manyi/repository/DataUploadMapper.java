package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.DataUpload;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface DataUploadMapper {
    List<DataUpload> getAll();
}
