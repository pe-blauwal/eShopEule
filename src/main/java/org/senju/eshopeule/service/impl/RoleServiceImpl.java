package org.senju.eshopeule.service.impl;

import lombok.RequiredArgsConstructor;
import org.senju.eshopeule.dto.RoleDTO;
import org.senju.eshopeule.exceptions.NotFoundException;
import org.senju.eshopeule.mappers.RoleMapper;
import org.senju.eshopeule.model.user.Permission;
import org.senju.eshopeule.model.user.Role;
import org.senju.eshopeule.repository.jpa.RoleRepository;
import org.senju.eshopeule.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.senju.eshopeule.constant.exceptionMessage.RoleExceptionMsg.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Cacheable(value = "roleCache", key = "#id")
    public RoleDTO getById(String id) throws NotFoundException {
        return roleMapper.convertToDTO(
                roleRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(ROLE_NOT_EXISTS_WITH_NAME_MSG)
                )
        );
    }

    @Override
    public void createNewRole(RoleDTO role) {
        roleRepository.save(roleMapper.convertToEntity(role));
    }

    @Override
    @CachePut(value = "roleCache", key = "#dto.id")
    public RoleDTO updateRole(RoleDTO dto) throws NotFoundException {
        Role loadedRole = roleRepository.findById(dto.getId()).orElseThrow(
                () -> new NotFoundException(
                        String.format(ROLE_NOT_EXISTS_WITH_ID_MSG, dto.getId()))
        );
        loadedRole = roleMapper.updateFromDto(dto, loadedRole);
        return roleMapper.convertToDTO(roleRepository.save(roleMapper.updateFromDto(dto, loadedRole)));
    }

    @Override
    @CacheEvict(value = "roleCache", key = "#id")
    public void deleteById(String id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<RoleDTO> getAllRole() {
        return roleRepository.findAll().stream()
                .map(roleMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> getAllStaffRole() {
        return roleRepository.getAllStaffRole().stream()
                .map(roleMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Role bootstrapRole(String name, Collection<Permission> permissions) {
        var role = roleRepository.findByName(name).orElse(null);
        if (role == null) {
            role = new Role(name, permissions);
            return roleRepository.save(role);
        }
        return role;
    }
}
