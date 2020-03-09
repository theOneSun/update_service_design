package com.sun.temp.update_manyi.repository;

import com.sun.temp.update_manyi.domain.ProjectInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjian.
 */
@Repository
public interface ProjectInfoMapper {
    List<ProjectInfo> getAll();
}
