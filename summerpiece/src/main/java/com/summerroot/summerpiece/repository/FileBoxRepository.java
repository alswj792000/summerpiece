package com.summerroot.summerpiece.repository;

import com.summerroot.summerpiece.domain.FileBox;
import com.summerroot.summerpiece.domain.FileBoxStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileBoxRepository extends JpaRepository<FileBox, Long> {
    Page<FileBox> findAllByStatus(Pageable pageable, FileBoxStatus status);

    Page<FileBox> findByFileOriginNameContainingIgnoreCaseAndStatus(Pageable pageable, String fileOriginName, FileBoxStatus status);

}
