package com.summerroot.summerpiece.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class FileBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @ManyToOne(targetEntity = Board.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @JsonManagedReference
    private Board board;

    @Column(nullable = false)
    private String fileOriginName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileSavedName;

    private Long fileSize;

    // @CreationTimestamp

    private String fileUpdateDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private String fileType;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonManagedReference
    private Member fileUploadMember;

    @Enumerated(EnumType.STRING)
    private FileBoxStatus status;

    @Builder
    public FileBox(Long id, String fileOriginName, String filePath, String fileSavedName,
                   Long fileSize, String fileUpdateDate, LocalDateTime modifiedDate, String fileType,
                   Member fileUploadMember, FileBoxStatus status, Board board){
        this.id = id;
        this.fileOriginName = fileOriginName;
        this.filePath = filePath;
        this.fileSavedName = fileSavedName;
        this.fileSize = fileSize;
        this.fileUpdateDate = fileUpdateDate;
        this.modifiedDate = modifiedDate;
        this.fileType = fileType;
        this.fileUploadMember = fileUploadMember;
        this.status = status;
        this.board = board;
    }

    public void deleteFile(){
        this.status = FileBoxStatus.valueOf(FileBoxStatus.N.name());
    }

}
