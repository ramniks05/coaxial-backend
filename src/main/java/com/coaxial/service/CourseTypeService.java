package com.coaxial.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.entity.CourseType;
import com.coaxial.repository.CourseTypeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CourseTypeService {
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    public List<CourseType> getAllCourseTypes(Boolean active) {
        if (active != null && active) {
            return courseTypeRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        } else {
            return courseTypeRepository.findAllByOrderByDisplayOrderAsc();
        }
    }
    
    public CourseType getCourseTypeById(Long id) {
        return courseTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course type not found with id: " + id));
    }
    
    public CourseType createCourseType(CourseType courseType) {
        // Check if name already exists
        if (courseTypeRepository.existsByNameIgnoreCase(courseType.getName())) {
            throw new IllegalArgumentException("Course type with name '" + courseType.getName() + "' already exists");
        }
        
        return courseTypeRepository.save(courseType);
    }
    
    public CourseType updateCourseType(Long id, CourseType courseType) {
        // Check if course type exists
        CourseType existingCourseType = getCourseTypeById(id);
        
        // Check if name already exists (excluding current record)
        if (courseTypeRepository.existsByNameIgnoreCaseAndIdNot(courseType.getName(), id)) {
            throw new IllegalArgumentException("Course type with name '" + courseType.getName() + "' already exists");
        }
        
        // Update fields
        existingCourseType.setName(courseType.getName());
        existingCourseType.setDescription(courseType.getDescription());
        existingCourseType.setStructureType(courseType.getStructureType());
        existingCourseType.setDisplayOrder(courseType.getDisplayOrder());
        existingCourseType.setIsActive(courseType.getIsActive());
        
        return courseTypeRepository.save(existingCourseType);
    }
    
    public void deleteCourseType(Long id) {
        // Check if course type exists
        getCourseTypeById(id);
        courseTypeRepository.deleteById(id);
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<CourseType> getCourseTypes(Boolean active, String search, Pageable pageable) {
        if (active != null && search != null && !search.isEmpty()) {
            return courseTypeRepository.findByIsActiveAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                active, search, search, pageable);
        } else if (active != null) {
            return courseTypeRepository.findByIsActive(active, pageable);
        } else if (search != null && !search.isEmpty()) {
            return courseTypeRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search, search, pageable);
        } else {
            return courseTypeRepository.findAll(pageable);
        }
    }
}
