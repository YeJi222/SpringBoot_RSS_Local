// Client, Controller, Service : DTO로 전달
// Service, DAO(Repository), DB : Entity로 전달

package com.readme.rss.data.service.Impl;

import com.readme.rss.data.dto.FrameworkDTO;
import com.readme.rss.data.entity.FrameworkEntity;
import com.readme.rss.data.handler.FrameworkHandler;
import com.readme.rss.data.service.FrameworkService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FrameworkServiceImpl implements FrameworkService {
    FrameworkHandler frameworkHandeler;

    @Autowired
    public FrameworkServiceImpl(FrameworkHandler frameworkHandler){
        this.frameworkHandeler = frameworkHandler;
    }

    // Service(Client) <-> Controller : DTO
    // Service <-> DAO(DB) : Entity
    @Override
    public FrameworkDTO saveFramework(String type, String name, String content){
        FrameworkEntity frameworkEntity = frameworkHandeler.saveFrameworkEntity(type, name, content);

        FrameworkDTO frameworkDTO = new FrameworkDTO(frameworkEntity.getType(), frameworkEntity.getName(), frameworkEntity.getContent());
        return frameworkDTO;
    }

    @Override
    public FrameworkDTO getFramework(String name){
        FrameworkEntity frameworkEntity = frameworkHandeler.getFrameworkEntity(name);

        FrameworkDTO frameworkDTO = new FrameworkDTO(frameworkEntity.getType(), frameworkEntity.getName(), frameworkEntity.getContent());
        return frameworkDTO;
    }

    @Override
    public List<String> getFrameworkNameList(){
        List<String> frameworkNameList= frameworkHandeler.getFrameworkNameList();

        return frameworkNameList;
    }

    @Override
    public String findContent(String name){
        return frameworkHandeler.findContent(name);
    }

}