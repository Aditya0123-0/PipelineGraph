package com.pipelinegraph.service;

import com.pipelinegraph.dto.SystemRequest;
import com.pipelinegraph.dto.SystemResponse;
import com.pipelinegraph.exception.NodeNotFoundException;
import com.pipelinegraph.model.SystemNode;
import com.pipelinegraph.repository.SystemRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemService {
    private final SystemRepository systemRepository;

    public SystemService(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    public List<SystemResponse> getAllSystems() {
        return systemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public SystemResponse getSystemById(String id) {
        return systemRepository.findById(id).map(this::toResponse).orElseThrow(()-> new NodeNotFoundException("System: " ,"id"));
    }





    //Helper method
    private SystemResponse toResponse(SystemNode systemNode) {
        return new SystemResponse(systemNode.getId(), systemNode.getName(), systemNode.getType(), systemNode.getCriticality());
    }


    public SystemResponse createSystem(@Valid SystemRequest systemRequest) {
        SystemNode systemNode = new SystemNode(
                systemRequest.id(),
                systemRequest.name(),
                systemRequest.type(),
                systemRequest.criticality()
        );

        SystemNode savedSystemNode = systemRepository.save(systemNode);
        return toResponse(savedSystemNode);
    }
}