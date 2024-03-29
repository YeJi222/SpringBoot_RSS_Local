package com.readme.rss.data.dao;

import com.readme.rss.data.entity.ProjectEntity;
import java.util.List;

public interface ProjectDAO {
    ProjectEntity saveProject(ProjectEntity projectEntity);
    ProjectEntity getProject(String id);

    List<ProjectEntity> getController(String projectId);

    List<String> getIdAll();

    List<ProjectEntity> getFileContent(String id);

    String getFileContentByFileName(String id, String file_name);
}
